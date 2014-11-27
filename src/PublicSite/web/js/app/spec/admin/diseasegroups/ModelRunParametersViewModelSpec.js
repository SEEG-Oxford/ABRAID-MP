/* A suite of tests for the ModelRunParametersViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/admin/diseasegroups/ModelRunParametersViewModel"
], function (ko, ModelRunParametersViewModel) {
    "use strict";

    var expectValidationRulesForNonNegativeInteger = function (arg) {
        expect(arg).toHaveValidationRule({name: "digit", params: true});
        expect(arg).toHaveValidationRule({name: "min", params: 0});
    };

    var expectValidationRulesForPositiveInteger = function (arg) {
        expect(arg).toHaveValidationRule({name: "digit", params: true});
        expect(arg).toHaveValidationRule({name: "min", params: 1});
    };

    var expectValidationRulesForNumberBetweenZeroAndOne = function (arg) {
        expect(arg).toHaveValidationRule({name: "number", params: true});
        expect(arg).toHaveValidationRule({name: "min", params: 0});
        expect(arg).toHaveValidationRule({name: "max", params: 1});
    };

    var baseUrl = "http://abraid.com/publicsite/";

    describe("The 'model run parameters' view model", function () {
        var eventName = "disease-group-selected";

        describe("holds the expected properties of a disease group", function () {
            var vm = new ModelRunParametersViewModel(baseUrl, "");
            it("as observables", function () {
                expect(vm.diseaseGroupId).toBeObservable();
                expect(vm.minNewLocations).toBeObservable();
                expect(vm.minEnvironmentalSuitability).toBeObservable();
                expect(vm.minDistanceFromDiseaseExtent).toBeObservable();
                expect(vm.minDataVolume).toBeObservable();
                expect(vm.minDistinctCountries).toBeObservable();
                expect(vm.minHighFrequencyCountries).toBeObservable();
                expect(vm.highFrequencyThreshold).toBeObservable();
                expect(vm.occursInAfrica).toBeObservable();
                expect(vm.useMachineLearning).toBeObservable();
                expect(vm.maxEnvironmentalSuitabilityWithoutML).toBeObservable();
            });

            it("with appropriate validation rules", function () {
                expectValidationRulesForNonNegativeInteger(vm.minNewLocations);
                expectValidationRulesForPositiveInteger(vm.minDataVolume);
                expectValidationRulesForNonNegativeInteger(vm.minDistinctCountries);
                expectValidationRulesForNonNegativeInteger(vm.minHighFrequencyCountries);
                expectValidationRulesForNonNegativeInteger(vm.highFrequencyThreshold);

                expectValidationRulesForNumberBetweenZeroAndOne(vm.minEnvironmentalSuitability);
                expectValidationRulesForNumberBetweenZeroAndOne(vm.maxEnvironmentalSuitabilityWithoutML);

                expect(vm.minDistanceFromDiseaseExtent).toHaveValidationRule({name: "number", params: true});
                expect(vm.minDataVolume).toHaveValidationRule({name: "required", params: true});
            });
        });

        describe("when the specified event is fired", function () {
            it("updates the disease group property fields", function () {
                // Arrange
                var diseaseGroup = {
                    id: "50",
                    minNewLocations: "1",
                    minEnvironmentalSuitability: "0.2",
                    minDistanceFromDiseaseExtent: "-300",
                    minDataVolume: "2",
                    minDistinctCountries: "3",
                    minHighFrequencyCountries: "4",
                    highFrequencyThreshold: "5",
                    occursInAfrica: true,
                    useMachineLearning: true,
                    maxEnvironmentalSuitabilityWithoutML: "0.7"
                };
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);

                // Act
                ko.postbox.publish(eventName, diseaseGroup);

                // Assert
                expect(vm.diseaseGroupId()).toBe(diseaseGroup.id);
                expect(vm.minNewLocations()).toBe(diseaseGroup.minNewLocations);
                expect(vm.minEnvironmentalSuitability()).toBe(diseaseGroup.minEnvironmentalSuitability);
                expect(vm.minDistanceFromDiseaseExtent()).toBe(diseaseGroup.minDistanceFromDiseaseExtent);
                expect(vm.minDataVolume()).toBe(diseaseGroup.minDataVolume);
                expect(vm.minDistinctCountries()).toBe(diseaseGroup.minDistinctCountries);
                expect(vm.minHighFrequencyCountries()).toBe(diseaseGroup.minHighFrequencyCountries);
                expect(vm.highFrequencyThreshold()).toBe(diseaseGroup.highFrequencyThreshold);
                expect(vm.occursInAfrica()).toBe(diseaseGroup.occursInAfrica);
                expect(vm.useMachineLearning()).toBe(diseaseGroup.useMachineLearning);
                expect(vm.maxEnvironmentalSuitabilityWithoutML()).toBe(
                    vm.useMachineLearning() ? null : diseaseGroup.maxEnvironmentalSuitabilityWithoutML
                );
                expect(vm.diseaseOccurrenceSpreadUrl()).toBe(
                    "http://abraid.com/publicsite/admin/diseases/50/spread");
                expect(vm.diseaseOccurrenceSpreadButtonEnabled()).toBe(true);
            });

            it("updates the disease group property fields, handling undefined/null/0 etc. appropriately", function () {
                // Arrange
                var diseaseGroup = {
                    id: undefined,
                    minNewLocations: null,
                    minEnvironmentalSuitability: undefined,
                    minDistanceFromDiseaseExtent: 0,
                    minDataVolume: "",
                    minDistinctCountries: NaN,
                    minHighFrequencyCountries: undefined,
                    highFrequencyThreshold: 0,
                    occursInAfrica: true,
                    useMachineLearning: false,
                    maxEnvironmentalSuitabilityWithoutML: ""
                };
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);

                // Act
                ko.postbox.publish(eventName, diseaseGroup);

                // Assert
                expect(vm.diseaseGroupId()).toBe("");
                expect(vm.minNewLocations()).toBe("");
                expect(vm.minEnvironmentalSuitability()).toBe("");
                expect(vm.minDistanceFromDiseaseExtent()).toBe(0);
                expect(vm.minDataVolume()).toBe("");
                expect(vm.minDistinctCountries()).toBe("");
                expect(vm.minHighFrequencyCountries()).toBe("");
                expect(vm.highFrequencyThreshold()).toBe(0);
                expect(vm.occursInAfrica()).toBe(diseaseGroup.occursInAfrica);
                expect(vm.useMachineLearning()).toBe(diseaseGroup.useMachineLearning);
                expect(vm.maxEnvironmentalSuitabilityWithoutML()).toBe("");
                expect(vm.diseaseOccurrenceSpreadButtonEnabled()).toBe(false);
            });
        });

        describe("holds the computed 'high frequency threshold' which", function () {
            it("returns the 'high frequency threshold' value when disease group occurs in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);
                var value = 1;
                vm.highFrequencyThresholdValue(value);
                // Act
                vm.occursInAfrica(true);
                // Assert
                expect(vm.highFrequencyThreshold()).toBe(value);
            });

            it("returns null when disease group does not occur in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);
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
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);
                var value = 1;
                vm.minHighFrequencyCountriesValue(value);
                // Act
                vm.occursInAfrica(true);
                // Assert
                expect(vm.minHighFrequencyCountries()).toBe(value);
            });

            it("returns null when disease group does not occur in Africa", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel(baseUrl, eventName);
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
