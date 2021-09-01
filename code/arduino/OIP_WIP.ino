#include "DHTesp.h"
#include <OneWire.h>
#include <DallasTemperature.h>
#include <AccelStepper.h>
#include "pitches.h"

// Define step constant
#define FULLSTEP 4

#define tempHumi 22
#define LED 23
#define stepper1 24
#define stepper2 26
#define stepper3 28
#define stepper4 30
#define pump 32
#define valve 34
#define contact1 2
#define contact2 3

#define fan 4
#define buzz 5
#define tempProbe 6

#define waterLevel A15

//Pi communication
#define bit1 36
#define bit2 38
#define bit3 40

/*
#define fillRequest 40 //001
#define reduceTempRequest 42 //010
#define washRequest 44 //011
#define drainRequest 46 //100
#define rinseRequest 48 //101
#define dryRequest 50 //110
#define alertRequest 52 //111
*/ 
#define stopPin 25
#define stopRequest 18
#define returnPin 29


DHTesp dht;
AccelStepper stepper(FULLSTEP, stepper1, stepper3, stepper2, stepper4);

OneWire oneWire(tempProbe);
DallasTemperature sensors(&oneWire);

float Celcius=0;
float Fahrenheit=0;

float level = 0;

boolean stopMode = false;


boolean prevLedState = LOW;
unsigned long initialLedActivation = 0;
//unsigned long maxLedTime = 300000; // full 5mins
unsigned long maxLedTime = 10000; //test for 10s

boolean prevReturnState = LOW;
unsigned long initialReturnActivation = 0;
unsigned long maxReturnTime = 5000; //hold signal for 5s

boolean prevStopState = LOW;
unsigned long initialStopActivation = 0;
unsigned long maxStopTime = 5000; //hold signal for 5s

unsigned long lastRead = 0;

unsigned long drainTime = 240000;

unsigned long maxDryTime = 1200000;


int request=0;

int dryCycle=0;

// notes in the melody:
int melody[] = {

  NOTE_C4, NOTE_G3, NOTE_G3, NOTE_A3, NOTE_G3, 0, NOTE_B3, NOTE_C4
};

// note durations: 4 = quarter note, 8 = eighth note, etc.:
int noteDurations[] = {

  4, 8, 8, 4, 4, 4, 4, 4
};



void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  sensors.begin();
  
  pinMode(LED, OUTPUT);
  pinMode(stepper1, OUTPUT);
  pinMode(stepper2, OUTPUT);
  pinMode(stepper3, OUTPUT);
  pinMode(stepper4, OUTPUT);
  pinMode(pump, OUTPUT);
  pinMode(valve, OUTPUT);
  pinMode(contact1, INPUT);
  pinMode(contact2, INPUT);
  
  pinMode(fan, OUTPUT);
  pinMode(buzz, OUTPUT);
  pinMode(tempProbe, INPUT);
  
  pinMode(waterLevel, INPUT);

//pi communication
/*
  pinMode(fillRequest, INPUT);
  pinMode(reduceTempRequest, INPUT);
  pinMode(washRequest, INPUT);
  pinMode(drainRequest, INPUT);
  pinMode(rinseRequest, INPUT);
  pinMode(dryRequest, INPUT);
  pinMode(alertRequest, INPUT);
  */
  
  pinMode(bit1, INPUT);
  pinMode(bit2, INPUT);
  pinMode(bit3, INPUT);
  
  pinMode(stopPin, OUTPUT);
  pinMode(stopRequest, INPUT);
  pinMode(returnPin, OUTPUT);
 
  attachInterrupt(digitalPinToInterrupt(contact1), ledControl, CHANGE);
  //attachInterrupt(digitalPinToInterrupt(contact2), doorOpen, CHANGE);
  attachInterrupt(digitalPinToInterrupt(stopRequest), eStop, CHANGE);

  
  dht.setup(tempHumi,  DHTesp::DHT11);

  digitalWrite(pump, LOW);
  digitalWrite(valve, LOW);
  digitalWrite(fan, LOW);
  digitalWrite(stopPin, LOW);
  digitalWrite(returnPin, LOW);
  
  stepper.setMaxSpeed(300);
  stepper.setAcceleration(20.0);
  stepper.stop();
  
  ledControl();

  
  
}

