/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    return function (diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel) {
        var getId = function (diseaseGroup) {
            return (diseaseGroup) ? diseaseGroup.id : null;
        };

        var parse = function (arg) {
            var val = parseInt(arg, 10);
            return isNaN(val) ? undefined : val;
        };

        return {
            name: diseaseGroupSettingsViewModel.name(),
            publicName: diseaseGroupSettingsViewModel.publicName(),
            shortName: diseaseGroupSettingsViewModel.shortName(),
            abbreviation: diseaseGroupSettingsViewModel.abbreviation(),
            groupType: diseaseGroupSettingsViewModel.selectedType(),
            isGlobal: diseaseGroupSettingsViewModel.isGlobal(),
            parentDiseaseGroup: {id: getId(diseaseGroupSettingsViewModel.selectedParentDiseaseGroup())},
            validatorDiseaseGroup: {id: getId(diseaseGroupSettingsViewModel.selectedValidatorDiseaseGroup())},
            minNewOccurrences: parse(modelRunParametersViewModel.minNewOccurrences()),
            minDataVolume: parse(modelRunParametersViewModel.minDataVolume()),
            minDistinctCountries: parse(modelRunParametersViewModel.minDistinctCountries()),
            minHighFrequencyCountries: parse(modelRunParametersViewModel.minHighFrequencyCountries()),
            highFrequencyThreshold: parse(modelRunParametersViewModel.highFrequencyThreshold()),
            occursInAfrica: modelRunParametersViewModel.occursInAfrica(),
            diseaseExtentParameters: {
                maxMonthsAgo: parse(diseaseExtentParametersViewModel.maxMonthsAgo()),
                maxMonthsAgoForHigherOccurrenceScore:
                    parse(diseaseExtentParametersViewModel.maxMonthsAgoForHigherOccurrenceScore()),
                lowerOccurrenceScore: parse(diseaseExtentParametersViewModel.lowerOccurrenceScore()),
                higherOccurrenceScore: parse(diseaseExtentParametersViewModel.higherOccurrenceScore()),
                minValidationWeighting: diseaseExtentParametersViewModel.minValidationWeighting(),
                minOccurrencesForPresence: parse(diseaseExtentParametersViewModel.minOccurrencesForPresence()),
                minOccurrencesForPossiblePresence:
                    parse(diseaseExtentParametersViewModel.minOccurrencesForPossiblePresence())
            }
        };
    };
});
