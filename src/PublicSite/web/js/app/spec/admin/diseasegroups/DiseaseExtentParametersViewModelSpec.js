/* A suite of tests for the DiseaseExtentParametersViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/admin/diseasegroups/DiseaseExtentParametersViewModel"
], function (ko, DiseaseExtentParametersViewModel) {
    "use strict";

    var constructDiseaseGroup = function (maxMonthsAgoForHigherOccurrenceScore, higherOccurrenceScore,
        lowerOccurrenceScore, minValidationWeighting, minOccurrencesForPresence, minOccurrencesForPossiblePresence) {
        return { diseaseExtentParameters: {
            maxMonthsAgoForHigherOccurrenceScore: maxMonthsAgoForHigherOccurrenceScore,
            higherOccurrenceScore: higherOccurrenceScore,
            lowerOccurrenceScore: lowerOccurrenceScore,
            minValidationWeighting: minValidationWeighting,
            minOccurrencesForPresence: minOccurrencesForPresence,
            minOccurrencesForPossiblePresence: minOccurrencesForPossiblePresence
        } };
    };

    var expectParameters = function (vm, parameters) {
        expect(vm.maxMonthsAgoForHigherOccurrenceScore()).toBe(parameters.maxMonthsAgoForHigherOccurrenceScore);
        expect(vm.higherOccurrenceScore()).toBe(parameters.higherOccurrenceScore);
        expect(vm.lowerOccurrenceScore()).toBe(parameters.lowerOccurrenceScore);
        expect(vm.minValidationWeighting()).toBe(parameters.minValidationWeighting);
        expect(vm.minOccurrencesForPresence()).toBe(parameters.minOccurrencesForPresence);
        expect(vm.minOccurrencesForPossiblePresence()).toBe(parameters.minOccurrencesForPossiblePresence);
    };

    var expectRule = function (arg, name, params) {
        expect(arg).toHaveValidationRule({name: name, params: params});
    };

    describe("The 'disease extent parameters' view model", function () {
        var eventName = "disease-group-selected";

        describe("holds the expected parameters for disease extent calculation", function () {
            var vm = new DiseaseExtentParametersViewModel("");
            it("as observables", function () {
                expect(vm.maxMonthsAgoForHigherOccurrenceScore).toBeObservable();
                expect(vm.higherOccurrenceScore).toBeObservable();
                expect(vm.lowerOccurrenceScore).toBeObservable();
                expect(vm.minValidationWeighting).toBeObservable();
                expect(vm.minOccurrencesForPresence).toBeObservable();
                expect(vm.minOccurrencesForPossiblePresence).toBeObservable();
            });

            it("with the appropriate validation rules", function () {
                expectRule(vm.maxMonthsAgoForHigherOccurrenceScore, "digit", true);
                expectRule(vm.maxMonthsAgoForHigherOccurrenceScore, "min", 0);

                expectRule(vm.higherOccurrenceScore, "digit", true);
                expectRule(vm.higherOccurrenceScore, "min", 0);

                expectRule(vm.lowerOccurrenceScore, "digit", true);
                expectRule(vm.lowerOccurrenceScore, "min", 0);
                expectRule(vm.lowerOccurrenceScore, "max", vm.higherOccurrenceScore);

                expectRule(vm.minValidationWeighting, "number", true);
                expectRule(vm.minValidationWeighting, "min", 0);
                expectRule(vm.minValidationWeighting, "max", 1);

                expectRule(vm.minOccurrencesForPresence, "digit", true);
                expectRule(vm.minOccurrencesForPresence, "min", 0);

                expectRule(vm.minOccurrencesForPossiblePresence, "digit", true);
                expectRule(vm.minOccurrencesForPossiblePresence, "min", 0);
                expectRule(vm.minOccurrencesForPossiblePresence, "max", vm.minOccurrencesForPresence);
            });
        });

        describe("when the specified event is fired", function () {
            it("updates the parameter fields", function () {
                // Arrange
                var diseaseGroup = constructDiseaseGroup("60", "24", "4", "2", "0.6", "5", "2");
                var vm = new DiseaseExtentParametersViewModel(eventName);

                // Act
                ko.postbox.publish(eventName, diseaseGroup);

                // Assert
                expectParameters(vm, diseaseGroup.diseaseExtentParameters);
            });

            it("sets the values to empty if the disease group does not have a disease extent", function () {
                // Arrange
                var diseaseGroup = { diseaseExtentParameters: undefined };
                var vm = new DiseaseExtentParametersViewModel(eventName);

                // Act
                ko.postbox.publish(eventName, diseaseGroup);

                // Assert
                expect(vm.maxMonthsAgoForHigherOccurrenceScore()).toBe("");
                expect(vm.higherOccurrenceScore()).toBe("");
                expect(vm.lowerOccurrenceScore()).toBe("");
                expect(vm.minValidationWeighting()).toBe("");
                expect(vm.minOccurrencesForPresence()).toBe("");
                expect(vm.minOccurrencesForPossiblePresence()).toBe("");
            });

            it("overwrites previous values as empty, if only some fields are specified", function () {
                // Arrange
                var vm = new DiseaseExtentParametersViewModel(eventName);

                var diseaseGroup1 = constructDiseaseGroup("60", "24", "4", "2", "0.6", "5", "2");
                var diseaseGroup2 = { diseaseExtentParameters: {
                    maxMonthsAgoForHigherOccurrenceScore: "12",
                    higherOccurrenceScore: "0",
                    lowerOccurrenceScore: null,
                    minValidationWeighting: undefined,
                    minOccurrencesForPresence: ""
                } };

                // Act
                ko.postbox.publish(eventName, diseaseGroup1);
                ko.postbox.publish(eventName, diseaseGroup2);

                // Assert
                var expectedParameters = {
                    maxMonthsAgoForHigherOccurrenceScore: "12",
                    higherOccurrenceScore: "0",
                    lowerOccurrenceScore: "",
                    minValidationWeighting: "",
                    minOccurrencesForPresence: "",
                    minOccurrencesForPossiblePresence: ""
                };
                expectParameters(vm, expectedParameters);
            });
        });
    });
});
