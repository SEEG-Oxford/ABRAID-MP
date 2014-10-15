/*
 * AMD defining the view model for the atlas layer selector.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore"], function (ko, _) {
    "use strict";

    return function (availableLayers) {
        var self = this;

        if (availableLayers.length === 0) {
            availableLayers = [ { disease: "---", runs: [ { id: undefined, date: "---" } ] } ];
        }

        self.types = [
            { display: "disease risk", id: "mean" },
            { display: "risk uncertainty", id: "uncertainty" }
        ];
        self.selectedType = ko.observable(self.types[0]);

        self.diseases = _(availableLayers).sortBy("disease");
        self.selectedDisease = ko.observable(self.diseases[0]);

        self.runs = ko.computed(function () {
            return _(self.selectedDisease().runs).sortBy("date").reverse();
        }, self);
        self.selectedRun = ko.observable(self.runs()[0]);

        self.selectedLayer = ko.computed(function () {
            return self.selectedRun().id ? self.selectedRun().id + "_" + self.selectedType().id : undefined;
        }, self).publishOn("active-atlas-layer");
    };
});
