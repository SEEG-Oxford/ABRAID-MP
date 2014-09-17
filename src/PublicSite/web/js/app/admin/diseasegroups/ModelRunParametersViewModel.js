/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko"
], function (ko) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        self.minNewLocations = ko.observable().extend({ digit: true, min: 0 });
        self.minDataVolume = ko.observable().extend({ required: true, digit: true, min: 0 });
        self.minDistinctCountries = ko.observable().extend({ digit: true, min: 0 });
        self.occursInAfrica = ko.observable();

        self.minHighFrequencyCountriesValue = ko.observable();
        self.minHighFrequencyCountries = ko.computed({
            read:  function () { return self.occursInAfrica() ? self.minHighFrequencyCountriesValue() : null; },
            write: function (value) { self.minHighFrequencyCountriesValue(value); },
            owner: self
        }).extend({ digit: true, min: 0 });

        self.highFrequencyThresholdValue = ko.observable();
        self.highFrequencyThreshold = ko.computed({
            read:  function () { return self.occursInAfrica() ? self.highFrequencyThresholdValue() : null; },
            write: function (value) { self.highFrequencyThresholdValue(value); },
            owner: self
        }).extend({ digit: true, min: 0 });

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.minNewLocations((diseaseGroup.minNewLocations || "").toString());
            self.minDataVolume((diseaseGroup.minDataVolume || "").toString());
            self.minDistinctCountries((diseaseGroup.minDistinctCountries || "").toString());
            self.minHighFrequencyCountries((diseaseGroup.minHighFrequencyCountries || "").toString());
            self.highFrequencyThreshold((diseaseGroup.highFrequencyThreshold || "").toString());
            self.occursInAfrica(diseaseGroup.occursInAfrica);
        });
    };
});