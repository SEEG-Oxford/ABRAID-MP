/* A suite of tests for the DiseaseGroupAdministrationViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupAdministrationViewModel",
    "ko",
    "underscore"
], function (DiseaseGroupAdministrationViewModel, ko, _) {
    "use strict";

    describe("The 'administration' view model", function () {
        var baseUrl = "";
        var eventName = "eventName";

        describe("holds the two view models for disease group settings and parameters which", function () {
            it("take the expected initial value", function () {
                // Arrange
                var diseaseGroupSettingsViewModel = "settings";
                var modelRunParametersViewModel = "parameters";
                // Act
                var vm = new DiseaseGroupAdministrationViewModel(
                    baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
                // Assert
                expect(vm.diseaseGroupSettingsViewModel).toBe(diseaseGroupSettingsViewModel);
                expect(vm.modelRunParametersViewModel).toBe(modelRunParametersViewModel);
            });
        });

        describe("holds the submit button, which", function () {
            var diseaseGroupSettingsViewModel = { name: function () { return "Name"; },
                publicName: function () { return "Public name"; },
                shortName: function () { return "Short name"; },
                abbreviation: function () { return "ABBREV"; },
                selectedType: function () { return "MICROCLUSTER"; },
                isGlobal: function () { return true; },
                selectedParentDiseaseGroup: function () { return { id: 2 }; },
                selectedValidatorDiseaseGroup: function () { return { id: 3 }; }};
            var modelRunParametersViewModel = { minNewOccurrences: function () { return 1; },
                minDataVolume: function () { return 2; },
                minDistinctCountries: function () { return 3; },
                minHighFrequencyCountries: function () { return 4; },
                highFrequencyThreshold: function () { return 5; },
                occursInAfrica: function () { return true; } };
            var expectedParams = "{\"name\":\"Name\"," +
                "\"publicName\":\"Public name\"," +
                "\"shortName\":\"Short name\"," +
                "\"abbreviation\":\"ABBREV\"," +
                "\"groupType\":\"MICROCLUSTER\"," +
                "\"isGlobal\":true," +
                "\"parentDiseaseGroup\":{\"id\":2}," +
                "\"validatorDiseaseGroup\":{\"id\":3}," +
                "\"minNewOccurrences\":1," +
                "\"minDataVolume\":2," +
                "\"minDistinctCountries\":3," +
                "\"minHighFrequencyCountries\":4," +
                "\"highFrequencyThreshold\":5," +
                "\"occursInAfrica\":true}";

            it("posts to the expected URL (by reacting to the published event) with the expected payload", function () {
                // Arrange
                var id = 1;
                var diseaseGroup = { id: id };
                var vm = new DiseaseGroupAdministrationViewModel(
                    baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
                var expectedUrl = baseUrl + "admin/diseasegroups/" + id + "/save";

                // Act
                ko.postbox.publish(eventName, diseaseGroup);
                vm.submit();

                // Arrange
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });

            it("when unsuccessful, updates the 'notice' with an error", function () {
                // Arrange
                var vm = new DiseaseGroupAdministrationViewModel(
                    baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
                var expectedNotice = { message: "Error saving disease group", priority: "warning" };
                // Act
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBe(true);
            });

            it("when successful, updates the 'notice' with a success alert", function () {
                // Arrange
                var vm = new DiseaseGroupAdministrationViewModel(
                    baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
                var expectedNotice = { message: "Saved successfully", priority: "success" };
                // Act
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBe(true);
            });
        });

        it("POSTs the expected content when not all parameters are defined", function () {
            // Arrange
            var diseaseGroupSettingsViewModel = { name: function () { return "Name"; },
                publicName: function () { return undefined; },
                shortName: function () { return undefined; },
                abbreviation: function () { return undefined; },
                selectedType: function () { return "MICROCLUSTER"; },
                isGlobal: function () { return undefined; },
                selectedParentDiseaseGroup: function () { return undefined; },
                selectedValidatorDiseaseGroup: function () { return undefined; }};
            var modelRunParametersViewModel = { minNewOccurrences: function () { return ""; },
                minDataVolume: function () { return ""; },
                minDistinctCountries: function () { return ""; },
                minHighFrequencyCountries: function () { return ""; },
                highFrequencyThreshold: function () { return ""; },
                occursInAfrica: function () { return undefined; } };
            var expectedParams = "{\"name\":\"Name\"," +
                "\"groupType\":\"MICROCLUSTER\"," +
                "\"parentDiseaseGroup\":{\"id\":null}," +
                "\"validatorDiseaseGroup\":{\"id\":null}}";
            var diseaseGroup = { id: 1 };
            var vm = new DiseaseGroupAdministrationViewModel(
                baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
            // Act
            ko.postbox.publish(eventName, diseaseGroup);
            vm.submit();

            // Arrange
            expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
        });
    });
});