void loop() {
  
if(stopMode != true){
 if((millis()-lastRead)>500){
   request=readPins();
   lastRead=millis();
 }
 Serial.println(request);

  switch(request){
    case 1:
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      fill();
      break;
    case 2:
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      reduceTemp();
      break;
    case 3:
      stepper.setSpeed(-3000);
      stepper.runSpeed();
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      break;
    case 4:
      digitalWrite(valve, HIGH);
      digitalWrite(pump, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      break;
    case 5:
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      rinse();
      break;
    case 6:
      dry();
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      break;
    case 7:
      tune();
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      break;

    default:
      digitalWrite(pump, LOW);
      digitalWrite(valve, LOW);
      digitalWrite(fan, LOW);
      stepper.stop();
      break;
  }
  
  essentials();
}
else if(stopMode==true){
  essentials();
  digitalWrite(valve,LOW);
  digitalWrite(pump,LOW);
  digitalWrite(fan,LOW);
  stepper.stop();
}
//delay(5);
}

void ledControl(){
  boolean door1 = digitalRead(contact1);
  if(door1 == HIGH)
  {
    digitalWrite(LED, HIGH);
  }
  else
  {
    digitalWrite(LED, LOW);
  }  
}

void fill(){
  level = analogRead(waterLevel);
  while(level<300){
    essentials();
    digitalWrite(pump, HIGH);
    level = analogRead(waterLevel);
    //Serial.println(level);
  }
  digitalWrite(pump, LOW);
  digitalWrite(returnPin, HIGH);
}

void reduceTemp(){
  delay(15000);
  sensors.requestTemperatures(); 
  Celcius=sensors.getTempCByIndex(0);
  while(Celcius > 40.0){
    essentials();
    sensors.requestTemperatures(); 
    Celcius=sensors.getTempCByIndex(0);
    if(Celcius != Celcius){
      Celcius = 60.0;
    }
    level = analogRead(waterLevel);
    if(level >100){
      digitalWrite(pump, LOW);
      digitalWrite(valve, HIGH);
    }
    else if (level<=100 && level>0){
      digitalWrite(pump, HIGH);
      digitalWrite(valve, HIGH);
    }
    else if (level==0){
      digitalWrite(pump, HIGH);
      digitalWrite(valve, LOW);
    }
  }
  digitalWrite(pump, LOW);
  digitalWrite(valve, LOW);
  digitalWrite(returnPin, HIGH);
}

void rinse(){

  level = analogRead(waterLevel);
  while(level<100){
    essentials();
    digitalWrite(pump, HIGH);
    level = analogRead(waterLevel);
  }
  digitalWrite(pump, LOW);
  
  stepper.setSpeed(-3000);
  stepper.runSpeed();

  unsigned long start=millis();
  while((millis()-start)<30000){
    stepper.runSpeed();
    essentials();
  }
  
  stepper.stop();

  digitalWrite(valve,HIGH);

  start=millis();
  while((millis()-start)<drainTime){
    essentials();
  }
   
  digitalWrite(valve,LOW);

  digitalWrite(returnPin, HIGH);
}

void doorOpen(){
  if(digitalRead(contact2)==true){
    digitalWrite(stopPin, LOW);
  }
  else
  {
    stopMode=true;
    digitalWrite(stopPin, HIGH);
  }
}

void eStop(){
  if(digitalRead(stopRequest)==true){
    stopMode=true;
  }
  else
  {
    stopMode=false;
  }
}

void tune(){
  // iterate over the notes of the melody:
  dryCycle=0;
  for (int thisNote = 0; thisNote < 8; thisNote++) {

    // to calculate the note duration, take one second divided by the note type.

    //e.g. quarter note = 1000 / 4, eighth note = 1000/8, etc.

    int noteDuration = 1000 / noteDurations[thisNote];

    tone(buzz, melody[thisNote], noteDuration);

    // to distinguish the notes, set a minimum time between them.

    // the note's duration + 30% seems to work well:

    int pauseBetweenNotes = noteDuration * 1.30;

    delay(pauseBetweenNotes);

    // stop the tone playing:

    noTone(buzz);

  }
}

void essentials(){
  
  //5min deactivation of LED lights
  boolean door1 = digitalRead(contact1);
  if (prevLedState == LOW && digitalRead(LED) == HIGH){
    prevLedState=HIGH;
    initialLedActivation = millis();
  }
  else if (prevLedState == HIGH && digitalRead(LED) == HIGH){
     if ((millis() - initialLedActivation)> maxLedTime){
        prevLedState = LOW;
        digitalWrite(LED, LOW);
     }
  }
  else if (prevLedState == HIGH && digitalRead(LED) == LOW){
    prevLedState = LOW;
  }

  //returnPin deactivation
  boolean returnState = digitalRead(returnPin);
  if (prevReturnState == LOW && returnState == HIGH){
    prevReturnState=HIGH;
    initialReturnActivation = millis();
  }
  else if (prevReturnState == HIGH && returnState == HIGH){
     if ((millis() - initialReturnActivation)> maxReturnTime){
        prevReturnState = LOW;
        digitalWrite(returnPin, LOW);
     }
  }

  //stopPin deactivation
  boolean StopState = digitalRead(stopPin);
  if (prevStopState == LOW && StopState == HIGH){
    prevStopState=HIGH;
    initialStopActivation = millis();
  }
  else if (prevStopState == HIGH && StopState == HIGH){
     if ((millis() - initialStopActivation)> maxStopTime){
        prevStopState = LOW;
        digitalWrite(stopPin, LOW);
     }
  }
}

void essentialLoop(){
  while(stopMode==true){
    essentials();
    digitalWrite(valve,LOW);
    digitalWrite(pump,LOW);
    digitalWrite(fan,LOW);
    stepper.stop();
  }
}

int readPins(){
  boolean b1 = digitalRead(bit1);
  boolean b2 = digitalRead(bit2);
  boolean b3 = digitalRead(bit3);
  int val = b1 << 2 | b2 << 1 | b3 << 0;
  return val;
}

void dry(){
  if(dryCycle == 0 ){
    stepper.setSpeed(-3000);
    stepper.runSpeed();
    digitalWrite(fan, HIGH);
    unsigned long start = millis();
    TempAndHumidity measurement = dht.getTempAndHumidity();
    essentials();
    
    //unsigned long dryTime = maxDryTime* map(measurement.temperature, 20,50,1,0.5)
    int maxEvap = map(measurement.temperature, 20,50, 50, 100);
    int evapRate = map(measurement.humidity, 30.0, 100.0, maxEvap, 0);
    Serial.println(evapRate);
    unsigned long dryTime = (unsigned long)(maxDryTime* (evapRate/50.0)); 

    while((millis()- start) < dryTime){
      stepper.runSpeed();
      essentials();
    }
    digitalWrite(returnPin, HIGH);
    dryCycle=1;
    
  }
  else if(dryCycle!=0){
    while(readPins() == 6){
      essentials();
      Serial.println("im in");
      stepper.setSpeed(-3000);
      stepper.runSpeed();
      digitalWrite(fan, HIGH);
      
    }
    dryCycle++;
  }
}
