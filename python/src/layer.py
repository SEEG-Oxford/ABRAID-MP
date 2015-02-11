"""
Module defining the Layer class of the ABRAID Ensemble Chain
structure, and its supporting functions.
"""
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

def _get_prediction(p, x):
    """ Return the probability that x belongs to the valid class '1' """
    return p.predict_proba(x)[0][np.where(p.classes_ == 1)[0][0]]

def _coefficient_of_variation(args):
    mu = np.mean(args)
    if mu == 0:
        return np.std(args)
    else:
        return np.std(args) / mu


class Layer(object):
    """ A Layer of predictors, each trained on a random subset of training data. """

    num_predictors_in_layer = 6
    subset_size = 0.4
    cv_threshold = 0.01

    def __init__(self, X, y):
        self.predictors = [linear_model.LogisticRegression(C=1e5) for i in range(self.num_predictors_in_layer)]
        self.train(X, y)

    def train(self, X, y):
        """ Train each predictor in the Layer on a random subset of the training data. """
        for p in self.predictors:
            (X_subset, y_subset) = _get_random_subset(self.subset_size, X, y)
            p.fit(X_subset, y_subset)

    def predict(self, x):
        """ Return the mean prediction, if the predictors are sufficiently in agreement. """
        predictions = [_get_prediction(p, x) for p in self.predictors]
        if _coefficient_of_variation(predictions) <= self.cv_threshold:
            return np.mean(predictions)
        else:
            return None
