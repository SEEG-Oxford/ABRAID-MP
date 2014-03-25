/**
 * Knockout view models to represent current state of map page.
 * Copyright (c) 2014 University of Oxford
 */
'use strict';

var DataValidationViewModels = (function() {
    var LayerSelectorViewModel = function () {
        this.validationTypes = ko.observableArray(["disease occurrences", "disease extent"]);
        this.selectedType = ko.observable();
        this.diseaseInterests = ko.observableArray(diseaseInterests);
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
                        DataValidationViewModels.counterViewModel.reviewCount(DataValidationViewModels.counterViewModel.reviewCount() + 1);
                    })
                    .fail(function (xhr) {
                        alert("Something went wrong. Please try again. " + xhr.responseText);
                    });
            };
        };
    };

    var CounterViewModel = function() {
        this.reviewCount = ko.observable();
    }

    var layerSelectorViewModel = new LayerSelectorViewModel();
    var selectedPointViewModel = new SelectedPointViewModel();
    var counterViewModel = new CounterViewModel();

    $(document).ready(function () {
        ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
        ko.applyBindings(selectedPointViewModel, $("#datapointInfo")[0]);
        ko.applyBindings(counterViewModel, $("#counter")[0]);
    });

    return {
        layerSelectorViewModel: layerSelectorViewModel,
        selectedPointViewModel: selectedPointViewModel,
        counterViewModel: counterViewModel
    }
}());




