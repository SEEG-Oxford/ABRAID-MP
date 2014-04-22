/**
 * Knockout view models to represent current state of map page.
 * Copyright (c) 2014 University of Oxford
 */
'use strict';

ko.utils.recursiveUnwrap = function (func) {
    if (typeof func != 'function') {
        return func;
    }
    return ko.utils.recursiveUnwrap(func());
};

// Custom binding to set the value on the flipclock.js counter
ko.bindingHandlers.counter = {
    init: function (element, valueAccessor) {
        var counter = $(element).FlipClock(ko.utils.recursiveUnwrap(valueAccessor), {
            clockFace: "Counter"
        });
        ko.utils.domData.set(element, "counter", counter);
    },
    update: function (element, valueAccessor) {
        var counter = ko.utils.domData.get(element, "counter");
        var value = ko.utils.recursiveUnwrap(valueAccessor);
        if ((value > 999) && ($(element).width() != 280)) {
            $(element).width(280);
        } else if ((value > 99) && ($(element).width() != 210)) {
            $(element).width(210);
        }
        counter.setValue(value);
    }
};

// Custom binding to format the datetime display with moment.js library
ko.bindingHandlers.date = {
    update: function (element, valueAccessor) {
        var date = ko.utils.recursiveUnwrap(valueAccessor);
        $(element).text(moment(date).lang('en-gb').format('LL'));
    }
};

ko.bindingHandlers.option = {
    update: function (element, valueAccessor) {
        var value = ko.utils.recursiveUnwrap(valueAccessor);
        ko.selectExtensions.writeValue(element, value);
    }
};

var DataValidationViewModels = (function () {

    function Group(label, children) {
        this.groupLabel = label;
        this.children = ko.observableArray(children);
    }

    var LayerSelectorViewModel = function () {
        this.validationTypes = ko.observableArray(["disease occurrences", "disease extent"]);
        this.selectedType = ko.observable("disease occurrences");
        this.selectedType.subscribe(function () {
            LeafletMap.toggleValidationTypeLayer();
        });
        this.groups = ko.observableArray([
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ]);
        this.selectedDisease = ko.observable();
        this.selectedDisease.subscribe(function () {
            DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
            LeafletMap.switchDiseaseOccurrenceLayer(this.selectedDisease().id);
        }, this);
        this.selectedDiseaseGroup = ko.observable();
        this.selectedDiseaseGroup.subscribe(function () {
           LeafletMap.switchDiseaseExtentLayer(this.selectedDiseaseGroup().id);
        }, this);
        this.noOccurrencesToReview = ko.observable(false);
    };

    function createUrl(langPair, summary) {
        return "http://translate.google.com/?" + "langpair=" + langPair + "&" + "text=" + encodeURIComponent(summary);
    }

    var SelectedPointViewModel = function () {
        this.selectedPoint = ko.observable(null);
        this.hasSelectedPoint = ko.computed(function () {
            return this.selectedPoint() !== null;
        }, this);
        this.clearSelectedPoint = function () {
            this.selectedPoint(null);
        };
        this.translationUrl = ko.computed(function () {
            if (this.hasSelectedPoint()) {
                var langPair = (this.selectedPoint().properties.alert.feedLanguage || "auto") + "|auto";
                var summary = this.selectedPoint().properties.alert.summary;
                var url = createUrl(langPair, summary);

                // If the encoded URL is too long, remove the last word from the summary
                var lastIndex;
                while (url.length > 2048) {
                    lastIndex = summary.lastIndexOf(" ");
                    summary = summary.substring(0, lastIndex);
                    url = createUrl(langPair, summary);
                }
                return url;
            }
        }, this);
        this.submitReview = function (review) {
            return function () {
                var diseaseId = DataValidationViewModels.layerSelectorViewModel.selectedDisease().id;
                var feature = DataValidationViewModels.selectedPointViewModel.selectedPoint();
                var occurrenceId = feature.id;
                var url = baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences/" + occurrenceId + "/validate";
                $.post(url, { review: review })
                    .done(function () {
                        // Status 2xx
                        // Display a success alert, remove the point from the map and side panel, incrememt the counter
                        $("#submitReviewSuccess").fadeIn(1000);
                        DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
                        LeafletMap.removeReviewedPoint(feature.id);
                        DataValidationViewModels.counterViewModel.incrementDiseaseOccurrenceReviewCount();
                    })
                    .fail(function (xhr) {
                        alert("Something went wrong. Please try again. " + xhr.responseText);
                    });
            };
        };
    };
    var CounterViewModel = function () {
        this.diseaseOccurrenceReviewCount = ko.observable(diseaseOccurrenceReviewCount);
        this.incrementDiseaseOccurrenceReviewCount = function () {
            this.diseaseOccurrenceReviewCount(this.diseaseOccurrenceReviewCount() + 1);
        };
    };

    var layerSelectorViewModel = new LayerSelectorViewModel();
    var selectedPointViewModel = new SelectedPointViewModel();
    var counterViewModel = new CounterViewModel();

    $(document).ready(function () {
        ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
        ko.applyBindings(selectedPointViewModel, $("#datapointInfo")[0]);
        if (loggedIn) {
            ko.applyBindings(counterViewModel, $("#counterDiv")[0]);
        }
    });

    return {
        layerSelectorViewModel: layerSelectorViewModel,
        selectedPointViewModel: selectedPointViewModel,
        counterViewModel: counterViewModel
    };
}());
