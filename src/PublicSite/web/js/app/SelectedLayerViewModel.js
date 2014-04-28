/*global define:false*/
define(["ko"], function (ko) {
    "use strict";

    return function (diseaseInterests, allOtherDiseases) {
        var self = this;

        var DISEASE_OCCURRENCES = "disease occurrences";
        var DISEASE_EXTENT = "disease extent";

        var Group = function (label, children) {
            this.groupLabel = label;
            this.children = ko.observableArray(children);
        };

        var notifyMap = function () {
            ko.postbox.publish("layers-changed",
                {
                    validationType: self.selectedType(),
                    diseaseSet: self.selectedDiseaseSet(),
                    disease: self.selectedDisease()
                }
            );
        };

        self.validationTypes = ko.observableArray([DISEASE_OCCURRENCES, DISEASE_EXTENT]);
        self.selectedType = ko.observable(DISEASE_OCCURRENCES);
        self.groups = ko.observableArray([
            new Group("Your Disease Interests", diseaseInterests),
            new Group("Other Diseases", allOtherDiseases)
        ]);
        self.selectedDiseaseSet = ko.observable(diseaseInterests[0]);
        self.selectedDisease = ko.observable(self.selectedDiseaseSet().diseaseGroups()[0]);
        self.noOccurrencesToReview = ko.observable(false).subscribeTo("noOccurrencesToReview"); // Published by MapView

        self.selectedType.subscribe(notifyMap);
        self.selectedDiseaseSet.subscribe(notifyMap);
        self.selectedDisease.subscribe(notifyMap);
    };
});
