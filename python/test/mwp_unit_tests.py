import contextlib
import numpy as np
import os
import shutil
import tempfile
import unittest

# <Path to>/ABRAID-MP/python must be in your PYTHONPATH.
from src import machine_weighting_predictor as mwp

DFE = 'distanceFromExtent'
ES = 'environmentalSuitability'
FEED = 'feedId'

PICKLES_SUBFOLDER_PATH = 'pickles/'

@contextlib.contextmanager
def move_to_temp_directory():
    current_dir = os.getcwd()
    temp_dir = tempfile.mkdtemp()
    os.chdir(temp_dir)
    os.makedirs(temp_dir + '/pickles')
    yield temp_dir
    os.chdir(current_dir)
    shutil.rmtree(temp_dir)

class TestMachineWeightingPredictor(unittest.TestCase):

    def test_construct_feed_classes(self):
        """
        Dictionary contains unique feedIds mapped to a column number
        """
        # Arrange
        data = [{FEED: 71}, {FEED: 71}, {FEED: 102}, {FEED: 6}]
        # Act
        feed_classes = mwp._construct_feed_classes(data)
        # Assert
        self.assertItemsEqual(feed_classes.keys(), [71, 102, 6])
        self.assertItemsEqual(feed_classes.values(), range(3))

    def test_convert_training_data_to_matrix(self):
        """
        Features are extracted into columns, each datapoint corresponds to a row in X
        """
        # Arrange
        json = [{ES: 0.8, DFE: -1000, FEED: 123}, {ES: 0.2, DFE: 200, FEED: 456}]
        feed_classes = {123: 0, 456: 1}
        expected_X = np.array([[0.8, -1000, 1, 0], [0.2, 200, 0, 1]])
        # Act
        X = mwp._convert_training_data_to_matrix(json, feed_classes)
        # Assert
        self.assertEqual(X.all(), expected_X.all())

    def test_convert_training_data_to_matrix_throws_key_error(self):
        # Arrange
        json_no_es = [{DFE: -1000, FEED: 123}]
        json_no_dfe = [{ES: 0.8, FEED: 123}]
        json_no_feed = [{ES: 0.8, DFE: -1000}]
        feed_classes = {123: 0}
        # Assert
        self.assertRaises(KeyError, mwp._convert_training_data_to_matrix, json_no_es, feed_classes)
        self.assertRaises(KeyError, mwp._convert_training_data_to_matrix, json_no_dfe, feed_classes)
        self.assertRaises(KeyError, mwp._convert_training_data_to_matrix, json_no_feed, feed_classes)

    def test_convert_data_to_vector(self):
        """
        Features are extracted into columns for one datapoint row
        """
        # Arrange
        feed_classes = {123: 0, 456: 1}
        data1 = {ES: 0.25, DFE: 101, FEED: 123} # Feed id in feed_classes
        data2 = {ES: 0.50, DFE: 102, FEED: 789} # Feed not previously seen during training
        data3 = {ES: 0.75, DFE: 103, FEED: 987} # Feed not previously seen during training
        # Act
        x1 = mwp._convert_data_to_vector(data1, feed_classes)
        x2 = mwp._convert_data_to_vector(data2, feed_classes)
        x3 = mwp._convert_data_to_vector(data3, feed_classes)
        # Assert
        self.assertItemsEqual(x1, np.array([0.25, 101, 1, 0])) # 1 in expected column according to feed_classes
        self.assertItemsEqual(x2, np.array([0.50, 102, 0, 0])) # 0s in all columns; no column for new feed
        self.assertItemsEqual(x3, np.array([0.75, 103, 0, 0])) # 0s in all columns; no column for another new feed

    def test_convert_data_to_vector_throws_key_error(self):
        # Arrange
        feed_classes = {123: 0, 456: 1}
        data1 = {DFE: 101, FEED: 123}
        data2 = {ES: 0.50, FEED: 789}
        data3 = {ES: 0.75, DFE: 103}
        # Act
        self.assertRaises(KeyError, mwp._convert_data_to_vector, data1, feed_classes)
        self.assertRaises(KeyError, mwp._convert_data_to_vector, data2, feed_classes)
        self.assertRaises(KeyError, mwp._convert_data_to_vector, data3, feed_classes)

    def test_pluck(self):
        """
        The specified feature is extracted from dict as an array
        """
        # Arrange
        feature = "distanceFromExtent"
        data = [{feature: 1}, {feature: 2}, {feature: 3}]
        # Act
        result = mwp._pluck(feature, data)
        # Assert
        self.assertEqual(result, [1, 2, 3])

    def test_get_pickled_predictor_filename(self):
        """
        The expected file name is concatenated with disease_group_id
        """
        # Arrange
        disease_group_id = 87
        expected_filename = PICKLES_SUBFOLDER_PATH + '87_predictor.pkl'
        # Act
        filename = mwp._get_pickled_predictor_filename(disease_group_id)
        # Assert
        self.assertEqual(filename, expected_filename)

    def test_get_pickled_feed_classes_filename(self):
        """
        The expected file name is concatenated with disease_group_id
        """
        # Arrange
        disease_group_id = 87
        expected_filename = PICKLES_SUBFOLDER_PATH + '87_feed_classes.pkl'
        # Act
        filename = mwp._get_pickled_feed_classes_filename(disease_group_id)
        # Assert
        self.assertEqual(filename, expected_filename)

    def test_save_pickles(self):
        """
        The predictor and feed_classes dict is saved to global dict, and pickled to expected location
        """
        with move_to_temp_directory() as tempdir:
            # Arrange
            disease_group_id = 1
            predictor = 'predictor'
            feed_classes = {123: 0}
            # Act
            mwp._save_pickles(disease_group_id, predictor, feed_classes)
            # Assert
            self.assertEqual(mwp.PREDICTORS[disease_group_id], predictor)
            self.assertEqual(mwp.FEED_CLASSES[disease_group_id], feed_classes)
            self.assertTrue(os.path.isfile(tempdir + '/' + mwp._get_pickled_feed_classes_filename(disease_group_id)))
            self.assertTrue(os.path.isfile(tempdir + '/' + mwp._get_pickled_predictor_filename(disease_group_id)))

    def test_log_response(self):
        """
        Message is appended with disease group clause and returned with status code
        """
        # Arrange
        expected_response = ("error for disease group (87)", 500)
        # Act
        response = mwp._log_response("error", 87, 500)
        # Assert
        self.assertEqual(response, expected_response)


suite = unittest.TestLoader().loadTestsFromTestCase(TestMachineWeightingPredictor)
unittest.TextTestRunner(verbosity=2).run(suite)
