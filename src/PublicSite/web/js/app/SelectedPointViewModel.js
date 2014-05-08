/**
 * An AMD defining the SelectedPointViewModel to hold the state of the selected occurrence point on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'point-selected' - published by MapView.
 * -- 'layers-changed' - published by SelectedLayerViewModel.
 * - Events published:
 * -- 'point-reviewed' on successful submitReview POST
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, alert, counter) {
        var self = this;

        var createTranslationUrl = function (langPair, summary) {
            return "http://translate.google.com/?" +
                "langpair=" + langPair + "&" +
                "text=" + encodeURIComponent(summary);
        };

        // If the encoded URL is too long, remove the last word from the summary
        var createTranslationUrlWithCorrectLength = function (langPair, summary) {
            var url = createTranslationUrl(langPair, summary);
            while (url.length > 2048) {
                var lastIndex = summary.lastIndexOf(" ");
                summary = summary.substring(0, lastIndex);
                url = createTranslationUrl(langPair, summary);
            }
            return url;
        };

        var diseaseId = null;
        ko.postbox.subscribe("layers-changed", function (value) {
            diseaseId = value.diseaseId;
        });

        self.counter = counter;
        self.selectedPoint = ko.observable(null).syncWith("point-selected");
        self.hasSelectedPoint = ko.computed(function () {
            return self.selectedPoint() !== null;
        });
        self.translationUrl = ko.computed(function () {
            if (self.hasSelectedPoint()) {
                var langPair = (self.selectedPoint().properties.alert.feedLanguage || "auto") + "|auto";
                var summary = self.selectedPoint().properties.alert.summary;
                return createTranslationUrlWithCorrectLength(langPair, summary);
            }
        });
        self.submitReview = function (review) {
            var occurrenceId = self.selectedPoint().id;
            var url = baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences/" + occurrenceId +
                "/validate";
            $.post(url, { review: review })
                .done(function () {
                    // Status 2xx
                    // Display a success alert, remove the point from the map and side panel, increment the counter
                    self.selectedPoint(null);
                    ko.postbox.publish("point-reviewed", occurrenceId);
                    $("#submitReviewSuccess").fadeIn(1000);
                })
                .fail(function () {
                    alert("Something went wrong. Please try again.");
                });
        };
    };
});
