/*global define:false, diseaseInterests:false, allOtherDiseases:false*/
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        var Group = function (label, children) {
            this.groupLabel = label;
            this.children = ko.observableArray(children);
        };

        self.validationTypes = ko.observableArray(["disease occurrences", "disease extent"]);
        self.selectedType = ko.observable("disease occurrences");
//        self.selectedType.subscribe(function () {
//            LeafletMap.toggleValidationTypeLayer();
//        });
        self.groups = ko.observableArray([
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ]);
        self.selectedDiseaseSet = ko.observable();
//        self.selectedDiseaseSet.subscribe(function () {
//            DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
//            LeafletMap.switchDiseaseOccurrenceLayer(this.selectedDiseaseSet().id);
//        });
        self.selectedDisease = ko.observable();
//        self.selectedDisease.subscribe(function () {
//            LeafletMap.switchDiseaseExtentLayer(this.selectedDisease().id);
//        });
        self.noOccurrencesToReview = ko.observable(false);
    };
});
