/* A suite of tests for the ModelRunParametersViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/admin/diseasegroup/ModelRunParametersViewModel"
], function (ko, _, ModelRunParametersViewModel) {
    "use strict";

    describe("The 'model run parameters' view model", function () {
        var eventName = "disease-group-selected";

        it("holds the expected properties of a disease group as observables", function () {
            var vm = new ModelRunParametersViewModel("");
            expect(vm.minNewOccurrences).toBeObservable();
            expect(vm.minDataVolume).toBeObservable();
            expect(vm.minDistinctCountries).toBeObservable();
            expect(vm.minHighFrequencyCountries).toBeObservable();
            expect(vm.highFrequencyThreshold).toBeObservable();
            expect(vm.occursInAfrica).toBeObservable();
        });

        it("updates the disease group property fields, when the specified event is fired", function () {
            // Arrange
            var diseaseGroup = {
                minNewOccurrences: "1",
                minDataVolume: "2",
                minDistinctCountries: "3",
                minHighFrequencyCountries: "4",
                highFrequencyThreshold: "5",
                occursInAfrica: true
            };
            var vm = new ModelRunParametersViewModel(eventName);

            // Act
            ko.postbox.publish(eventName, diseaseGroup);

            // Assert
            expect(vm.minNewOccurrences()).toBe(diseaseGroup.minNewOccurrences);
            expect(vm.minDataVolume()).toBe(diseaseGroup.minDataVolume);
            expect(vm.minDistinctCountries()).toBe(diseaseGroup.minDistinctCountries);
            expect(vm.minHighFrequencyCountries()).toBe(diseaseGroup.minHighFrequencyCountries);
            expect(vm.highFrequencyThreshold()).toBe(diseaseGroup.highFrequencyThreshold);
            expect(vm.occursInAfrica()).toBe(diseaseGroup.occursInAfrica);
        });

        describe("holds the computed 'high frequency threshold' which", function () {
            it("returns the 'high frequency threshold' value when disease group occurs in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(eventName);
                var value = 1;
                vm.highFrequencyThresholdValue(value);
                // Act
                vm.occursInAfrica(true);
                // Assert
                expect(vm.highFrequencyThreshold()).toBe(value);
            });

            it("returns null when disease group does not occur in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(eventName);
                var value = 1;
                vm.highFrequencyThresholdValue(value);
                // Act
                vm.occursInAfrica(false);
                // Assert
                expect(vm.highFrequencyThreshold()).toBe(null);
            });
        });

        describe("holds the computed 'min high frequency countries' which", function () {
            it("returns the 'min high frequency countries' value when disease group occurs in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(eventName);
                var value = 1;
                vm.minHighFrequencyCountriesValue(value);
                // Act
                vm.occursInAfrica(true);
                // Assert
                expect(vm.minHighFrequencyCountries()).toBe(value);
            });

            it("returns null when disease group does not occur in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(eventName);
                var value = 1;
                vm.minHighFrequencyCountriesValue(value);
                // Act
                vm.occursInAfrica(false);
                // Assert
                expect(vm.minHighFrequencyCountries()).toBe(null);
            });
        });
    });
});
