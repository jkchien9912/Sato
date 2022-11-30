#!/usr/bin/env python3

import matplotlib.pyplot as plt
import numpy as np
import sys


file = open(sys.argv[1], 'r')
lines = file.readlines()

rates = []
for rate in lines:
    rates.append(float(rate))

indices = []
for i in range(len(rates)):
    indices.append(i)

x = np.array(indices)
y = np.array(rates)
s = [1 for i in range(x.shape[0])]
plt.scatter(x, y, s=s)
plt.ylabel("Page Fault Rate")
plt.ylim(0, 1)
plt.savefig(str(sys.argv[1]) + '.jpg')
