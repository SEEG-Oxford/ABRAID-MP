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
    FEED_CLASSES[disease_group_id] = {}

    if len(data) > 50:
        try:
            X = _convert_json_to_matrix(disease_group_id, data)
            y = np.array(_pluck('expertWeighting', data))
        except KeyError:
            return _log_response('Invalid JSON', disease_group_id, 400)
        else:
            predictor.train(X, y)
            _save_predictor(disease_group_id, predictor)
            return _log_response('Trained predictor saved', disease_group_id, 200)
    else:
        _save_predictor(disease_group_id, predictor)
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
        x = np.zeros(2 + len(feed_classes) + 1)
        x[0] = request.json['environmentalSuitability']
        x[1] = request.json['distanceFromExtent']
        feed = _get_feed_class(disease_group_id, request.json['feedId'])
        x[feed + 2] = 1
    except KeyError:
        return _log_response('Invalid JSON', disease_group_id, 400)

    prediction = predictor.predict(x)
    if prediction is None:
        return ('No prediction', 200)
    else:
        return (str(prediction), 200)


def _convert_json_to_matrix(disease_group_id, json):
    feeds = [_get_feed_class(disease_group_id, feed_id) for feed_id in _pluck('feedId', json)]
    n = 2 + len(FEED_CLASSES[disease_group_id]) + 1
    X = np.zeros((len(json), n))

    X[:, 0] = _pluck('environmentalSuitability', json)
    X[:, 1] = _pluck('distanceFromExtent', json)
    for i, f in enumerate(feeds):
        X[i, f + 2] = 1
    return X


def _get_feed_class(disease_group_id, feed_id):
    """ Map from feed id (which could be any integer and skew the data) to an incremental class number """
    if feed_id not in FEED_CLASSES[disease_group_id]:
        FEED_CLASSES[disease_group_id][feed_id] = len(FEED_CLASSES[disease_group_id])
    return FEED_CLASSES[disease_group_id][feed_id]


def _pluck(name, json):
    """ Extract the named feature from each item in json, as an array """
    return [x[name] for x in json]


def _save_predictor(disease_group_id, predictor):
    """ Save to dict, and back up a pickled version on disk """
    PREDICTORS[disease_group_id] = predictor
    try:
        joblib.dump(predictor, _get_pickled_predictor_filename(disease_group_id))
        joblib.dump(FEED_CLASSES[disease_group_id], _get_pickled_feed_classes_filename(disease_group_id))
    except IOError as e:
        app.logger.error(e.strerror + ': Unable to save pickle for disease group (' + str(disease_group_id) + ')')


def _get_pickled_predictor_filename(disease_group_id):
    return PICKLES_SUBFOLDER_PATH + str(disease_group_id) + '_predictor.pkl'


def _get_pickled_feed_classes_filename(disease_group_id):
    return PICKLES_SUBFOLDER_PATH + str(disease_group_id) + '_feed_classes.pkl'


def _log_response(message, disease_group_id, status_code):
    message = message + ' for disease group (' + str(disease_group_id) + ')'
    if status_code == 400:
        app.logger.error(message)
    else:
        app.logger.info(message)
    return (message, status_code)


if __name__ == '__main__':
    app.run(host='localhost', debug=True, use_debugger=True)

