/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    var ModelRunParametersPayload = function (minNewOccurrences, minDataVolume, minDistinctCountries,
                                              minHighFrequencyCountries, highFrequencyThreshold, occursInAfrica) {
        return {
            minNewOccurrences: minNewOccurrences,
            minDataVolume: minDataVolume,
            minDistinctCountries: minDistinctCountries,
            minHighFrequencyCountries: minHighFrequencyCountries,
            highFrequencyThreshold: highFrequencyThreshold,
            occursInAfrica: occursInAfrica
        };
    };

    return {
        fromViewModel: function (disease) {
            return new ModelRunParametersPayload(
                disease.minNewOccurrences(),
                disease.minDataVolume(),
                disease.minDistinctCountries(),
                disease.minHighFrequencyCountries(),
                disease.highFrequencyThreshold(),
                disease.occursInAfrica()
            );
        },
        fromJson: function (disease) {
            return new ModelRunParametersPayload(
                disease.minNewOccurrences,
                disease.minDataVolume,
                disease.minDistinctCountries,
                disease.minHighFrequencyCountries,
                disease.highFrequencyThreshold,
                disease.occursInAfrica
            );
        }
    };
});
