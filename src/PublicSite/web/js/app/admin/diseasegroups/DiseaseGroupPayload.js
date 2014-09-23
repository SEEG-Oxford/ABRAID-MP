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
            parentDiseaseGroup: { id: getId(diseaseGroupSettingsViewModel.selectedParentDiseaseGroup()) },
            validatorDiseaseGroup: { id: getId(diseaseGroupSettingsViewModel.selectedValidatorDiseaseGroup()) },
            minNewLocations: parseInteger(modelRunParametersViewModel.minNewLocations()),
            minEnvironmentalSuitability: parseNumber(modelRunParametersViewModel.minEnvironmentalSuitability()),
            minDistanceFromDiseaseExtent: parseNumber(modelRunParametersViewModel.minDistanceFromDiseaseExtent()),
            minDataVolume: parseInteger(modelRunParametersViewModel.minDataVolume()),
            minDistinctCountries: parseInteger(modelRunParametersViewModel.minDistinctCountries()),
            minHighFrequencyCountries: parseInteger(modelRunParametersViewModel.minHighFrequencyCountries()),
            highFrequencyThreshold: parseInteger(modelRunParametersViewModel.highFrequencyThreshold()),
            occursInAfrica: modelRunParametersViewModel.occursInAfrica(),
            diseaseExtentParameters: {
                maxMonthsAgoForHigherOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.maxMonthsAgoForHigherOccurrenceScore()),
                lowerOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.lowerOccurrenceScore()),
                higherOccurrenceScore:
                    parseInteger(diseaseExtentParametersViewModel.higherOccurrenceScore()),
                minValidationWeighting:
                    parse(diseaseExtentParametersViewModel.minValidationWeighting()),
                minOccurrencesForPresence:
                    parseInteger(diseaseExtentParametersViewModel.minOccurrencesForPresence()),
                minOccurrencesForPossiblePresence:
                    parseInteger(diseaseExtentParametersViewModel.minOccurrencesForPossiblePresence())
            }
        };
    };
});
