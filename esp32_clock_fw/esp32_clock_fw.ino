// client side code 
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

String ssid = "dongdong";      
String password = "75489969";  
String serverUrl = "http://192.168.35.207:8082";
String uniquekey;

unsigned long previousMillis = 0;
const long interval = 10000; 
StaticJsonDocument<200> doc;

int connect=0;
int wifiretrytimes=10;
byte mac[6];

bool auth_flag = false;

void setup() {
  Serial.begin(115200);
  connect=connectToWiFi();

  if(connect < 0){
    Serial.println("Failed Connect to WiFi");
  } else {
    Serial.println("Success Connect to WiFi");
    Serial.print("Connect Try time: ");
    Serial.println(connect,DEC);
  }
  
  WiFi.macAddress(mac);

  auth_flag = false;
}

void loop() {
  unsigned long currentMillis = millis();
  if(connect>0){
    if (currentMillis - previousMillis >= interval) {
      if(auth_flag){
        HTTPClient http;
        String routeserver = "?location2";
        http.begin(serverUrl+routeserver);
        http.addHeader("Content-Type", "application/json");

        http.begin(serverUrl);

        int httpResponseCode = http.GET();

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

          double latitude = doc["X"];
          double longitude = doc["Y"];
          Serial.println(latitude, 6);
          Serial.println(longitude, 6);   
        } else {
          Serial.println("Error on HTTP request");
        }

        http.end();
      } else {
        HTTPClient http;
        String routeserver = "?auth";
        http.begin(serverUrl+routeserver);
        http.addHeader("Content-Type", "application/json");

        char temp[64]={0,};
        sprintf(temp,"{\"api_key\":\"%02X%02X%02X%02X%02X%02X5348\"}", mac[0],mac[1],mac[2],mac[3],mac[4],mac[5]);
        String testmsg = temp;
        Serial.println("key json:"+testmsg);
        int httpResponseCode = http.POST(testmsg);

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

  // Serial Communication 
  if (Serial.available()) {
    String input = Serial.readStringUntil('\n');
    if (input.startsWith("SSID=")) {
      ssid = input.substring(5);
      Serial.println("SSID Changed: " + ssid);
      // connectToWiFi(); 
    } else if (input.startsWith("SSID?")) {
      Serial.println("SSID: " + ssid);
    } else if (input.startsWith("PASSWORD=")) {
      password = input.substring(9);
      Serial.println("Password Changed.");
      // connectToWiFi(); 
    } else if (input.startsWith("PASSWORD?")) {
      Serial.println("PASSWORD: " + password);
    } else if (input.startsWith("URL=")) {
      serverUrl = input.substring(4);
      Serial.println("URL Changed: " + serverUrl);
    } else if (input.startsWith("URL?")) {
      Serial.println("URL: " + serverUrl);
    } else if (input.startsWith("JSON=")) {
      char tempdata[input.substring(5).length()];
      input.substring(5).toCharArray(tempdata, input.substring(5).length()+1);
      Serial.println("Read json string");
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
      Serial.println("Try Connect to WiFi");
      connect=connectToWiFi();

      if(connect<0){
        Serial.println("Failed Connect to WiFi");
      } else {  
        Serial.println("Success Connect to WiFi");
        Serial.print("Connect Try time: ");
        Serial.println(connect,DEC);
      }
    } else if(input.startsWith("MAC?")){
      Serial.println("MAC Addr: " + WiFi.macAddress());
    } else if(input.startsWith("WIFIRETRY=")){
      wifiretrytimes = input.substring(10).toInt();
      Serial.print("Wifi Retry Time Changed: ");
      Serial.println(wifiretrytimes, DEC);
    } else if(input.startsWith("WIFIRETRY?")){
      Serial.print("Wifi Retry Time: ");
      Serial.println(wifiretrytimes, DEC);
    }
  }
}

int connectToWiFi() {
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