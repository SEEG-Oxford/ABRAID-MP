/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    return function (viewModel) {
        return {
            minNewOccurrences: viewModel.minNewOccurrences(),
            minDataVolume: viewModel.minDataVolume(),
            minDistinctCountries: viewModel.minDistinctCountries(),
            minHighFrequencyCountries: viewModel.minHighFrequencyCountries(),
            highFrequencyThreshold: viewModel.highFrequencyThreshold(),
            occursInAfrica: viewModel.occursInAfrica()
        };
    };
});
