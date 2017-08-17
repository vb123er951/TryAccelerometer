import matplotlib.pyplot as plt

time = []
X = []
Y = []
Z = []
headers = []

f = open('/home/nol/output.txt', 'r')
header = f.readline()
headers = header.split(',')
first = True
for l in f.readlines():
	line = l.split(',')
	if first:
		firstTime = long(line[0])
		first = False
	time.append(long(line[0]) - firstTime)
	X.append(line[1])
	Y.append(line[2])
	Z.append(line[3])
f.close()

plt.plot(time, X, label='x')
plt.plot(time, Y, label='y')
plt.plot(time, Z, label='z')
plt.xlabel('Time')
plt.legend()
plt.show()
