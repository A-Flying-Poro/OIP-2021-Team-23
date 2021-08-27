import RPi.GPIO as GPIO
import argparse
import sys

parser = argparse.ArgumentParser()
parser.add_argument(
    '--pin',
     help = 'Pin used for setting high or low voltage',
     required = True,
     type = int
)
parser.add_argument(
    '--value',
     help = 'Set the pin to high (1) or low (0)',
     required = True,
     type = int
)
args = parser.parse_args()

if args.value < 0 or args.value > 1:
    sys.stderr.write('Invalid value provided: ' + str(args.value) + ', expected 1 or 0\n')
    sys.stderr.flush()
    sys.exit(1)

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(args.pin, GPIO.OUT)

GPIO.output(args.pin, args.value)