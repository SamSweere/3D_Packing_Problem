import matplotlib.pyplot as plt
import numpy as np
from mpl_toolkits.mplot3d import Axes3D

# cargo container
container = np.full((8, 8, 8), False)
colors = np.empty(container.shape, dtype=object)

# there is a own array in a line for every parcel/pentominoe
# every element of a parcel is a cube consisting of a tuple (x, y, z)
parcels = [
    [(0, 0, 0), (1, 0, 0), (0, 0, 1), (0, 0, 2), (0, 0, 3)],
    [(4, 4, 0), (4, 4, 1), (4, 4, 2), (3, 4, 2), (5, 4, 2)],
    [(3, 4, 3), (4, 4, 3), (5, 4, 3), (3, 4, 4), (5, 4, 4)]
]

for parcel in parcels:
    # choose a random color with the format (R, G, B, A)
    color = np.append(np.random.rand(3), [1])
    for cube in parcel:
        container[cube[0]][cube[1]][cube[2]] = True
        colors[cube[0]][cube[1]][cube[2]] = color

# create the figure
fig = plt.figure()
ax = Axes3D(fig)
ax.voxels(container, facecolors=colors, edgecolor='k')
ax.set_xlim(0, len(container))
ax.set_ylim(0, len(container[0]))
ax.set_zlim(0, len(container[0][0]))
ax.set_xlabel('x')
ax.set_ylabel('y')
ax.set_zlabel('z')

plt.show()
