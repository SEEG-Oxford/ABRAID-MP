/* A suite of tests for the DiseaseGroupPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroup/DiseaseGroupPayload",
    "underscore"
], function (DiseaseGroupPayload, _) {
    "use strict";

    var wrap = function (arg) {
        return function () { return arg; };
    };

    describe("The payload returns the expected structure", function () {
        it("when given two view models", function () {
            // Arrange
            var diseaseGroupSettingsViewModel = { name: wrap("Name"),
                publicName: wrap("Public name"),
                shortName: wrap("Short name"),
                abbreviation: wrap("ABBREV"),
                selectedType: wrap("MICROCLUSTER"),
                isGlobal: wrap(true),
                selectedParentDiseaseGroup: wrap({ id: 2 }),
                selectedValidatorDiseaseGroup: wrap({ id: 3 }) };
            var modelRunParametersViewModel = { minNewOccurrences: wrap(1),
                minDataVolume: wrap(2),
                minDistinctCountries: wrap(3),
                minHighFrequencyCountries: wrap(4),
                highFrequencyThreshold: wrap(5),
                occursInAfrica: wrap(true) };
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
            var diseaseGroupSettingsViewModel = { name: wrap("Name"),
                publicName: wrap(undefined),
                shortName: wrap(undefined),
                abbreviation: wrap(undefined),
                selectedType: wrap("MICROCLUSTER"),
                isGlobal: wrap(undefined),
                selectedParentDiseaseGroup: wrap(undefined),
                selectedValidatorDiseaseGroup: wrap(undefined) };
            var modelRunParametersViewModel = { minNewOccurrences: wrap(""),
                minDataVolume: wrap(""),
                minDistinctCountries: wrap(""),
                minHighFrequencyCountries: wrap(""),
                highFrequencyThreshold: wrap(""),
                occursInAfrica: wrap(undefined) };
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
            expect(payload).toEqual(expectedPayload);
        });
    });
});
