"""
Module defining the Chain class of the ABRAID Ensemble Chain.
"""
from __future__ import division
from layer import Layer
import numpy as np


class Chain(object):

    def __init__(self):
        self.layers = []

    def train(self, X, y):
        """
        Iteratively construct the multi-layer chain. Datapoints for which
        a trusted prediction could not be found fall through to next layer.
        """
        data = X.copy()
        labels = y.copy()
        count = 0
        while count < 5:
            try:
                layer = Layer(data, labels)
            except ValueError:
                """ The number of classes has to be greater than one. """
                break
            else:
                self.layers.append(layer)

                results = [layer.predict(x) for x in data]
                to_next_layer = [i for (i, r) in enumerate(results) if r is None]

                data = np.array(data[to_next_layer])
                labels = np.array(labels[to_next_layer])
                count += 1

    def predict(self, x):
        """ Iterate through the layers until a trusted prediction is found. """
        for layer in self.layers:
            prediction = layer.predict(x)
            if prediction is not None:
                return prediction
        return None
