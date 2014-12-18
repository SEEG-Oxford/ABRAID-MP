import contextlib
import json
import os
import shutil
import tempfile
import testdata
import unittest
from flask import url_for
from flask.ext.testing import TestCase

# <Path to>/ABRAID-MP/python must be in your PYTHONPATH.
from src import machine_weighting_predictor as mwp

DFE = 'distanceFromExtent'
ES = 'environmentalSuitability'
EW = 'expertWeighting'
FEED = 'feedId'

DISEASE_GROUP_ID = 1

class MockPredictor(object):

    def __init__(self, prediction):
        self.prediction = prediction

    def predict(self, x):
        return self.prediction

@contextlib.contextmanager
def move_to_temp_directory():
    current_dir = os.getcwd()
    temp_dir = tempfile.mkdtemp()
    os.chdir(temp_dir)
    os.makedirs(temp_dir + '/pickles')
    yield temp_dir
    os.chdir(current_dir)
    shutil.rmtree(temp_dir)

class MachineWeightingPredictorTestCase(TestCase):

    def create_app(self):
        return mwp.app

    def _post_json(self, method, json_content):
        url = url_for(method, disease_group_id=DISEASE_GROUP_ID)
        return self.client.post(url, data=json.dumps(json_content), headers=[('Content-Type', 'application/json')])

    def test_url_for(self):
        # Arrange
        method = 'train'
        expected_url = '/' + str(DISEASE_GROUP_ID) + '/' + method
        # Act
        url = url_for(method, disease_group_id=DISEASE_GROUP_ID)
        # AssertionError
        self.assertEqual(url, expected_url)

    def test_train_returns_invalid_json_for_missing_points(self):
        response = self._post_json('train', {})
        self.assert400(response)
        assert 'Invalid JSON' in response.data

    def test_train_returns_invalid_json_for_missing_feature(self):
        points = [{'unexpectedFeature':123} for i in range(50)]
        response = self._post_json('train', {'points':points})
        self.assert400(response)
        assert 'Invalid JSON' in response.data

    def test_train_saves_empty_predictor(self):
        with move_to_temp_directory():
            response = self._post_json('train', {'points':[]})
            self.assert200(response)
            assert 'Insufficient training data - empty predictor saved' in response.data

    def test_train_saves_trained_predictor(self):
        with move_to_temp_directory():
            response = self._post_json('train', {'points':testdata.points})
            self.assert200(response)
            assert 'Trained predictor saved' in response.data

    def test_predict_returns_invalid_json_for_missing_feature(self):
        mwp.PREDICTORS[DISEASE_GROUP_ID] = MockPredictor(None)
        mwp.FEED_CLASSES[DISEASE_GROUP_ID] = {}
        datapoint = {'unexpectedFeature': 456}
        response = self._post_json('predict', datapoint)
        self.assert400(response)
        assert 'Invalid JSON' in response.data

    def test_predict_returns_no_prediction(self):
        mwp.PREDICTORS[DISEASE_GROUP_ID] = MockPredictor(None)
        response = self._post_json('predict', {ES: 0.8, DFE: 150, FEED: 1})
        self.assert200(response)
        assert 'No prediction' in response.data

    def test_predict_returns_prediction(self):
        prediction = 0.8
        mwp.PREDICTORS[DISEASE_GROUP_ID] = MockPredictor(prediction)
        response = self._post_json('predict', {ES: 0.8, DFE: 150, FEED: 1})
        self.assert200(response)
        assert str(prediction) in response.data


if __name__ == '__main__':
    unittest.main(verbosity=2)
