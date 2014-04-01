/**
 * Knockout view models to represent current state of map page.
 * Copyright (c) 2014 University of Oxford
 */
'use strict';

ko.utils.recursiveUnwrap = function (func) {
    if (typeof func != 'function') {
        return func;
    } else {
        return ko.utils.recursiveUnwrap(func());
    }
};

// Custom binding to set the value on the flipclock.js counter
ko.bindingHandlers.counter = {
    init: function(element, valueAccessor) {
        var counter = $(element).FlipClock(ko.utils.recursiveUnwrap(valueAccessor), {
            clockFace: "Counter"
        });
        ko.utils.domData.set(element, "counter", counter);
    },
    update: function(element, valueAccessor) {
        var counter = ko.utils.domData.get(element, "counter");
        counter.setValue(ko.utils.recursiveUnwrap(valueAccessor));
    }
};

// Custom binding to format the datetime display with moment.js library
ko.bindingHandlers.date = {
    update: function(element, valueAccessor) {
        var date = ko.utils.recursiveUnwrap(valueAccessor);
        $(element).text(moment(date).lang('en-gb').format('LL'));
    }
};

ko.bindingHandlers.option = {
    update: function(element, valueAccessor) {
        var value = ko.utils.recursiveUnwrap(valueAccessor);
        ko.selectExtensions.writeValue(element, value);
    }
};

var DataValidationViewModels = (function() {

    function Group(label, children) {
        this.groupLabel = ko.observable(label);
        this.children = ko.observableArray(children);
    }

    function Disease(diseaseInterest) {
        this.diseaseLabel = ko.observable(diseaseInterest.name);
        this.id = ko.observable(diseaseInterest.id);
        this.reviewCount = ko.observable(diseaseInterest.reviewCount);
    }

    function convertDiseaseInterestsToObservableDiseases() {
        var list = [];
        for (var i = 0; i < diseaseInterests.length; i++) {
            list[i] = new Disease(diseaseInterests[i]);
        }
        return list;
    }

    var LayerSelectorViewModel = function () {
        this.validationTypes = ko.observableArray(["disease occurrences", "disease extent"]);
        this.selectedType = ko.observable();
        this.groups = ko.observableArray([
            new Group("Your Disease Interests", convertDiseaseInterestsToObservableDiseases ),
            new Group("Other Diseases", [ ])
        ]);
        this.selectedDisease = ko.observable();
        this.selectedDisease.subscribe(function () {
            DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
            LeafletMap.switchDiseaseLayer(this.selectedDisease().id);
            DataValidationViewModels.counterViewModel.reviewCount(this.selectedDisease().reviewCount);
        }, this);
    };

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
                var googleTranslateUrl = "http://translate.google.com/"
                var feedLanguage = this.selectedPoint().properties.alert.feedLanguage;
                var encodedSummary = encodeURIComponent(this.selectedPoint().properties.alert.summary);
                if (feedLanguage != null) {
                    return (googleTranslateUrl + "?langpair=" + feedLanguage + "%7Cen&text=" + encodedSummary).substring(0,2048);
                } else {
                    return (googleTranslateUrl + "#auto/en/" + encodedSummary).substring(0,2048);
                }
            }
        }, this);
        this.submitReview = function(review) {
            return function () {
                var diseaseId = DataValidationViewModels.layerSelectorViewModel.selectedDisease().id;
                var feature = DataValidationViewModels.selectedPointViewModel.selectedPoint();
                var occurrenceId = feature.id;
                var url = baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences/" + occurrenceId + "/validate";
                $.post(url, { review: review })
                    .done(function(data, status, xhr) {
                        // Status 2xx
                        // Remove the point from the map and side panel, display a success alert, increment the counter
                        DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
                        $("#submitReviewSuccess").fadeIn();
                        LeafletMap.removeReviewedPoint(feature.id);
                        DataValidationViewModels.counterViewModel.incrementCount();
                    })
                    .fail(function (xhr) {
                        alert("Something went wrong. Please try again. " + xhr.responseText);
                    });
            };
        };
    };

    var CounterViewModel = function() {
        this.reviewCount = ko.observable();
        this.incrementCount = function () {
            this.reviewCount(this.reviewCount() + 1);
        };
    }

    var layerSelectorViewModel = new LayerSelectorViewModel();
    var selectedPointViewModel = new SelectedPointViewModel();
    var counterViewModel = new CounterViewModel();

    $(document).ready(function () {
        ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
        ko.applyBindings(selectedPointViewModel, $("#datapointInfo")[0]);
        ko.applyBindings(counterViewModel, $("#counterDiv")[0]);
    });

    return {
        layerSelectorViewModel: layerSelectorViewModel,
        selectedPointViewModel: selectedPointViewModel,
        counterViewModel: counterViewModel
    }
}());




