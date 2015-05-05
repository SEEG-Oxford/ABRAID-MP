/* A suite of tests for the DiseaseGroupAdministrationViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupAdministrationViewModel",
    "ko"
], function (DiseaseGroupAdministrationViewModel, ko) {
    "use strict";

    var wrap = function (arg) {
        return function () { return arg; };
    };

    describe("The 'disease group administration' view model", function () {
        var baseUrl = "";
        var selectedEvent = "selectedEvent";
        var savedEvent = "savedEvent";

        it("holds the 3 view models for disease group settings and parameters, which take the expected initial value",
        function () {
            // Arrange
            var diseaseGroupSettingsViewModel = "settings";
            var modelRunParametersViewModel = "model run parameters";
            var diseaseExtentParametersViewModel = "extent parameters";
            // Act
            var vm = new DiseaseGroupAdministrationViewModel(baseUrl, {}, diseaseGroupSettingsViewModel,
                modelRunParametersViewModel, diseaseExtentParametersViewModel, selectedEvent, savedEvent);
            // Assert
            expect(vm.diseaseGroupSettingsViewModel).toBe(diseaseGroupSettingsViewModel);
            expect(vm.modelRunParametersViewModel).toBe(modelRunParametersViewModel);
            expect(vm.diseaseExtentParametersViewModel).toBe(diseaseExtentParametersViewModel);
        });

        describe("holds the submit button which,", function () {
            var vm = {};
            var refreshSpy = jasmine.createSpy();
            var diseaseGroupSettingsViewModel = {
                name: wrap("Name"),
                publicName: wrap("Public name"),
                shortName: wrap("Short name"),
                abbreviation: wrap("ABBREV"),
                selectedType: wrap("MICROCLUSTER"),
                isGlobal: wrap(true),
                selectedParentDiseaseGroup: wrap({ id: 2 }),
                selectedValidatorDiseaseGroup: wrap({ id: 3 })
            };
            var modelRunParametersViewModel = {
                minNewLocations: wrap(1),
                maxEnvironmentalSuitabilityForTriggering: wrap(0.2),
                minDistanceFromDiseaseExtentForTriggering: wrap(-300),
                minDataVolume: wrap(2),
                minDistinctCountries: wrap(3),
                minHighFrequencyCountries: wrap(4),
                highFrequencyThreshold: wrap(5),
                occursInAfrica: wrap(true),
                useMachineLearning: wrap(true),
                maxEnvironmentalSuitabilityWithoutML: wrap(0.8)
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(24),
                higherOccurrenceScore: wrap(2),
                lowerOccurrenceScore: wrap(1),
                minValidationWeighting: wrap(0.6),
                minOccurrencesForPossiblePresence: wrap(2),
                minOccurrencesForPresence: wrap(5)
            };
            var expectedParams =
            "{" +
                "\"name\":\"Name\"," +
                "\"publicName\":\"Public name\"," +
                "\"shortName\":\"Short name\"," +
                "\"abbreviation\":\"ABBREV\"," +
                "\"groupType\":\"MICROCLUSTER\"," +
                "\"isGlobal\":true," +
                "\"parentDiseaseGroup\":{\"id\":2}," +
                "\"validatorDiseaseGroup\":{\"id\":3}," +
                "\"minNewLocations\":1," +
                "\"maxEnvironmentalSuitabilityForTriggering\":0.2," +
                "\"minDistanceFromDiseaseExtentForTriggering\":-300," +
                "\"minDataVolume\":2," +
                "\"minDistinctCountries\":3," +
                "\"minHighFrequencyCountries\":4," +
                "\"highFrequencyThreshold\":5," +
                "\"occursInAfrica\":true," +
                "\"useMachineLearning\":true," +
                "\"maxEnvironmentalSuitabilityWithoutML\":0.8," +
                "\"diseaseExtentParameters\":{" +
                    "\"maxMonthsAgoForHigherOccurrenceScore\":24," +
                    "\"lowerOccurrenceScore\":1," +
                    "\"higherOccurrenceScore\":2," +
                    "\"minValidationWeighting\":0.6," +
                    "\"minOccurrencesForPresence\":5," +
                    "\"minOccurrencesForPossiblePresence\":2" +
                "}" +
            "}";

            beforeEach(function () {
                vm = new DiseaseGroupAdministrationViewModel(baseUrl, refreshSpy, diseaseGroupSettingsViewModel,
                    modelRunParametersViewModel, diseaseExtentParametersViewModel, selectedEvent, savedEvent);
            });

            describe("when an existing disease group is selected,", function () {
                describe("posts to the expected URL (by reacting to the event),", function () {
                    it("with the expected payload", function () {
                        // Arrange
                        var id = 1;
                        var diseaseGroup = { id: id };
                        var expectedUrl = baseUrl + "admin/diseases/" + id + "/save";

                        // Act
                        ko.postbox.publish(selectedEvent, diseaseGroup);
                        vm.submit();

                        // Arrange
                        expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                        expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                        expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                    });

                    it("when unsuccessful, updates the 'notices' with an error", function () {
                        // Arrange
                        var expectedNotice = { message: "Server error. Please refresh the page and try again.",
                                               priority: "warning" };
                        // Act
                        vm.submit();
                        jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                        // Assert
                        expect(vm.notices()).toContain(expectedNotice);
                    });

                    describe("when successful,", function () {
                        it("updates the 'notice' with a success alert", function () {
                            // Arrange
                            ko.postbox.publish(selectedEvent, { id: 1 });
                            var expectedNotice = { message: "Form saved successfully.", priority: "success" };
                            // Act
                            vm.submit();
                            jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                            // Assert
                            expect(vm.notices()).toContain(expectedNotice);
                        });

                        it("fires the 'disease group saved' event with the expected id", function () {
                            // Arrange
                            var diseaseGroupId = 1;
                            ko.postbox.publish(selectedEvent, { id: diseaseGroupId });

                            var subscription = ko.postbox.subscribe(savedEvent, function (value) {
                                // Assert
                                expect(value).toBe(diseaseGroupId);
                            });

                            // Act
                            vm.submit();
                            jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                            subscription.dispose();
                        });
                    });
                });
            });

            describe("when a new empty disease group is added,", function () {
                it("posts to the expected URL with the expected payload", function () {
                    // Arrange
                    var diseaseGroup = { };
                    var expectedUrl = baseUrl + "admin/diseases/add";

                    // Act
                    ko.postbox.publish(selectedEvent, diseaseGroup);
                    vm.submit();

                    // Arrange
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                });

                it("when unsuccessful, updates the 'notice' with an error", function () {
                    // Arrange
                    var expectedNotice = { message: "Server error. Please refresh the page and try again.",
                                           priority: "warning" };
                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                    // Assert
                    expect(vm.notices()).toContain(expectedNotice);
                });

                it("when successful, refreshes the page", function () {
                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    // Assert
                    expect(refreshSpy).toHaveBeenCalled();
                });
            });
        });

        it("POSTs the expected content when not all parameters are defined", function () {
            // Arrange
            var diseaseGroupSettingsViewModel = {
                name: wrap("Name"),
                publicName: wrap(undefined),
                shortName: wrap(undefined),
                abbreviation: wrap(undefined),
                selectedType: wrap("MICROCLUSTER"),
                isGlobal: wrap(undefined),
                selectedParentDiseaseGroup: wrap(undefined),
                selectedValidatorDiseaseGroup: wrap(undefined)
            };
            var modelRunParametersViewModel = {
                minNewLocations: wrap(""),
                maxEnvironmentalSuitabilityForTriggering: wrap(""),
                minDistanceFromDiseaseExtentForTriggering: wrap(""),
                minDataVolume: wrap(""),
                minDistinctCountries: wrap(""),
                minHighFrequencyCountries: wrap(""),
                highFrequencyThreshold: wrap(""),
                occursInAfrica: wrap(undefined),
                useMachineLearning: wrap(undefined),
                maxEnvironmentalSuitabilityWithoutML: wrap(undefined)
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(""),
                higherOccurrenceScore: wrap(""),
                lowerOccurrenceScore: wrap(""),
                minValidationWeighting: wrap(""),
                minOccurrencesForPossiblePresence: wrap(""),
                minOccurrencesForPresence: wrap("")
            };
            var expectedParams = "{\"name\":\"Name\"," +
                "\"groupType\":\"MICROCLUSTER\"," +
                "\"parentDiseaseGroup\":{\"id\":null}," +
                "\"validatorDiseaseGroup\":{\"id\":null}," +
                "\"diseaseExtentParameters\":{}}";
            var diseaseGroup = { id: 1 };
            var vm = new DiseaseGroupAdministrationViewModel(baseUrl, {}, diseaseGroupSettingsViewModel,
                modelRunParametersViewModel, diseaseExtentParametersViewModel, selectedEvent, savedEvent);
            // Act
            ko.postbox.publish(selectedEvent, diseaseGroup);
            vm.submit();
            // Arrange
            expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
        });
    });
});
