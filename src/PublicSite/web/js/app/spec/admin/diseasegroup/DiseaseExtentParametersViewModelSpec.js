/* A suite of tests for the DiseaseExtentParametersViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/admin/diseasegroup/DiseaseExtentParametersViewModel"
], function (ko, DiseaseExtentParametersViewModel) {
    "use strict";

    var constructDiseaseGroup = function (maxMonthsAgo, maxMonthsAgoForHigherOccurrenceScore, higherOccurrenceScore,
        lowerOccurrenceScore, minValidationWeighting, minOccurrencesForPresence, minOccurrencesForPossiblePresence) {
        return { diseaseExtentParameters: {
            maxMonthsAgo: maxMonthsAgo,
            maxMonthsAgoForHigherOccurrenceScore: maxMonthsAgoForHigherOccurrenceScore,
            higherOccurrenceScore: higherOccurrenceScore,
            lowerOccurrenceScore: lowerOccurrenceScore,
            minValidationWeighting: minValidationWeighting,
            minOccurrencesForPresence: minOccurrencesForPresence,
            minOccurrencesForPossiblePresence: minOccurrencesForPossiblePresence
        } };
    };

    var expectParameters = function (vm, parameters) {
        expect(vm.maxMonthsAgo()).toBe(parameters.maxMonthsAgo);
        expect(vm.maxMonthsAgoForHigherOccurrenceScore()).toBe(parameters.maxMonthsAgoForHigherOccurrenceScore);
        expect(vm.higherOccurrenceScore()).toBe(parameters.higherOccurrenceScore);
        expect(vm.lowerOccurrenceScore()).toBe(parameters.lowerOccurrenceScore);
        expect(vm.minValidationWeighting()).toBe(parameters.minValidationWeighting);
        expect(vm.minOccurrencesForPresence()).toBe(parameters.minOccurrencesForPresence);
        expect(vm.minOccurrencesForPossiblePresence()).toBe(parameters.minOccurrencesForPossiblePresence);
    };

    describe("The 'disease extent parameters' view model", function () {
        var eventName = "disease-group-selected";

        it("holds the expected parameters for disease extent calculation as observables", function () {
            var vm = new DiseaseExtentParametersViewModel("");
            expect(vm.maxMonthsAgo).toBeObservable();
            expect(vm.maxMonthsAgoForHigherOccurrenceScore).toBeObservable();
            expect(vm.higherOccurrenceScore).toBeObservable();
            expect(vm.lowerOccurrenceScore).toBeObservable();
            expect(vm.minValidationWeighting).toBeObservable();
            expect(vm.minOccurrencesForPresence).toBeObservable();
            expect(vm.minOccurrencesForPossiblePresence).toBeObservable();
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
                expect(vm.maxMonthsAgo()).toBe("");
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
                    maxMonthsAgo: "30",
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
                    maxMonthsAgo: "30",
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
