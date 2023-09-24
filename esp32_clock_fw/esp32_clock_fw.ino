// client side code 
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <
#include <PubSubClient.h>

#define DEBUG 1

#define EEPROM_START_ADDRESS           0
#define EEPROM_SSID_ADDRESS            4
#define EEPROM_PASSWORD_ADDRESS        68
#define EEPROM_URL_ADDRESS             132
#define EEPROM_KEY_ADDRESS             388
#define EEPROM_WIFIRETRY_ADDRESS       408
#define EEPROM_FACTORYRST_ADDRESS      416
#define EEPROM_BROKER_ADDRESS          424
#define EEPROM_MQTTPORT_ADDRESS        680
#define EEPROM_MQTTID_ADDRESS          688
#define EEPROM_MQTTPASSWORD_ADDRESS    752
#define EEPROM_END_ADDRESS             4095

String ssid;
String password;
String serverUrl;
String uniquekey;

unsigned int factory_reset=0;
unsigned long previousMillis = 0;
const long interval = 10000;

StaticJsonDocument<200> doc;

String brokerUrl;
unsigned int mqttport=1883;
String mqttid;
String mqttpw;
PubSubClient client(espclient);

int connect=0;
unsigned int wifiretrytimes=10;
byte mac[6];

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

  if(client)
  auth_flag = false;
}

void loop() {
  unsigned long currentMillis = millis();

  Serialcommand();
  if(connect>0){
    if (currentMillis - previousMillis >= interval) {
      if(auth_flag){
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
      } else {
        HTTPClient http;
        String routeserver = "/auth";
        http.begin(serverUrl+routeserver);
        http.addHeader("Content-Type", "application/json");

        char temp[64]={0,};
        sprintf(temp,"{\"uuid\":\"%s\"}", uniquekey);
        
        String msg = temp;
#ifdef DEBUG
        Serial.println("key json:"+msg);
#endif
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

          auth_flag = doc["AUTH"];          
        } else {
          Serial.println("Error on HTTP request");
        }

        http.end();
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
  Serial.println(wifiretrytimes);
#endif
  factory_reset = EEPROM.readUInt(EEPROM_FACTORYRST_ADDRESS);
#ifdef DEBUG
  Serial.print("INFO: Factory reset is ");
  Serial.println(factory_reset);
#endif
  brokerUrl = EEPROM.readString(EEPROM_BROKER_ADDRESS);
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
    ssid = "dongdong";
    EEPROM.writeString(EEPROM_SSID_ADDRESS, ssid);

    password = "75489969";
    EEPROM.writeString(EEPROM_PASSWORD_ADDRESS, password);

    serverUrl = "http://192.168.0.6:8082";
    EEPROM.writeString(EEPROM_URL_ADDRESS, serverUrl);

    char tmp[32]={0,};
    WiFi.macAddress(mac);
    sprintf(tmp,"WSLY%02x%02x%02x%02x%02x%02x", mac[0],mac[1],mac[2],mac[3],mac[4],mac[5]);
    uniquekey = tmp;
    uniquekey.toUpperCase();
    EEPROM.writeString(EEPROM_KEY_ADDRESS, uniquekey);

    wifiretrytimes = 5;
    EEPROM.writeUInt(EEPROM_WIFIRETRY_ADDRESS, wifiretrytimes);

    factory_reset = 0;
    EEPROM.writeUInt(EEPROM_FACTORYRST_ADDRESS, factory_reset);

    brokerUrl = "mqtt://localhost";
    EEPROM.writeString(EEPROM_BROKER_ADDRESS, brokerUrl);

    mqttport = 1883;
    EEPROM.writeUInt(EEPROM_MQTTPORT_ADDRESS, mqttport);

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
      ESP.restart();
    } else if (input.startsWith("BROKER=")) {
      brokerUrl = input.substring(7);
      EEPROM.writeString(EEPROM_BROKER_ADDRESS, brokerUrl);
      EEPROM.commit();
      Serial.println("AT: Broker Changed: " + brokerUrl);
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



