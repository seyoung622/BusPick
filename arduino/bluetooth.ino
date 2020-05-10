#include <SoftwareSerial.h>

const int btTx = 2;
const int btRx = 3;
const int speakerpin = 6;
const int led = 10;
int sw = 8;

SoftwareSerial bluetooth(btTx, btRx);

void setup() {
  pinMode(led, OUTPUT);
  pinMode(sw, INPUT_PULLUP);
  
  Serial. begin(9600);
  bluetooth. begin(9600);
  Serial. println("AT command");
}

void loop() {
  if(bluetooth.available() > 0){
    char num = char(bluetooth.read());
    if(num =='1'){
      digitalWrite(led, HIGH);
      tone(speakerpin, 1000, 1000);
    }
  }
  if(digitalRead(sw)== LOW){ //스위치를 누르면 led OFF
    digitalWrite(led,LOW);
  }
}
