/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko"
], function (ko) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        self.minNewOccurrences = ko.observable().extend({ digit: true, min: 0 });
        self.minDataVolume = ko.observable().extend({ digit: true, min: 0 });
        self.minDistinctCountries = ko.observable().extend({ digit: true, min: 0 });
        self.minHighFrequencyCountries = ko.observable().extend({ digit: true, min: 0 });
        self.highFrequencyThreshold = ko.observable().extend({ digit: true, min: 0 });
        self.occursInAfrica = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.minNewOccurrences((diseaseGroup.minNewOccurrences || "").toString());
            self.minDataVolume((diseaseGroup.minDataVolume || "").toString());
            self.minDistinctCountries((diseaseGroup.minDistinctCountries || "").toString());
            self.minHighFrequencyCountries((diseaseGroup.minHighFrequencyCountries || "").toString());
            self.highFrequencyThreshold((diseaseGroup.highFrequencyThreshold || "").toString());
            self.occursInAfrica(diseaseGroup.occursInAfrica);
        });
    };
});