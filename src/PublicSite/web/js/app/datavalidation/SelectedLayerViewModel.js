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
define(["ko", "underscore"], function (ko, _) {
    "use strict";

    return function (diseaseInterests, allOtherDiseases, diseasesNeedingExtentInput, diseasesNeedingOccurrenceInput) {
        var self = this;
        var DISEASE_OCCURRENCES = "disease occurrences";
        var DISEASE_EXTENT = "disease extent";
        var getFirstModeNeedingInput = function () {
            if (diseasesNeedingExtentInput.length === 0 && diseasesNeedingOccurrenceInput.length !== 0) {
                return DISEASE_OCCURRENCES;
            }
            return DISEASE_EXTENT;
        };
        var Group = function (label, children) {
            this.groupLabel = label;
            this.children = children;
        };

        var getValidatorDiseaseGroups = function () {
            return _(self.groups[0].children).union(self.groups[1].children);
        };

        var getInputRequiredSetForCurrentMode = function () {
            return self.selectedType() === DISEASE_OCCURRENCES ?
                diseasesNeedingOccurrenceInput : diseasesNeedingExtentInput;
        };

        var getFirstDiseaseNeedingInput = function (diseaseGroups, withFallback) {
            var needingInput = getInputRequiredSetForCurrentMode();
            var disease = _(diseaseGroups).find(function (disease) { return _(needingInput).contains(disease.id); });
            if (withFallback && !disease) {
                return diseaseGroups[0];
            }
            return disease;
        };

        var getFirstValidatorDiseaseGroupNeedingInput = function () {
            var validatorDiseaseGroups = getValidatorDiseaseGroups();
            var group = _(validatorDiseaseGroups)
                .find(function (vdg) {
                    return getFirstDiseaseNeedingInput(vdg.diseaseGroups) !== undefined;
                });
            return group || validatorDiseaseGroups[0];
        };

        // View Model State
        self.validationTypes = [DISEASE_EXTENT, DISEASE_OCCURRENCES];
        self.selectedType = ko.observable(getFirstModeNeedingInput());
        self.groups = [
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ];
        self.selectedDiseaseSet = ko.observable(getFirstValidatorDiseaseGroupNeedingInput());
        self.selectedDiseaseSet.subscribe(function (newValue) {
            self.selectedDisease(getFirstDiseaseNeedingInput(newValue.diseaseGroups, true));
        });
        self.selectedDisease = ko.observable(
            getFirstDiseaseNeedingInput(self.selectedDiseaseSet().diseaseGroups, true));

        // View State
        self.showDiseaseExtentLayer = ko.computed(function () {
            return (self.selectedType() === DISEASE_EXTENT);
        }, self);
        self.noFeaturesToReview = ko.observable(false).subscribeTo("no-features-to-review"); // Published by MapView

        // Publish the changes
        ko.computed(function () {
            var isOccurrenceLayer = (self.selectedType() === DISEASE_OCCURRENCES);
            return {
                type: self.selectedType(),
                diseaseId: isOccurrenceLayer ? self.selectedDiseaseSet().id : self.selectedDisease().id,
                diseaseName: isOccurrenceLayer ? self.selectedDiseaseSet().name : self.selectedDisease().name
            };
        }, self).publishOn("layers-changed");
    };
});
