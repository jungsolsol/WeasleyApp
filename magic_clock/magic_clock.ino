// client side code 
#include <WiFi.h>
#include <HTTPClient.h>

String ssid = "SK_WiFiGIGAA98E_2.4G";          // 기본 SSID를 설정합니다.
String password = "1603058178";  // 기본 비밀번호를 설정합니다.
String serverUrl = "http://192.168.35.207:8082"; // 기본 URL을 설정합니다.

unsigned long previousMillis = 0;
const long interval = 60000;  // 1분(60,000 밀리초)

void setup() {
  // 시리얼 통신을 시작합니다.
  Serial.begin(115200);

  // Wi-Fi 연결을 시작합니다.
  connectToWiFi();

  Serial.println("Connected to WiFi");
}

void loop() {
  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis >= interval) {
    // 현재 시간이 이전 요청 시간에서 1분 이상 경과한 경우

    // HTTPClient 객체를 만듭니다.
    HTTPClient http;
    http.begin(serverUrl);

    // HTTP GET 요청을 보냅니다.
    String testmsg = "HELLO";
    int httpResponseCode = http.GET();

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println("HTTP Response Code: " + String(httpResponseCode));
      Serial.println("Response: " + response);
    } else {
      Serial.println("Error on HTTP request");
    }

    // HTTP 연결을 종료합니다.
    http.end();

    // 이전 요청 시간을 업데이트합니다.
    previousMillis = currentMillis;
  }

  // 이 부분에 추가 작업을 수행할 수 있습니다.

  // 시리얼 입력을 확인하여 Wi-Fi 및 URL 설정을 변경합니다.
  if (Serial.available()) {
    String input = Serial.readStringUntil('\n');
    if (input.startsWith("SSID=")) {
      ssid = input.substring(5);
      Serial.println("SSID 설정이 변경되었습니다: " + ssid);
      connectToWiFi(); // 변경된 SSID로 다시 연결
    } else if (input.startsWith("PASSWORD=")) {
      password = input.substring(9);
      Serial.println("비밀번호 설정이 변경되었습니다.");
      connectToWiFi(); // 변경된 비밀번호로 다시 연결
    } else if (input.startsWith("URL=")) {
      serverUrl = input.substring(4);
      Serial.println("URL 설정이 변경되었습니다: " + serverUrl);
    }
  }
}

void connectToWiFi() {
  WiFi.begin(ssid.c_str(), password.c_str());

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
}
