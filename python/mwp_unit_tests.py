import unittest
import machine_weighting_predictor as mwp
import numpy as np

es = 'environmentalSuitability'
dfe = 'distanceFromExtent'
feed = 'feedId'

class TestMachineWeightingPredictor(unittest.TestCase):

    def test_construct_feed_classes(self):
        """
        Dictionary contains unique feedIds mapped to a column number
        """
        # Arrange
        data = [{feed: 71}, {feed: 71}, {feed: 102}, {feed: 6}]
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
        json = [{es: 0.8, dfe: -1000, feed: 123}, {es: 0.2, dfe: 200, feed: 456}]
        feed_classes = { 123: 0, 456: 1 }
        expected_X = np.array([[0.8, -1000, 1, 0], [0.2, 200, 0, 1]])
        # Act
        X = mwp._convert_training_data_to_matrix(json, feed_classes)
        # Assert
        self.assertEqual(X.all(), expected_X.all())

    def test_convert_data_to_vector(self):
        """
        Features are extracted into columns
        """
        # Arrange
        feed_classes = { 123: 0, 456: 1 }
        request1 = {es: 0.25, dfe: 101, feed: 123} # Feed id in feed_classes
        request2 = {es: 0.50, dfe: 102, feed: 789} # Feed not previously seen during training
        request3 = {es: 0.75, dfe: 103, feed: 987} # Feed not previously seen during training
        # Act
        x1 = mwp._convert_data_to_vector(request1, feed_classes)
        x2 = mwp._convert_data_to_vector(request2, feed_classes)
        x3 = mwp._convert_data_to_vector(request3, feed_classes)
        # Assert
        self.assertEqual(x1.all(), np.array([0.25, 101, 1, 0]).all()) # 1 in expected column according to feed_classes
        self.assertEqual(x2.all(), np.array([0.50, 102, 0, 0]).all()) # 0s in all columns; no column for new feed
        self.assertEqual(x3.all(), np.array([0.75, 103, 0, 0]).all()) # 0s in all columns; no column for another new feed

suite = unittest.TestLoader().loadTestsFromTestCase(TestMachineWeightingPredictor)
unittest.TextTestRunner(verbosity=2).run(suite)
