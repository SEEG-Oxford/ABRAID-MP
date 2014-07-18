/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    var getId = function (diseaseGroup) {
        return (diseaseGroup) ? diseaseGroup.id.toString() : null;
    };

    return function (diseaseGroupSettings, modelRunParameters) {
        return {
            name: diseaseGroupSettings.name(),
            publicName: diseaseGroupSettings.publicName(),
            shortName: diseaseGroupSettings.shortName(),
            abbreviation: diseaseGroupSettings.abbreviation(),
            groupType: diseaseGroupSettings.selectedType(),
            isGlobal: diseaseGroupSettings.isGlobal(),
            parentDiseaseGroup: {id: getId(diseaseGroupSettings.selectedParentDiseaseGroup())},
            validatorDiseaseGroup: {id: getId(diseaseGroupSettings.selectedValidatorDiseaseGroup())},
            minNewOccurrences: modelRunParameters.minNewOccurrences(),
            minDataVolume: modelRunParameters.minDataVolume(),
            minDistinctCountries: modelRunParameters.minDistinctCountries(),
            minHighFrequencyCountries: modelRunParameters.minHighFrequencyCountries(),
            highFrequencyThreshold: modelRunParameters.highFrequencyThreshold(),
            occursInAfrica: modelRunParameters.occursInAfrica()
        };
    };
});
