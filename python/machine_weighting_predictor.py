from chain import Chain
from flask import Flask, request
from sklearn.externals import joblib
import numpy as np

app = Flask(__name__)

PREDICTORS = {}
FEED_CLASSES = {}


@app.route('/<int:disease_group_id>/train', methods=['POST'])
def train(disease_group_id):
    """ Use data extracted from request JSON to create structure in a training phase """

    try:
        data = request.json['points']
    except KeyError:
        return ('Invalid JSON', 400)
    else:
        predictor = Chain()

    if (len(data) == 0):
        save_predictor(disease_group_id, predictor)
        return ('No training data', 200)

    try:
        X = _convert_json_to_matrix(disease_group_id, data)
        y = np.array(_pluck('expertWeighting', data))
    except KeyError:
        return ('Invalid JSON', 400)
    else:
        predictor.train(X,y)
        save_predictor(disease_group_id, predictor)
        return ('', 204)

def save_predictor(disease_group_id, predictor):    
    PREDICTORS[disease_group_id] = predictor

    # Backup a pickled version on disk
    joblib.dump(predictor, _get_pickled_predictor_filename(disease_group_id))
    joblib.dump(FEED_CLASSES, _get_pickled_feed_classes_filename(disease_group_id))

@app.route('/<int:disease_group_id>/predict', methods=['POST'])
def predict(disease_group_id):
    """ Return the prediction of the provided disease occurrence point """

    # Use the predictor in memory, otherwise load from backup pickle version
    if disease_group_id in PREDICTORS:
        predictor = PREDICTORS[disease_group_id]
    else:
        try:
            filename = _get_pickled_predictor_filename(disease_group_id)
            predictor = joblib.load(filename)
        except Exception:
            return ('Unable to load predictor for disease group', 400)

    # Use the feed classes map in memory, otherwise load from backup pickle version
    if disease_group_id in FEED_CLASSES:
        feed_classes = FEED_CLASSES[disease_group_id]
    else:
        try:
            filename = _get_pickled_feed_classes_filename(disease_group_id)
            feed_classes = joblib.load(filename)
        except Exception:
            return ('Unable to load feeds for disease group', 400)

    try:
        x = np.zeros(2 + len(feed_classes) + 1)
        x[0] = request.json['environmentalSuitability']
        x[1] = request.json['distanceFromExtent']
        feed = _get_feed_class(disease_group_id, request.json['feedId'])
        x[feed + 2] = 1
    except KeyError:
        return ('Invalid JSON', 400)

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
    if disease_group_id not in FEED_CLASSES:
        FEED_CLASSES[disease_group_id] = {}        
    if feed_id not in FEED_CLASSES[disease_group_id]:
        FEED_CLASSES[disease_group_id][feed_id] = len(FEED_CLASSES[disease_group_id])
    return FEED_CLASSES[disease_group_id][feed_id]


def _pluck(name, json):
    """ Extract the named feature from each item in json, as an array """
    return [x[name] for x in json]


def _get_pickled_predictor_filename(disease_group_id):
    return 'pickles/' + str(disease_group_id) + '_predictor.pkl'


def _get_pickled_feed_classes_filename(disease_group_id):
    return 'pickles/' + str(disease_group_id) + '_feed_classes.pkl'


if __name__ == '__main__':
    app.run(host='localhost', debug=True, use_debugger=True)

