/**
 * Knockout view models to represent current state of map page.
 * Copyright (c) 2014 University of Oxford
 */
var ViewModels = {
    LayerSelectorViewModel: function () {
        this.validationTypes = ["disease occurrences", "disease extent"];
        this.selectedType = ko.observable();
        this.diseaseInterests = diseaseInterests;
        this.selectedDisease = ko.observable();
        this.selectedDisease.subscribe(function () {
            // When a different disease is selected, clear the current data from the map
            LeafletMap.clusterLayer.clearLayers();
            LeafletMap.diseaseOccurrenceLayer.clearLayers();
            // Add the new feature collection to the clustered layer, and zoom to its bounds
            $.getJSON(baseUrl + 'datavalidation/diseases/' + this.selectedDisease().id + '/occurrences', function (featureCollection) {
                LeafletMap.clusterLayer.addLayer(LeafletMap.diseaseOccurrenceLayer.addData(featureCollection));
                LeafletMap.map.fitBounds(LeafletMap.diseaseOccurrenceLayer.getBounds());
            });
        }, this);
    },

    SelectedPointViewModel: function () {
        this.selectedPoint = ko.observable(null);
        this.hasSelectedPoint = ko.computed(function () {
            return this.selectedPoint() !== null;
        }, this);
        this.clearSelectedPoint = function () {
            this.selectedPoint(null);
        };
        this.userLoggedIn = ko.observable(false);
    }
};

var layerSelectorViewModel = new ViewModels.LayerSelectorViewModel();
var selectedPointViewModel = new ViewModels.SelectedPointViewModel();

$(document).ready(function () {
    'use strict';

    ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
    ko.applyBindings(selectedPointViewModel, $("#sidePanel")[0]);
});
