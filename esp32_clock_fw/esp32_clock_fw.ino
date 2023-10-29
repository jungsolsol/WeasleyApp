// client side code 
#include <WiFi.h>
// #include <WifiClient.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <PubSubClient.h>

#define DEBUG 1

#define EEPROM_START_ADDRESS           0
#define EEPROM_SSID_ADDRESS            4
#define EEPROM_PASSWORD_ADDRESS        68
#define EEPROM_URL_ADDRESS             132
#define EEPROM_KEY_ADDRESS             388
#define EEPROM_WIFIRETRY_ADDRESS       420
#define EEPROM_FACTORYRST_ADDRESS      428
#define EEPROM_BROKER_ADDRESS          436
#define EEPROM_MQTTPORT_ADDRESS        692
#define EEPROM_MQTTID_ADDRESS          700
#define EEPROM_MQTTPASSWORD_ADDRESS    764
#define EEPROM_END_ADDRESS             4095

String ssid;
String password;
String serverUrl;
String uniquekey;

unsigned int factory_reset=0;
unsigned long previousMillis = 0;
const long interval = 10000;

StaticJsonDocument<200> doc;
unsigned int clock_point[4]={0,};

String brokerUrl;
unsigned int mqttport=1883;
String mqttid;
String mqttpw;

int connect=0;
unsigned int wifiretrytimes;
byte mac[6];

IPAddress server;

void mqtt_callback(char* topic, byte* payload, unsigned int length);

WiFiClient wfclient;
PubSubClient client(wfclient);

bool auth_flag = false;


void setup() {
  mcuinit();
    connect=connectToWiFi();

#ifdef DEBUG
  if(connect < 0){
    Serial.println("DEBUG: Failed Connect to WiFi");
  } else {
    Serial.println("DEBUG: Success Connect to WiFi");
    Serial.print("DEBUG: Connect Try time: ");
    Serial.println(connect,DEC);
  }
#endif
  
  // set mqtt client configuration
  client.setClient(wfclient);
  client.setServer(server, mqttport);
  client.setCallback(mqtt_callback);

#ifdef DEBUG
  auth_flag = true;
#elif
  auth_flag = false;
#endif
}

void loop() {
  unsigned long currentMillis = millis();

  client.loop();
  Serialcommand();
  if(connect>0){
    if (currentMillis - previousMillis >= interval) {
      if(auth_flag){
#ifdef easteregg
        HTTPClient http;
        String routeserver = "?location2";
        http.begin(serverUrl+routeserver);
        http.addHeader("Content-Type", "application/json");

        http.begin(serverUrl);

        char temp[64]={0,};
        sprintf(temp,"{\"ack\": true}");
        
        String msg = temp;

        int httpResponseCode = http.POST(msg);

        if (httpResponseCode > 0) {
          String response = http.getString();
          Serial.println("HTTP Response Code: " + String(httpResponseCode));
          // Serial.println("Response: " + response);
          char tempdata[response.length()];
          response.toCharArray(tempdata, response.length()+1);
          Serial.print("Received Response: ");
          Serial.println(tempdata);
          
          auto error = deserializeJson(doc, tempdata);
          if (error) {
              Serial.print(("deserializeJson() failed with code "));
              Serial.println(error.c_str());
              return;
          }

          double latitude = doc["usernumber"];
          double longitude = doc["point"];
          Serial.println(latitude, 6);
          Serial.println(longitude, 6);   
        } else {
          Serial.println("Error on HTTP request");
        }

        http.end();
#endif
      if (!client.connected()) {
        reconnect(uniquekey);
      }

      } else {     // Auth REST API
//         HTTPClient http;
//         http.begin(serverUrl);
//         http.addHeader("Content-Type", "application/json");

//         char temp[128]={0,};
//         char keytemp[64]={0,};
//         uniquekey.toCharArray(keytemp,uniquekey.length());
//         sprintf(temp,"{\"uuid\":\"%s\"}", keytemp);
        
//         String msg = temp;
// #ifdef DEBUG
//         Serial.println("key json:"+msg);
// #endif
//         int httpResponseCode = http.POST(msg);

//         if(httpResponseCode >= 400) {
//           Serial.println("Server Error");
//           Serial.println("HTTP Response Code: " + String(httpResponseCode));
//         } else if (httpResponseCode >= 200) {
// #ifdef DEBUG
//           String response = http.getString();
//           Serial.println("HTTP Response Code: " + String(httpResponseCode));
//           // Serial.println("Response: " + response);
//           char tempdata[response.length()];
//           response.toCharArray(tempdata, response.length()+1);
//           Serial.print("Received Response: ");
//           Serial.println(tempdata);
// #endif
//           auth_flag = true;
//         } else {
//           Serial.println("Error on HTTP request");
//           Serial.println("HTTP Response Code: " + String(httpResponseCode));
//         }

//         http.end();
      }
    
      previousMillis = currentMillis;
    }
  }
}

