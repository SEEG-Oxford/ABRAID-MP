/*
 * AMD defining the view model for the atlas layer selector.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (availableLayers) {
        var self = this;

        self.types = [
            { display: "disease risk", id: "mean" },
            { display: "risk uncertainty", id: "uncertainty" }
        ];
        self.selectedType = ko.observable(self.types[0])

        self.diseases = availableLayers;
        self.selectedDisease = ko.observable(self.diseases[0]);

        self.runs = ko.computed(function () {
            return self.selectedDisease().runs;
        }, self);
        self.selectedRun = ko.observable(self.runs()[0]);

        self.selectedLayer = ko.computed(function () {
            return self.selectedRun().id + "_" + self.selectedType().id;
        }, self).publishOn("layer-changed");
    };
});
