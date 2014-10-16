from __future__ import division
from math import floor
from random import shuffle
from sklearn import linear_model
import numpy as np


def _get_random_subset(subset_size, X, y):
    indexes = _get_random_subset_indexes(subset_size, len(X))
    return (X[indexes], y[indexes])


def _get_random_subset_indexes(subset_size, n):
    list_of_indexes = range(n)
    shuffle(list_of_indexes)
    k = int(floor(subset_size * n))
    return list_of_indexes[:k]


def _coefficient_of_variation(args):
    return np.std(args) / np.mean(args)


class Layer(object):

    num_predictors_in_layer = 6
    subset_size = 0.4
    cv_threshold = 0.01

    def __init__(self, X, y):
        self.predictors = [linear_model.LogisticRegression(C=1e5) for i in range(self.num_predictors_in_layer)]
        self.train(X, y)

    def train(self, X, y):
        for p in self.predictors:
            (X_subset, y_subset) = _get_random_subset(self.subset_size, X, y)
            p.fit(X_subset, y_subset)

    def predict(self, x):
        predictions = [p.predict(x)[0] for p in self.predictors]
        if _coefficient_of_variation(predictions) <= self.cv_threshold:
            return np.mean(predictions)
        else:
            return None
