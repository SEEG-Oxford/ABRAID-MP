/* A suite of tests for the DiseaseGroupPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroup/DiseaseGroupPayload",
    "underscore"
], function (DiseaseGroupPayload, _) {
    "use strict";

    describe("The payload returns the expected structure", function () {
        it("when given two view models", function () {
            // Arrange
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
            var expectedPayload = {
                name : "Name",
                publicName: "Public name",
                shortName: "Short name",
                abbreviation: "ABBREV",
                groupType: "MICROCLUSTER",
                isGlobal: true,
                parentDiseaseGroup: { id: 2 },
                validatorDiseaseGroup: { id: 3 },
                minNewOccurrences: 1,
                minDataVolume: 2,
                minDistinctCountries: 3,
                minHighFrequencyCountries:  4,
                highFrequencyThreshold: 5,
                occursInAfrica: true
            };
            // Act
            var payload = new DiseaseGroupPayload(diseaseGroupSettingsViewModel, modelRunParametersViewModel);
            // Assert
            expect(_.isEqual(payload, expectedPayload)).toBe(true);
        });

        it("when not all parameters are defined", function () {
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
            var expectedPayload = {
                name : "Name",
                publicName: undefined,
                shortName: undefined,
                abbreviation: undefined,
                groupType: "MICROCLUSTER",
                isGlobal: undefined,
                parentDiseaseGroup: { id: null },
                validatorDiseaseGroup: { id: null },
                minNewOccurrences: undefined,
                minDataVolume: undefined,
                minDistinctCountries: undefined,
                minHighFrequencyCountries:  undefined,
                highFrequencyThreshold: undefined,
                occursInAfrica: undefined
            };
            // Act
            var payload = new DiseaseGroupPayload(diseaseGroupSettingsViewModel, modelRunParametersViewModel);
            // Assert
            expect(_.isEqual(payload, expectedPayload)).toBe(true);
        });
    });
});
