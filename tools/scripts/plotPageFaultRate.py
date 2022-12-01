#!/usr/bin/env python3

import matplotlib.pyplot as plt
import numpy as np
import sys
import itertools

colors = itertools.cycle(["blue", "green", "red", "orange", "purple"])
labels = ['16k', '8k', '4k', '2k', '1k']
plots = []

for i in range(len(sys.argv)):
    if i == 0:
        continue
    file = open(sys.argv[i], 'r')
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
    plots.append(plt.scatter(x, y, s=s, color=next(colors)))
plt.ylabel("Page Fault Rate")
plt.ylim(0, 1)
plt.xticks([])
lgnd = plt.legend(tuple(plots), tuple(labels), loc='best', title = "page size")
for i in range(len(labels)):
    lgnd.legendHandles[i].set_sizes([10])
    
plt.savefig('result.jpg')
