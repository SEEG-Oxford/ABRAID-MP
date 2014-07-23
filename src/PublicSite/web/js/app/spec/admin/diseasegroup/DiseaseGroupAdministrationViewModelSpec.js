/* A suite of tests for the DiseaseGroupAdministrationViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/admin/diseasegroup/DiseaseGroupAdministrationViewModel"], function (DiseaseGroupAdministrationViewModel) {
    "use strict";

    describe("The 'administration' view model", function () {
        describe("holds the two view models for disease group settings and parameters which", function () {
            it("take the expected initial value", function () {
                // Arrange
                var baseUrl = "";
                var diseaseGroupSettingsViewModel = "settings";
                var modelRunParametersViewModel = "parameters";
                var eventName = "eventName";
                // Act
                var vm = new DiseaseGroupAdministrationViewModel(
                    baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, eventName);
                // Assert
                expect(vm.diseaseGroupSettingsViewModel).toBe(diseaseGroupSettingsViewModel);
                expect(vm.modelRunParametersViewModel).toBe(modelRunParametersViewModel);
            });
        });
    });
});
