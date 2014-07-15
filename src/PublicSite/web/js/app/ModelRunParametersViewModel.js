/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "underscore",
    "app/ModelRunParametersPayload"
], function (ko, $, _, ModelRunParametersPayload) {
    "use strict";

    return function (baseUrl, diseaseGroupSelectedEventName) {
        var self = this;

        self.minNewOccurrences = ko.validatedObservable().extend({ digit: true, min: 0 });
        self.minDataVolume = ko.validatedObservable().extend({ digit: true, min: 0 });
        self.minDistinctCountries = ko.validatedObservable().extend({ digit: true, min: 0 });
        self.minHighFrequencyCountries = ko.validatedObservable().extend({ digit: true, min: 0 });
        self.highFrequencyThreshold = ko.validatedObservable().extend({ digit: true, min: 0 });
        self.occursInAfrica = ko.observable();

        var diseaseGroupId;
        var originalPayload;
        var data = ko.computed(function () { return ModelRunParametersPayload.fromViewModel(self); });

        self.notice = ko.observable();
        self.enableSaveButton = ko.computed(function () {
            return !(_.isEqual(originalPayload, data()));
        });
        self.save = function () {
            var url = baseUrl + "admindiseasegroup/" + diseaseGroupId + "/modelrunparameters";
            $.post(url, data())
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving", priority: "warning"}); });
        };

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
            originalPayload = ModelRunParametersPayload.fromJson(diseaseGroup);
            self.minNewOccurrences(diseaseGroup.minNewOccurrences);
            self.minDataVolume(diseaseGroup.minDataVolume);
            self.minDistinctCountries(diseaseGroup.minDistinctCountries);
            self.minHighFrequencyCountries(diseaseGroup.minHighFrequencyCountries);
            self.highFrequencyThreshold(diseaseGroup.highFrequencyThreshold);
            self.occursInAfrica(diseaseGroup.occursInAfrica);
        });
    };
});