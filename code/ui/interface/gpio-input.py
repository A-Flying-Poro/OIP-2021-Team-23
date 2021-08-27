import RPi.GPIO as GPIO
import argparse

parser = argparse.ArgumentParser()
parser.add_argument(
    '--pin',
     help = 'Pin used for receiving input from Arduino',
     required = True,
     type = int
)
args = parser.parse_args()

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(args.pin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

print(GPIO.input(args.pin))