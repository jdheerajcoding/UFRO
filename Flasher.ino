/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */

int i = 2;
boolean dir;
boolean buttonPressed;
long prevMil, currentMil;
int mode = 0;
int potValue, upperPotValue, lowerPotValue, delayValue, pulseValue;
int upperRange = 6000;

// sensor pins
int trigPin = 5;
int echoPin = 6;

// the setup routine runs once when you press reset:
void setup() 
{       
  Serial.begin(9600);
  
  // Initialize LED pins
  DDRB |= B00111111;
  
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
}

// the loop routine runs over and over again forever:
void loop() 
{
  currentMil = millis();
  
  // get button press
  if(digitalRead(7) == LOW)
  {
    delay(1); // debounce
    
    if(digitalRead(7) == LOW && !buttonPressed)
    {
      buttonPressed = true;
      mode++;
      if(mode > 4)
        mode = 0;
    }
  }
  else
    buttonPressed = false;
  
  // Trigger the HCSR04 ultrasonic sensor
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  pulseValue = pulseIn(echoPin, HIGH);
  Serial.println(pulseValue);
  
  // limit the range
  if(pulseValue > upperRange)
    pulseValue = upperRange;
  else if(pulseValue < 200)
    pulseValue = 200;
  
  // Get analog values
  potValue = analogRead(A0);
  
  // remap pot value
  upperPotValue = map(potValue, 0, 1024, 200, 100);
  lowerPotValue = map(potValue, 0, 1024, 60, 30);
  
  // remap the delay value according to the ultrasonic sensor
  delayValue = map(pulseValue, 200, upperRange, lowerPotValue, upperPotValue);
  
  
  // Blink some LEDs!
  if(mode == 0) // night rider
  {
    PORTB |= (1 << i);
  
    if((currentMil - prevMil) > delayValue)
    {
      prevMil = currentMil;
    
      PORTB &= ~(1 << i);
  
      if(dir == true && i < 5)
        i++;
      else if(dir == true)
         dir = false; 
      else if(dir == false && i > 0)
        i--;
      else
        dir = true;
    }
  }
  else if(mode == 1) // rotate up
  {
    PORTB |= (1 << i);
  
    if((currentMil - prevMil) > delayValue)
    {
      prevMil = currentMil;
    
      PORTB &= ~(1 << i);
  
      if(i < 5)
        i++;
      else
        i = 0; // PB0 digital 8
    }
  }
  else if(mode == 2) // rotate up
  {
    PORTB |= (1 << i);
  
    if((currentMil - prevMil) > delayValue)
    {
      prevMil = currentMil;
    
      PORTB &= ~(1 << i);
  
      if(i > 0)
        i--;
      else
        i = 5; // PB5 digital 13
    }
  }
  else if(mode == 3) // alternate
  {
    if((currentMil - prevMil) > delayValue)
    {
      prevMil = currentMil;
    
      if(dir == true)
      {
        dir = false;
        PORTB &= B11000000;
        PORTB |= B00101010;
      }
      else
      {
        dir = true;
        PORTB &= B11000000;
        PORTB |= B00010101;
      }
    }
  }
  else if(mode == 4) // random!
  {
    if((currentMil - prevMil) > delayValue)
    {
      prevMil = currentMil;
        
      PORTB &= B11000000;
      PORTB |= random(0, 64);
    }
  }
}
