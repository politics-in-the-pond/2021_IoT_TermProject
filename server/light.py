# -*- coding: utf-8 -*-
#light.py
import socket
#from _thread import *
import RPi.GPIO as GPIO
import time
import cv2
import numpy as np
import threading

GPIO.setmode(GPIO.BCM)

GPIO.setup(14, GPIO.OUT)
GPIO.output(14, GPIO.LOW)
pwm_white = GPIO.PWM(14, 500)

GPIO.setup(15, GPIO.OUT)
GPIO.output(15, GPIO.LOW)
pwm_green = GPIO.PWM(15, 500)

GPIO.setup(18, GPIO.OUT)
GPIO.output(18, GPIO.LOW)
pwm_yello = GPIO.PWM(18, 500)

GPIO.setup(3, GPIO.OUT)
GPIO.output(3, GPIO.LOW)
pwm_blue = GPIO.PWM(3, 500)

GPIO.setup(4, GPIO.OUT)
GPIO.output(4, GPIO.LOW)
pwm_red = GPIO.PWM(4, 500)

power = "0"
color = "-1"
gesture = "0"
duty = 0
recordcnt = 0
mode = 0

def setCount(cnt) :
	global recordcnt
	recordcnt = cnt

def hand():
	try:
		cap=cv2.VideoCapture(0)
		cap.set(3,320)
		cap.set(4,240)
	except:
		print('camera_error')
		return

	beforecnt = 0

	while True:
		ret, frame = cap.read()

		if not ret:
			print('camera_error')
			break

		dst = frame.copy()
		test = cv2.cvtColor(dst, cv2.COLOR_BGR2YCrCb)
		mask_hand = cv2.inRange(test, np.array([0,132,77], dtype = "uint8"), np.array([255,173,133], dtype = "uint8"))
		blur = cv2.blur(mask_hand, (2,2))
		ret, thr = cv2.threshold(blur, 132, 255, cv2.THRESH_BINARY)
		_, contours, hierachy = cv2.findContours(thr, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
		cnt = 0

		if contours :
			contours = max(contours, key=lambda x: cv2.contourArea(x))
			hull = cv2.convexHull(contours, returnPoints=False)
			defects = cv2.convexityDefects(contours, hull)
			#cv2.drawContours(dst, contours, -1, (0, 255, 255), 2)
			if defects is not None :
				cnt = 0
				for i in range(defects.shape[0]):
					startd, endd, fard, d = defects[i][0]
					start = tuple(contours[startd][0])
					end = tuple(contours[endd][0])
					far = tuple(contours[fard][0])
					a = np.sqrt((end[0] - start[0]) ** 2 + (end[1] - start[1]) ** 2)
					b = np.sqrt((far[0] - start[0]) ** 2 + (far[1] - start[1]) ** 2)
					c = np.sqrt((end[0] - far[0]) ** 2 + (end[1] - far[1]) ** 2)
					angle = np.arccos((b ** 2 + c ** 2 - a ** 2) / (2 * b * c))

					if angle >= np.pi / 10 and angle <= np.pi / 2.3 :
						cnt +=1
						cv2.circle(dst, far, 4, [0, 0, 255], -1)
				if cnt > 0 :
					cnt = cnt+1
					beforecnt = beforecnt * 0.97 + cnt * 0.03
				
		else :
			cnt = 0
			setCount(round(beforecnt))

		cv2.putText(dst, str(round(beforecnt)), (0, 90), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255) , 2, cv2.LINE_AA)
		cv2.putText(dst, str(round(recordcnt)), (0, 130), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0) , 2, cv2.LINE_AA)
		
		if mode == -1:
			colorCode = "c" + str(recordcnt)
			setColor(colorCode)

		cv2.imshow('dst', dst)
		#cv2.imshow('ret', thr)
		#cv2.imshow('key', test)

		k = cv2.waitKey(1) & 0xFF
		if k == 27:
			break

	cap.release()
	cv2.destroyAllWindows()

