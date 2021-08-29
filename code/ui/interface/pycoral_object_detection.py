# based on https://github.com/google-coral/pycoral/blob/master/examples/detect_image.py
from imutils.video import VideoStream, FPS
import argparse
import time
import cv2
from PIL import Image, ImageDraw
import numpy as np
import imutils
from timeit import default_timer as timer

from pycoral.adapters import common
from pycoral.adapters import detect
from pycoral.utils.dataset import read_label_file
from pycoral.utils.edgetpu import make_interpreter


def draw_objects(image, objs, labels):
    draw = ImageDraw.Draw(image)
    for obj in objs:
        bbox = obj.bbox
     
        draw.rectangle(
            [(bbox.xmin , bbox.ymin), (bbox.xmax, bbox.ymax)], outline='lime')
        draw.text((bbox.xmin + 10, bbox.ymin + 10), '%s\n%.2f' %
                (labels.get(obj.id, obj.id), obj.score), fill='lime')                   
    displayImage = np.asarray(image)
    cv2.imshow('Coral Live Object Detection', displayImage)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--model', help='File path of Tflite model.', required=True)
    parser.add_argument(
        '--labels', help='File path of label file.', required=True)
    parser.add_argument('--picamera',
                        action='store_true',
                        help="Use PiCamera for image capture",
                        default=True)
    parser.add_argument(
        '-t', '--threshold', type=float, default=0.5,
        help='Classification score threshold')
    parser.add_argument(
        '--headless', type=bool, default=False,
        help='Run without showing camera and detection output')
    args = parser.parse_args()

#     print('Loading {} with {} labels.'.format(args.model, args.labels))
    labels = read_label_file(args.labels) if args.labels else {}
    interpreter = make_interpreter(args.model)
    interpreter.allocate_tensors()
    
    # Initialize video stream
    vs = VideoStream(usePiCamera=args.picamera, resolution=(1280, 720)).start()
    time.sleep(2)

    fps = FPS().start()

    counts = {}

    framesReceived = 0

    framesTotal = 30
    framesInference = framesTotal * 0.6

    while (framesReceived < framesTotal):
        framesReceived += 1
        try:
            # Read frame from video
            screenshot = vs.read()
     
            image = Image.fromarray(screenshot)
            _, scale = common.set_resized_input(
                interpreter, image.size, lambda size: image.resize(size, Image.ANTIALIAS))
            interpreter.invoke()
            objs = detect.get_objects(interpreter, args.threshold, scale)
            if not args.headless:
                draw_objects(image, objs, labels)
    

            if(len(objs) != 0):
                detectedId = objs[0].id
                if detectedId not in counts:
                    counts[detectedId] = 1
                else:
                    counts[detectedId] += 1
            
            #if the inference results occurs the same 3 times
            # if(count >= framesInference):
            #     found = True
            #     if(result == 0):
            #         print("dry")
            #     else:
            #         print("wet")
            #     fps.stop()
            #     break
            
            if(cv2.waitKey(5) & 0xFF == ord('q')):
                fps.stop()
                break

            fps.update()
        except KeyboardInterrupt:
            fps.stop()
            break

    #if not found:
    #    print("missing")

    highestId = None
    highestCount = 0
    for detectedId, detectedCount in counts.items():
        if highestId is None or detectedCount > highestCount:
            highestId = detectedId
            highestCount = detectedCount

    result = "missing"
    if highestId is not None or highestCount >= framesInference:
        result = labels.get(obj.id, obj.id)

    print(result)

#     print("Elapsed time: " + str(fps.elapsed()))
#     print("Approx FPS: :" + str(fps.fps()))

    cv2.destroyAllWindows()
    vs.stop()
#     time.sleep(2)


if __name__ == '__main__':
    main()