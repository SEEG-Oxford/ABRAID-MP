/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    return function (diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel) {
        var getId = function (diseaseGroup) {
            return (diseaseGroup) ? diseaseGroup.id : null;
        };

        var parseInteger = function (arg) {
            var val = parseInt(arg, 10);
            return isNaN(val) ? undefined : val;
        };

        var parseNumber = function (arg) {
            var val = parseFloat(arg);
            return isNaN(val) ? undefined : val;
        };

        return {
            name: diseaseGroupSettingsViewModel.name(),
            publicName: diseaseGroupSettingsViewModel.publicName(),
            shortName: diseaseGroupSettingsViewModel.shortName(),
            abbreviation: diseaseGroupSettingsViewModel.abbreviation(),
            groupType: diseaseGroupSettingsViewModel.selectedType(),
            isGlobal: diseaseGroupSettingsViewModel.isGlobal(),
            modelMode: modelRunParametersViewModel.modelMode(),
            parentDiseaseGroup: { id: getId(diseaseGroupSettingsViewModel.selectedParentDiseaseGroup()) },
            validatorDiseaseGroup: { id: getId(diseaseGroupSettingsViewModel.selectedValidatorDiseaseGroup()) },
            maxDaysBetweenModelRuns:  parseInteger(modelRunParametersViewModel.maxDaysBetweenModelRuns()),
            minNewLocations: parseInteger(modelRunParametersViewModel.minNewLocations()),
            maxEnvironmentalSuitabilityForTriggering:
                parseNumber(modelRunParametersViewModel.maxEnvironmentalSuitabilityForTriggering()),
            minDistanceFromDiseaseExtentForTriggering:
                parseNumber(modelRunParametersViewModel.minDistanceFromDiseaseExtentForTriggering()),
            minDataVolume: parseInteger(modelRunParametersViewModel.minDataVolume()),
            minDistinctCountries: parseInteger(modelRunParametersViewModel.minDistinctCountries()),
            minHighFrequencyCountries: parseInteger(modelRunParametersViewModel.minHighFrequencyCountries()),
            highFrequencyThreshold: parseInteger(modelRunParametersViewModel.highFrequencyThreshold()),
            occursInAfrica: modelRunParametersViewModel.occursInAfrica(),
            useMachineLearning: modelRunParametersViewModel.useMachineLearning(),
            maxEnvironmentalSuitabilityWithoutML:
                parseNumber(modelRunParametersViewModel.maxEnvironmentalSuitabilityWithoutML()),
            diseaseExtentParameters: {
                maxMonthsAgoForHigherOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.maxMonthsAgoForHigherOccurrenceScore()),
                lowerOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.lowerOccurrenceScore()),
                higherOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.higherOccurrenceScore()),
                minValidationWeighting:
                    parseNumber(diseaseExtentParametersViewModel.minValidationWeighting()),
            }
        };
    };
});
