from __future__ import division
from layer import Layer
import numpy as np


class Chain(object):

    def __init__(self):
        self.layers = []

    def train(self, X, y):
        data = X.copy()
        labels = y.copy()
        count = 0
        while count < 5:
            layer = Layer(data, labels)
            self.layers.append(layer)

            results = [layer.predict(x) for x in data]
            to_next_layer = [i for (i, r) in enumerate(results) if r is None]

            data = np.array(data[to_next_layer])
            labels = np.array(labels[to_next_layer])
            count += 1

    def predict(self, x):
        for layer in self.layers:
            prediction = layer.predict(x)
            if prediction is not None:
                return prediction
        return None
