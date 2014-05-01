/*
 * AMD defining the layers displayed on the map, as chosen by the layer selector.
 * Copyright (c) 2014 University of Oxford
 * Events published:
 *  -- 'validation-type-changed' - published immediately on value change
 *  -- 'disease-set-changed'     - should only be published for disease occurrence layer
 *  -- 'disease-changed'         - only published for disease extent layer, since the dropdown to which it is bound is
 *                                 only visible on this layer
 *  Events subscribed to:
 *  -- 'no-features-to-review'   - boolean, if there are no points added to the map
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

        // View Model State
        self.validationTypes = [DISEASE_OCCURRENCES, DISEASE_EXTENT];
        self.selectedType = ko.observable(self.validationTypes[0]).publishOn("validation-type-changed");
        self.groups = [
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ];
        self.selectedDiseaseSet = ko.observable(self.groups[0].children[0]);
        self.selectedDiseaseSet.subscribe(function (value) {
            if (self.selectedType() === DISEASE_OCCURRENCES) {
                ko.postbox.publish("disease-set-changed", value);
            }
        });
        self.selectedDisease = ko.observable(self.selectedDiseaseSet().diseaseGroups[0]).publishOn("disease-changed");

        // View State
        self.showDiseaseExtentLayer = ko.computed(function () {
            return (self.selectedType() === DISEASE_EXTENT);
        });
        self.noFeaturesToReview = ko.observable(false).subscribeTo("no-features-to-review"); // Published by MapView

        // Publish the initial state
        ko.postbox.publish("disease-set-changed", self.selectedDiseaseSet());
    };
});
