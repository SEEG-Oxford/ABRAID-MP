/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (coffee) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, diseaseGroupSelectedEventName) {
        var self = this;

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
            self.minNewOccurrences(diseaseGroup.minNewOccurrences);
            self.minDataVolume(diseaseGroup.minDataVolume);
            self.minDistinctCountries(diseaseGroup.minDistinctCountries);
            self.minHighFrequencyCountries(diseaseGroup.minHighFrequencyCountries);
            self.highFrequencyThreshold(diseaseGroup.highFrequencyThreshold);
            self.occursInAfrica(diseaseGroup.occursInAfrica);
        });

        var diseaseGroupId;
        self.minNewOccurrences = ko.observable();
        self.minDataVolume = ko.observable();
        self.minDistinctCountries = ko.observable();
        self.minHighFrequencyCountries = ko.observable();
        self.highFrequencyThreshold = ko.observable();
        self.occursInAfrica = ko.observable();

        self.notice = ko.observable();
        self.saveChanges = function () {
            var url = baseUrl + "admindiseasegroup/" + diseaseGroupId + "/savemodelrunparameters";
            var data = {
                minNewOccurrences: self.minNewOccurrences(),
                minDataVolume: self.minDataVolume(),
                minDistinctCountries: self.minDistinctCountries(),
                minHighFrequencyCountries: self.minHighFrequencyCountries(),
                highFrequencyThreshold: self.highFrequencyThreshold(),
                occursInAfrica: self.occursInAfrica()
            };
            $.post(url, data)
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving", priority: "warning"}); });
        };
    };
});