/* A suite of tests for the DiseaseGroupPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/admin/diseasegroups/DiseaseGroupPayload"], function (DiseaseGroupPayload) {
    "use strict";

    var wrap = function (arg) {
        return function () { return arg; };
    };

    describe("The payload returns the expected structure", function () {
        it("when given three view models", function () {
            // Arrange
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
                minDataVolume: wrap(2),
                minDistinctCountries: wrap(3),
                minHighFrequencyCountries: wrap(4),
                highFrequencyThreshold: wrap(5),
                occursInAfrica: wrap(true)
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(24),
                higherOccurrenceScore: wrap(2),
                lowerOccurrenceScore: wrap(1),
                minValidationWeighting: wrap(0.6),
                minOccurrencesForPossiblePresence: wrap(2),
                minOccurrencesForPresence: wrap(5)
            };
            var expectedPayload = {
                name : "Name",
                publicName: "Public name",
                shortName: "Short name",
                abbreviation: "ABBREV",
                groupType: "MICROCLUSTER",
                isGlobal: true,
                parentDiseaseGroup: { id: 2 },
                validatorDiseaseGroup: { id: 3 },
                minNewLocations: 1,
                minDataVolume: 2,
                minDistinctCountries: 3,
                minHighFrequencyCountries:  4,
                highFrequencyThreshold: 5,
                occursInAfrica: true,
                diseaseExtentParameters: {
                    maxMonthsAgoForHigherOccurrenceScore: 24,
                    higherOccurrenceScore: 2,
                    lowerOccurrenceScore: 1,
                    minValidationWeighting: 0.6,
                    minOccurrencesForPossiblePresence: 2,
                    minOccurrencesForPresence: 5
                }
            };
            // Act
            var payload = new DiseaseGroupPayload(
                diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel);
            // Assert
            expect(payload).toEqual(expectedPayload);
        });

        it("when not all parameters are defined", function () {
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
                minDataVolume: wrap(""),
                minDistinctCountries: wrap(""),
                minHighFrequencyCountries: wrap(""),
                highFrequencyThreshold: wrap(""),
                occursInAfrica: wrap(undefined)
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(""),
                higherOccurrenceScore: wrap(""),
                lowerOccurrenceScore: wrap(""),
                minValidationWeighting: wrap(""),
                minOccurrencesForPossiblePresence: wrap(""),
                minOccurrencesForPresence: wrap("")
            };
            var expectedPayload = {
                name : "Name",
                publicName: undefined,
                shortName: undefined,
                abbreviation: undefined,
                groupType: "MICROCLUSTER",
                isGlobal: undefined,
                parentDiseaseGroup: { id: null },
                validatorDiseaseGroup: { id: null },
                minNewLocations: undefined,
                minDataVolume: undefined,
                minDistinctCountries: undefined,
                minHighFrequencyCountries:  undefined,
                highFrequencyThreshold: undefined,
                occursInAfrica: undefined,
                diseaseExtentParameters: {
                    maxMonthsAgoForHigherOccurrenceScore: undefined,
                    higherOccurrenceScore: undefined,
                    lowerOccurrenceScore: undefined,
                    minValidationWeighting: undefined,
                    minOccurrencesForPossiblePresence: undefined,
                    minOccurrencesForPresence: undefined
                }
            };
            // Act
            var payload = new DiseaseGroupPayload(
                diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel);
            // Assert
            expect(payload).toEqual(expectedPayload);
        });
    });
});
