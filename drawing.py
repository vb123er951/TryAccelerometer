import matplotlib.pyplot as plt
import matplotlib.cbook as cbook

fname = cbook.get_sample_data('/home/nol/output.txt', asfileobj=False)

plt.plotfile(fname, ('time', 'x', 'y', 'z'), subplots=False)
plt.show()
