import RPi.GPIO as GPIO
import argparse
import time

parser = argparse.ArgumentParser()
parser.add_argument(
    '--value',
     help = 'Value to output in binary',
     required = True,
     type = int
)
args = parser.parse_args()

bitPins = reversed([3, 5, 7])

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

for i, pin in enumerate(bitPins):
    value = (args.value & 1 << i) != 0
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, value)