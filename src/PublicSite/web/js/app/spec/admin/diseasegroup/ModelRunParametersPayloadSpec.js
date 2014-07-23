/* A suite of tests for the ModelRunParametersPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/admin/diseasegroup/ModelRunParametersPayload",
    "app/admin/diseasegroup/ModelRunParametersViewModel"
], function (ko, _, ModelRunParametersPayload, ModelRunParametersViewModel) {
    "use strict";

    describe("The 'model run parameters payload'", function () {
        var diseaseGroup = {
            name: "dengue",
            publicName: "Dengue",
            minNewOccurrences: 1,
            minDataVolume: 2,
            minDistinctCountries: 3,
            minHighFrequencyCountries: 4,
            highFrequencyThreshold: 5,
            occursInAfrica: true
        };
        var expectedResult = {
            minNewOccurrences: 1,
            minDataVolume: 2,
            minDistinctCountries: 3,
            minHighFrequencyCountries: 4,
            highFrequencyThreshold: 5,
            occursInAfrica: true
        };

        it("holds the 'fromJson' method which returns the expected payload", function () {
            // Act
            var result = ModelRunParametersPayload.fromJson(diseaseGroup);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });

        it("holds the 'fromViewModel' method which returns the expected payload", function () {
            // Arrange
            var eventName = "event";
            var vm = new ModelRunParametersViewModel("", eventName);
            ko.postbox.publish(eventName, diseaseGroup);
            // Act
            var result = ModelRunParametersPayload.fromViewModel(vm);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });
    });
});
