/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/ModelRunParametersPayload"
], function (ko, ModelRunParametersPayload) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        self.minNewOccurrences = ko.observable().extend({ digit: true, min: 0 });
        self.minDataVolume = ko.observable().extend({ digit: true, min: 0 });
        self.minDistinctCountries = ko.observable().extend({ digit: true, min: 0 });
        self.minHighFrequencyCountries = ko.observable().extend({ digit: true, min: 0 });
        self.highFrequencyThreshold = ko.observable().extend({ digit: true, min: 0 });
        self.occursInAfrica = ko.observable();

        self.data = function () {
            return new ModelRunParametersPayload(self);
        };

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.minNewOccurrences(diseaseGroup.minNewOccurrences);
            self.minDataVolume(diseaseGroup.minDataVolume);
            self.minDistinctCountries(diseaseGroup.minDistinctCountries);
            self.minHighFrequencyCountries(diseaseGroup.minHighFrequencyCountries);
            self.highFrequencyThreshold(diseaseGroup.highFrequencyThreshold);
            self.occursInAfrica(diseaseGroup.occursInAfrica);
        });
    };
});