void mcuinit(void){
  String buff;
  int temp;
  
  Serial.begin(115200);
  if (!EEPROM.begin(1000)) {
    Serial.println("ERROR: Failed to initialise EEPROM");
    Serial.println("ERROR: Restarting...");
    delay(1000);
    ESP.restart();
  }

  ssid = EEPROM.readString(EEPROM_SSID_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: SSID is " + ssid);
#endif
  password = EEPROM.readString(EEPROM_PASSWORD_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: PASSWORD is " + password);
#endif
  serverUrl = EEPROM.readString(EEPROM_URL_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: URL is " + serverUrl);
#endif
  uniquekey = EEPROM.readString(EEPROM_KEY_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: KEY is " + uniquekey);
#endif
  wifiretrytimes = EEPROM.readUInt(EEPROM_WIFIRETRY_ADDRESS);
#ifdef DEBUG
  Serial.print("INFO: Wifi retry is ");
  Serial.println(wifiretrytimes, DEC);
#endif
  factory_reset = EEPROM.readUInt(EEPROM_FACTORYRST_ADDRESS);
#ifdef DEBUG
  Serial.print("INFO: Factory reset is ");
  Serial.println(factory_reset);
#endif
  brokerUrl = EEPROM.readString(EEPROM_BROKER_ADDRESS);
  if (server.fromString(brokerUrl)) { // try to parse into the IPAddress
    // Serial.println(server); // print the parsed IPAddress 
  } else {
      Serial.println("ERROR: UnParsable IP");
  }
#ifdef DEBUG
  Serial.println("INFO: Broker Server is " + brokerUrl);
#endif
  mqttport = EEPROM.readUInt(EEPROM_MQTTPORT_ADDRESS);
#ifdef DEBUG
  Serial.print("INFO: Broker Port is ");
  Serial.println(mqttport);
#endif
  mqttid = EEPROM.readString(EEPROM_MQTTID_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: MQTT ID is " + mqttid);
#endif
  mqttpw = EEPROM.readString(EEPROM_MQTTPASSWORD_ADDRESS);
#ifdef DEBUG
  Serial.println("INFO: MQTT Password is " + mqttpw);
#endif

  if(factory_reset){
    // TODO: EEPROM Fail handlings
    ssid = "dongdong";
    EEPROM.writeString(EEPROM_SSID_ADDRESS, ssid);

    password = "75489969";
    EEPROM.writeString(EEPROM_PASSWORD_ADDRESS, password);

    serverUrl = "http://192.168.0.35:8088/api/auth-e";
    EEPROM.writeString(EEPROM_URL_ADDRESS, serverUrl);

    char tmp[32]={0,};
    WiFi.macAddress(mac);
    sprintf(tmp,"WSLY%02x%02x%02x%02x%02x%02x", mac[0],mac[1],mac[2],mac[3],mac[4],mac[5]);
    uniquekey = tmp;
    uniquekey.toUpperCase();
    EEPROM.writeString(EEPROM_KEY_ADDRESS, uniquekey);

    wifiretrytimes = 10;
    EEPROM.writeUInt(EEPROM_WIFIRETRY_ADDRESS, wifiretrytimes);

    factory_reset = 0;
    EEPROM.writeUInt(EEPROM_FACTORYRST_ADDRESS, factory_reset);

    brokerUrl = "192.168.0.24";
    EEPROM.writeString(EEPROM_BROKER_ADDRESS, brokerUrl);

    mqttport = 1883;
    EEPROM.writeUInt(EEPROM_MQTTPORT_ADDRESS, mqttport);

    mqttid = "guest";
    EEPROM.writeString(EEPROM_MQTTID_ADDRESS, mqttid);

    mqttpw = "guest";
    EEPROM.writeString(EEPROM_MQTTPASSWORD_ADDRESS, mqttpw);

    EEPROM.commit();
  }
}

int connectToWiFi(void) {
  int cnt=0;
  WiFi.begin(ssid.c_str(), password.c_str());

  while ((WiFi.status() != WL_CONNECTED) && (cnt < wifiretrytimes)) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
    cnt++;
  }

  if(cnt >= wifiretrytimes)
    return -1;
  else
    return cnt;
}

void Serialcommand(void){
  // Serial Communication 
  if (Serial.available()) {
    String input = Serial.readStringUntil('\n');
    if (input.startsWith("SSID=")) {
      ssid = input.substring(5);
      EEPROM.writeString(EEPROM_SSID_ADDRESS, ssid);
      EEPROM.commit();
      Serial.println("AT: SSID Changed: " + ssid);
      // connectToWiFi(); 
    } else if (input.startsWith("SSID?")) {
      Serial.println("AT: SSID: " + ssid);
    } else if (input.startsWith("PASSWORD=")) {
      password = input.substring(9);
      EEPROM.writeString(EEPROM_PASSWORD_ADDRESS, password);
      EEPROM.commit();
      Serial.println("AT: Password Changed.");
      // connectToWiFi(); 
    } else if (input.startsWith("PASSWORD?")) {
      Serial.println("AT: PASSWORD: " + password);
    } else if (input.startsWith("URL=")) {
      serverUrl = input.substring(4);
      EEPROM.writeString(EEPROM_URL_ADDRESS, serverUrl);
      EEPROM.commit();
      Serial.println("AT: URL Changed: " + serverUrl);
    } else if (input.startsWith("URL?")) {
      Serial.println("AT: URL: " + serverUrl);
    } else if (input.startsWith("JSON=")) {
      char tempdata[input.substring(5).length()];
      input.substring(5).toCharArray(tempdata, input.substring(5).length()+1);
      Serial.println("AT: Read json string");
      Serial.println(tempdata);
      //TESTDATA -> JSON={\"sensor\":\"gps\",\"time\":1351824120,\"data\":[48.756080,2.302038]}
      
      auto error = deserializeJson(doc, tempdata);
      if (error) {
          Serial.print(("deserializeJson() failed with code "));
          Serial.println(error.c_str());
          return;
      }
      const char* sensor = doc["sensor"];
      long time = doc["time"];
      double latitude = doc["data"][0];
      double longitude = doc["data"][1];

      Serial.println(sensor);
      Serial.println(time);
      Serial.println(latitude, 6);
      Serial.println(longitude, 6);

    } else if(input.startsWith("CONNECT")){
      Serial.println("AT: Try Connect to WiFi");
      connect=connectToWiFi();

      if(connect<0){
        Serial.println("AT: Failed Connect to WiFi");
      } else {  
        Serial.println("AT: Success Connect to WiFi");
        Serial.print("AT: Connect Try time: ");
        Serial.println(connect,DEC);
      }
    } else if(input.startsWith("MAC?")){
      Serial.println("AT: MAC Addr: " + WiFi.macAddress());
    } else if(input.startsWith("KEY?")){
      Serial.println("AT: Unique Key: " + uniquekey);
    } else if(input.startsWith("WIFIRETRY=")){
      wifiretrytimes = input.substring(10).toInt();
      EEPROM.writeUInt(EEPROM_WIFIRETRY_ADDRESS, wifiretrytimes);
      EEPROM.commit();
      Serial.print("AT: Wifi Retry Time Changed: ");
      Serial.println(wifiretrytimes, DEC);
    } else if(input.startsWith("WIFIRETRY?")){
      Serial.print("AT: Wifi Retry Time: ");
      Serial.println(wifiretrytimes, DEC);
    } else if(input.startsWith("FACTORYRESET!")){
      factory_reset=1;
      EEPROM.writeUInt(EEPROM_WIFIRETRY_ADDRESS, factory_reset);
      EEPROM.commit();
      delay(1);
      ESP.restart();
    } else if (input.startsWith("BROKER=")) {
      brokerUrl = input.substring(7);
      EEPROM.writeString(EEPROM_BROKER_ADDRESS, brokerUrl);
      EEPROM.commit();
      if (server.fromString(brokerUrl)) { // try to parse into the IPAddress
          Serial.println("AT: Broker Changed: " + brokerUrl);
      } else {
          Serial.println("ERROR: UnParsable IP");
      }
    } else if (input.startsWith("BROKER?")) {
      Serial.println("AT: Broker: " + brokerUrl);
    } else if(input.startsWith("MQTTPORT=")){
      mqttport = input.substring(9).toInt();
      EEPROM.writeUInt(EEPROM_MQTTPORT_ADDRESS, mqttport);
      EEPROM.commit();
      Serial.print("AT: Broker port Changed: ");
      Serial.println(mqttport, DEC);
    } else if(input.startsWith("MQTTPORT?")){
      Serial.print("AT: Broker port: ");
      Serial.println(mqttport, DEC);
    } else if (input.startsWith("MQTTID=")) {
      mqttid = input.substring(7);
      EEPROM.writeString(EEPROM_MQTTID_ADDRESS, mqttid);
      EEPROM.commit();
      Serial.println("AT: MQTT ID Changed: " + mqttid);
      // connectToWiFi(); 
    } else if (input.startsWith("MQTTID?")) {
      Serial.println("AT: MQTT ID : " + mqttid);
    } else if (input.startsWith("MQTTPW=")) {
      mqttpw = input.substring(7);
      EEPROM.writeString(EEPROM_MQTTPASSWORD_ADDRESS, mqttpw);
      EEPROM.commit();
      Serial.println("AT: MQTT Password Changed.");
      // connectToWiFi(); 
    } else if (input.startsWith("MQTTPW?")) {
      Serial.println("AT: MQTT Password: " + mqttpw);
    } else {
      Serial.println("AT: Wrong Command.");
    }
  }
}

void mqtt_callback(char* topic, byte* payload, unsigned int length){
  // Allocate the correct amount of memory for the payload copy
  char* p = (char*)malloc(length+1);
  // Copy the payload to the new buffer
  memset(p,0,length+1);
  memcpy(p,(char*)payload,length);
  // client.publish("outTopic", p, length);

  String buf = String(p);
  Serial.print("mqtt receive msg: ");
  Serial.println(buf);
  Serial.print("mqtt receive length: ");
  Serial.println(length, DEC);

// TODO: add parser
  auto error = deserializeJson(doc, p);
  if (error) {
      Serial.print(("deserializeJson() failed with code "));
      Serial.println(error.c_str());
      return;
  }
  // TODO: data error handlers
  int usernumber = doc["usernumber"];
  int point = doc["point"];
  clock_point[usernumber] = point;
  Serial.print("USER NUMBER : ");
  Serial.println(usernumber, DEC);
  Serial.print("Point : ");
  Serial.println(point, DEC);

  // Free the memory
  free(p);
}

void reconnect(String key) {
  char tmp[32] = {0,};
  tmp[0] = '/';
  key.toCharArray(&tmp[1], key.length());
  // Loop until we're reconnected
  if (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    // if (client.connect(tmp, "guest", "guest")) {
    if (client.connect(tmp)) {
      Serial.println("INFO: MQTT connected");
      // Once connected, publish an announcement...
      // client.publish("outTopic","hello world");
      // ... and resubscribe
      client.subscribe("test/test");  // TODO: changable
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      // delay(5000);
    }
  }
}


