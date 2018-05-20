import sys
import math
import random
import pylab
import numpy as np

# this script requires a single command line argument:
# degree of the polynomial, otherwise it fits a straight line
degree = 1

if len(sys.argv) >= 2:
  degree = int(sys.argv[1])

# the function that we want to fit
def f(x):
    return math.sin(2*math.pi*x)

# plot f
xs = np.linspace(0.0, 1.0, num=1000)
ys = [f(x) for x in xs]
pylab.axis([0.0, 1.0, -2, 2])
ax = pylab.gca()
ax.set_autoscale_on(False)
pylab.plot(xs, ys)

# randomly choose data points from [0; 1) and fit a polynomial of
# degree d to this data
repetitions = 10
num_points = 10

for n in range(repetitions):
    # select data points
    xData = [random.random() for i in range(num_points)]
    yData = [f(x) for x in xData]

    # fit polynomial
    coefficients = np.polyfit(xData, yData, degree)
    polynomial = np.poly1d(coefficients)
    fitted = polynomial(xData)

    # plot polynomial
    xs = np.linspace(0.0, 1.0, num=1000)
    ys = polynomial(xs)
    pylab.plot(xs, ys, "r-")

pylab.show()
