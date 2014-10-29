/*
 * AMD defining the layers displayed on the map, as chosen by the layer selector.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'no-features-to-review' - published by MapView.
 * - Events published:
 * -- 'layers-changed' if
 * --- validation type value changes
 * --- disease set changes on disease occurrences layer
 * --- disease changes on disease extent layer
 */
define(["ko"], function (ko) {
    "use strict";

    return function (diseaseInterests, allOtherDiseases) {
        var self = this;
        var DISEASE_OCCURRENCES = "disease occurrences";
        var DISEASE_EXTENT = "disease extent";

        var Group = function (label, children) {
            this.groupLabel = label;
            this.children = children;
        };

        // Return the first validator disease group of the expert's "Disease Interests", if they have any registered.
        // Otherwise, return the first disease set of the "All Other Diseases" list.
        var initialSelectedDiseaseSet = function () {
            return diseaseInterests.length !== 0 ? self.groups[0].children[0] : self.groups[1].children[0];
        };

        // View Model State
        self.validationTypes = [DISEASE_OCCURRENCES, DISEASE_EXTENT];
        self.selectedType = ko.observable(self.validationTypes[0]);
        self.groups = [
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ];
        self.selectedDiseaseSet = ko.observable(initialSelectedDiseaseSet());
        self.selectedDisease = ko.observable(self.selectedDiseaseSet().diseaseGroups[0]);

        // View State
        self.showDiseaseExtentLayer = ko.computed(function () {
            return (self.selectedType() === DISEASE_EXTENT);
        }, self);
        self.noFeaturesToReview = ko.observable(false).subscribeTo("no-features-to-review"); // Published by MapView
        self.notReadyForReview = ko.observable(false).subscribeTo("disease-not-ready-for-review");

        // Publish the changes
        ko.computed(function () {
            return {
                type: self.selectedType(),
                diseaseId: (self.selectedType() === DISEASE_OCCURRENCES) ? self.selectedDiseaseSet().id :
                                                                           self.selectedDisease().id,
                occurrenceOnly: self.selectedDisease().occurrenceOnly
            };
        }, self).publishOn("layers-changed");
    };
});
