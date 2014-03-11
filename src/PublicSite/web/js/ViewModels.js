var ViewModels = (function () {
    return {
        LayerSelectorViewModel: function () {
            this.validationTypes = ["disease occurrences", "disease extent"];
            this.selectedType = ko.observable();
            this.diseaseInterests = diseaseInterests;
            this.selectedDisease = ko.observable();
            this.selectedDisease.subscribe(function () {
                Map.locationLayer.clearLayers();
                $.getJSON('datavalidation/diseases/' + this.selectedDisease().id + '/occurrences', function (featureCollection) {
                    Map.locationLayer.addData(featureCollection);
                    Map.map.fitBounds(Map.locationLayer.getBounds());
                });
            }, this);
        },
        SelectedPointViewModel: function () {
            this.selectedPoint = ko.observable(null);
            this.hasSelectedPoint = ko.computed(function(){
                return this.selectedPoint() != null;
            }, this);
            this.clearSelectedPoint = function() {
                this.selectedPoint(null);
            };
        }
    }
}());

var layerSelectorViewModel = new ViewModels.LayerSelectorViewModel();
var selectedPointViewModel = new ViewModels.SelectedPointViewModel();

$(document).ready(function () {
    ko.applyBindings(layerSelectorViewModel, $("#layerSelector")[0]);
    ko.applyBindings(selectedPointViewModel, $("#sidePanel")[0]);
});