def navigation(text):
	global mode
	control = text.rstrip('\n')
	print("in navigation: " + control)
	set = control.split(" ")
	print(set)

	for i in set:
		print(i)
		if i[0] == "p":
			setPower(i)
		elif i[0] == "c":
			setColor(i)
		elif i[0] == "t":
			setTime(i)
		elif i == "-1":
			mode = -1
			print("mode changed")
		elif i == "0":
			mode = 0
			print("mode changed")

	if power == "0":
		powerOff()


def setPower(pCode):
	global power
	power = pCode[1]
	if power == "1":
		GPIO.output(14, GPIO.HIGH)
		GPIO.output(15, GPIO.HIGH)
		GPIO.output(18, GPIO.HIGH)
		GPIO.output(3, GPIO.HIGH)
		GPIO.output(4, GPIO.HIGH)
	if power == "0":
		GPIO.output(14, GPIO.LOW)
		GPIO.output(15, GPIO.LOW)
		GPIO.output(18, GPIO.LOW)
		GPIO.output(3, GPIO.LOW)
		GPIO.output(4, GPIO.LOW)


def setColor(Ccode):
	global mode
	global color
	global recordcnt
	color = Ccode[1]
	if mode == -1:
		if color == "0":
			GPIO.output(14, GPIO.HIGH)
			GPIO.output(15, GPIO.HIGH)
			GPIO.output(18, GPIO.HIGH)
			GPIO.output(3, GPIO.HIGH)
			GPIO.output(4, GPIO.HIGH)
		elif color == "1":
			GPIO.output(14, GPIO.HIGH)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "2":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.HIGH)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "3":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.HIGH)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "4":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.HIGH)
			GPIO.output(4, GPIO.LOW)
		elif color == "5":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.HIGH)
		
	else:
		if color == "0":
			GPIO.output(14, GPIO.HIGH)
			GPIO.output(15, GPIO.HIGH)
			GPIO.output(18, GPIO.HIGH)
			GPIO.output(3, GPIO.HIGH)
			GPIO.output(4, GPIO.HIGH)
		elif color == "1":
			GPIO.output(14, GPIO.HIGH)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "2":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.HIGH)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "3":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.HIGH)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.LOW)
		elif color == "4":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.HIGH)
			GPIO.output(4, GPIO.LOW)
		elif color == "5":
			GPIO.output(14, GPIO.LOW)
			GPIO.output(15, GPIO.LOW)
			GPIO.output(18, GPIO.LOW)
			GPIO.output(3, GPIO.LOW)
			GPIO.output(4, GPIO.HIGH)

def setTime(tcode):
	global duty
	duty = int(tcode[1])
	print(duty)
	time.sleep(duty)
	powerOff()

def powerOff():
	GPIO.output(14, GPIO.LOW)
	GPIO.output(15, GPIO.LOW)
	GPIO.output(18, GPIO.LOW)
	GPIO.output(3, GPIO.LOW)
	GPIO.output(4, GPIO.LOW)

if GPIO.input(14):
	GPIO.output(14, GPIO.LOW)
if GPIO.input(15):
	GPIO.output(15, GPIO.LOW)
if GPIO.input(18):
	GPIO.output(18, GPIO.LOW)
if GPIO.input(3):
	GPIO.output(3, GPIO.LOW)
if GPIO.input(4):
	GPIO.output(4, GPIO.LOW)


HOST = ""
PORT = 7777
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('socket created')
s.bind((HOST, PORT))
print('Socket bind complete')
s.listen(1)
print('socket now listening...')

hand_thread = threading.Thread(target=hand);
hand_thread.setDaemon(True)
hand_thread.start()

while True:
	conn, addr = s.accept()
	print("connected by", addr)
	
	data = conn.recv(1024)
	data = data.decode().strip()
	if data == 'end':
		break
	elif not data:
		break
	
	print("received: " + data)
	navigation(data)
	res = data + "complete"
	conn.sendall(res.encode())
	conn.close()

s.close()
GPIO.cleanup()

