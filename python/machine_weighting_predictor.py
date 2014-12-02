"""
Entry point for Python service, to which data is POSTed to train the ABRAID Ensemble Chain,
and from which a prediction for a new datapoint is requested.
"""
from chain import Chain
from flask import Flask, request
from sklearn.externals import joblib
import logging
import numpy as np

app = Flask(__name__)

PREDICTORS = {}
FEED_CLASSES = {}
PICKLES_SUBFOLDER_PATH = 'pickles/'

# Replace Flask's existing log handlers (debug and prod) with one custom format handler
del app.logger.handlers[:]
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter(
    '%(asctime)s [%(process)d] [%(levelname)s] [%(filename)s:%(lineno)d]: %(message)s'
))
app.logger.addHandler(handler)
app.logger.setLevel(logging.DEBUG)

@app.route('/<int:disease_group_id>/train', methods=['POST'])
def train(disease_group_id):
    """ Use data extracted from request JSON to create structure in a training phase. """

    try:
        data = request.json['points']
    except KeyError:
        return _log_response('Invalid JSON', disease_group_id, 400)

    predictor = Chain()
    feed_classes = {}

    if len(data) > 50:
        try:
            feed_classes = _construct_feed_classes(data)
            X = _convert_training_data_to_matrix(data, feed_classes)
            y = np.array(_pluck('expertWeighting', data))
        except KeyError:
            return _log_response('Invalid JSON', disease_group_id, 400)
        else:
            predictor.train(X, y)
            _save_pickles(disease_group_id, predictor, feed_classes)
            return _log_response('Trained predictor saved', disease_group_id, 200)
    else:
        _save_pickles(disease_group_id, predictor, feed_classes)
        return _log_response('Insufficient training data - empty predictor saved', disease_group_id, 200)


@app.route('/<int:disease_group_id>/predict', methods=['POST'])
def predict(disease_group_id):
    """ Return the prediction of the provided disease occurrence point. """

    # Use the predictor in memory, otherwise load from backup pickle version
    if disease_group_id in PREDICTORS:
        predictor = PREDICTORS[disease_group_id]
    else:
        try:
            filename = _get_pickled_predictor_filename(disease_group_id)
            predictor = joblib.load(filename)
            PREDICTORS[disease_group_id] = predictor
        except IOError as e:
            return _log_response(e.strerror + ': Unable to load predictor', disease_group_id, 400)

    # Use the feed classes map in memory, otherwise load from backup pickle version
    if disease_group_id in FEED_CLASSES:
        feed_classes = FEED_CLASSES[disease_group_id]
    else:
        try:
            filename = _get_pickled_feed_classes_filename(disease_group_id)
            feed_classes = joblib.load(filename)
            FEED_CLASSES[disease_group_id] = feed_classes
        except IOError as e:
            return _log_response(e.strerror + ': Unable to load feeds', disease_group_id, 400)

    try:
        data = request.get_json()
        x = _convert_data_to_vector(data, feed_classes)
    except KeyError:
        return _log_response('Invalid JSON', disease_group_id, 400)

    prediction = predictor.predict(x)
    if prediction is None:
        return ('No prediction', 200)
    else:
        return (str(prediction), 200)


def _construct_feed_classes(data):
    """ Create a dictionary mapping from each feedId in training data, to an incremental class number """
    feed_classes = {}
    for feed_id in _pluck('feedId', data):
        if feed_id not in feed_classes:
            feed_classes[feed_id] = len(feed_classes)
    return feed_classes


def _convert_training_data_to_matrix(json, feed_classes):
    """ Columns of X represent features eg: [0.9, 123, 0, 1, 0] for a datapoint coming from the second of 3 feeds """
    n = 2 + len(feed_classes)
    m = len(json)
    X = np.zeros((m, n))

    X[:, 0] = _pluck('environmentalSuitability', json) # A probability between 0 and 1
    X[:, 1] = _pluck('distanceFromExtent', json)       # A value in km
    
    # Each unique feed_id maps to a column number, where a 1 represents the occurrence alert came from that feed.
    feeds = [feed_classes[feed_id] for feed_id in _pluck('feedId', json)]
    for i, feed_class in enumerate(feeds):
        X[i, feed_class + 2] = 1
    return X


def _convert_data_to_vector(data, feed_classes):
    n = 2 + len(feed_classes)
    x = np.zeros(n)
    
    x[0] = data['environmentalSuitability']
    x[1] = data['distanceFromExtent']

    feed_id = data['feedId']
    if feed_id in feed_classes:
        feed_class = feed_classes[feed_id]
        x[feed_class + 2] = 1
    return x

    
def _pluck(name, data):
    """ Extract the named feature from each item in data, as an array """
    return [x[name] for x in data]


def _save_pickles(disease_group_id, predictor, feed_classes):
    """ Save to global dict, and back up a pickled version on disk """
    PREDICTORS[disease_group_id] = predictor
    FEED_CLASSES[disease_group_id] = feed_classes
    try:
        joblib.dump(predictor, _get_pickled_predictor_filename(disease_group_id))
        joblib.dump(feed_classes, _get_pickled_feed_classes_filename(disease_group_id))
    except IOError as e:
        app.logger.error(e.strerror + ': Unable to save pickle for disease group (' + str(disease_group_id) + ')')


def _get_pickled_predictor_filename(disease_group_id):
    return PICKLES_SUBFOLDER_PATH + str(disease_group_id) + '_predictor.pkl'


def _get_pickled_feed_classes_filename(disease_group_id):
    return PICKLES_SUBFOLDER_PATH + str(disease_group_id) + '_feed_classes.pkl'


def _log_response(message, disease_group_id, status_code):
    message = message + ' for disease group (' + str(disease_group_id) + ')'
    if status_code >= 500:
        app.logger.error(message)
    elif status_code >= 400:
        app.logger.warn(message)
    else:
        app.logger.info(message)
    
    return (message, status_code)


if __name__ == '__main__':
    app.run(host='localhost')
