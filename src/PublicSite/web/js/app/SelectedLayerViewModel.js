/*
 * AMD defining the layers displayed on the map, as chosen by the layer selector.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore"], function (ko, _) {
    "use strict";

    return function (diseaseInterests, allOtherDiseases) {
        var self = this;
        var DISEASE_OCCURRENCES = "disease occurrences";
        var DISEASE_EXTENT = "disease extent";

        var Group = function (label, children) {
            this.groupLabel = label;
            this.children = children;
        };

        // View Model State
        self.validationTypes = ko.observableArray([DISEASE_OCCURRENCES, DISEASE_EXTENT]);
        self.selectedType = ko.observable(self.validationTypes[0]);
        self.groups = [
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ];
        self.selectedDiseaseSet = ko.observable(self.groups[0].children[0]);
        self.selectedDisease = ko.observable(self.selectedDiseaseSet().diseaseGroups[0]);

        // View State
        self.showDiseaseExtentLayer = ko.computed(function () {
            return (self.selectedType() === DISEASE_EXTENT);
        });
        self.noFeaturesToReview = ko.observable(false).subscribeTo("no-features-to-review"); // Published by MapView

        // Publish the changes:
        // On disease extent layer - when the disease (on the last dropdown menu) is changed,
        // On disease occurrence layer - when the parent diseaseSet is changed, since child disease is not used.
        var layers = ko.computed(function () {
            return {
                type: self.selectedType(),
                diseaseSet: self.selectedDiseaseSet(),
                disease: self.selectedDisease()
            };
        });
        layers.subscribe(function (value) {
            if (value.type === DISEASE_OCCURRENCES || _(value.diseaseSet.diseaseGroups).contains(value.disease)) {
                ko.postbox.publish("layers-changed", value);
            }
        });
        // Publish the initial state
        ko.postbox.publish("layers-changed", layers());
    };
});
