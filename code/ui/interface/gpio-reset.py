import RPi.GPIO as GPIO
import argparse

parser = argparse.ArgumentParser()
parser.add_argument(
    '--pin',
     help = 'Pin to reset to input without any pull up or down resistor',
     required = True,
     type = int
)
args = parser.parse_args()

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(args.pin, GPIO.IN)