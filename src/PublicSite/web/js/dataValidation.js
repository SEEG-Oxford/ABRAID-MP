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
            LeafletMap.updateDiseaseLayer(this.selectedDisease().id);
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
    };

    var layerSelectorViewModel = new LayerSelectorViewModel();
    var selectedPointViewModel = new SelectedPointViewModel();

    $(document).ready(function () {
        ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
        ko.applyBindings(selectedPointViewModel, $("#sidePanel")[0]);
    });

    return {
        layerSelectorViewModel: layerSelectorViewModel,
        selectedPointViewModel: selectedPointViewModel
    }
}());




