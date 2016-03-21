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
                maxDaysBetweenModelRuns: wrap(7),
                minNewLocations: wrap(1),
                maxEnvironmentalSuitabilityForTriggering: wrap(0.2),
                minDistanceFromDiseaseExtentForTriggering: wrap(-300),
                modelMode: wrap("Bhatt2013"),
                agentType: wrap("VIRUS"),
                filterBiasDataByAgentType: wrap(true),
                minDataVolume: wrap(2),
                minDistinctCountries: wrap(3),
                minHighFrequencyCountries: wrap(4),
                highFrequencyThreshold: wrap(5),
                occursInAfrica: wrap(true),
                useMachineLearning: wrap(true),
                maxEnvironmentalSuitabilityWithoutML: wrap(0.5)
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(24),
                higherOccurrenceScore: wrap(2),
                lowerOccurrenceScore: wrap(1),
                minValidationWeighting: wrap(0.6)
            };
            var expectedPayload = {
                name : "Name",
                publicName: "Public name",
                shortName: "Short name",
                abbreviation: "ABBREV",
                groupType: "MICROCLUSTER",
                isGlobal: true,
                modelMode: "Bhatt2013",
                agentType: "VIRUS",
                filterBiasDataByAgentType: true,
                parentDiseaseGroup: { id: 2 },
                validatorDiseaseGroup: { id: 3 },
                maxDaysBetweenModelRuns: 7,
                minNewLocations: 1,
                maxEnvironmentalSuitabilityForTriggering: 0.2,
                minDistanceFromDiseaseExtentForTriggering: -300,
                minDataVolume: 2,
                minDistinctCountries: 3,
                minHighFrequencyCountries:  4,
                highFrequencyThreshold: 5,
                occursInAfrica: true,
                useMachineLearning: true,
                maxEnvironmentalSuitabilityWithoutML: 0.5,
                diseaseExtentParameters: {
                    maxMonthsAgoForHigherOccurrenceScore: 24,
                    higherOccurrenceScore: 2,
                    lowerOccurrenceScore: 1,
                    minValidationWeighting: 0.6
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
                maxDaysBetweenModelRuns: wrap(""),
                minNewLocations: wrap(""),
                maxEnvironmentalSuitabilityForTriggering: wrap(""),
                minDistanceFromDiseaseExtentForTriggering: wrap(""),
                modelMode: wrap("Bhatt2013"),
                agentType: wrap(""),
                filterBiasDataByAgentType: wrap(false),
                minDataVolume: wrap(""),
                minDistinctCountries: wrap(""),
                minHighFrequencyCountries: wrap(""),
                highFrequencyThreshold: wrap(""),
                occursInAfrica: wrap(undefined),
                useMachineLearning: wrap(undefined),
                maxEnvironmentalSuitabilityWithoutML: wrap("")
            };
            var diseaseExtentParametersViewModel = {
                maxMonthsAgoForHigherOccurrenceScore: wrap(""),
                higherOccurrenceScore: wrap(""),
                lowerOccurrenceScore: wrap(""),
                minValidationWeighting: wrap("")
            };
            var expectedPayload = {
                name : "Name",
                publicName: undefined,
                shortName: undefined,
                abbreviation: undefined,
                groupType: "MICROCLUSTER",
                isGlobal: undefined,
                modelMode: "Bhatt2013",
                agentType: "",
                filterBiasDataByAgentType: false,
                parentDiseaseGroup: { id: null },
                validatorDiseaseGroup: { id: null },
                maxDaysBetweenModelRuns: undefined,
                minNewLocations: undefined,
                maxEnvironmentalSuitabilityForTriggering: undefined,
                minDistanceFromDiseaseExtentForTriggering: undefined,
                minDataVolume: undefined,
                minDistinctCountries: undefined,
                minHighFrequencyCountries:  undefined,
                highFrequencyThreshold: undefined,
                occursInAfrica: undefined,
                useMachineLearning: undefined,
                maxEnvironmentalSuitabilityWithoutML: undefined,
                diseaseExtentParameters: {
                    maxMonthsAgoForHigherOccurrenceScore: undefined,
                    higherOccurrenceScore: undefined,
                    lowerOccurrenceScore: undefined,
                    minValidationWeighting: undefined
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
