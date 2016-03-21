/*
 * AMD defining the view model for the atlas layer selector.
 * Copyright (c) 2014 University of Oxford
 * - Events published:
 * -- 'active-atlas-type'
 * -- 'active-atlas-layer'
 * -- 'selected-run'
 */
define([
    "ko",
    "underscore"
], function (ko, _) {
    "use strict";

    return function (availableLayers, seegMember) {
        var self = this;

        if (availableLayers.length === 0) {
            availableLayers = [ {
                disease: "---",
                runs: [ { id: undefined, date: "---", rangeStart: "???", rangeEnd: "???"} ]
            } ];
        }

        self.types = [];
        self.types.push({ display: "disease risk", id: "mean" });
        if (seegMember) {
            self.types.push({ display: "risk uncertainty", id: "uncertainty" });
        }
        self.types.push({ display: "disease extent", id: "extent" });
        self.types.push({ display: "input occurrences", id: "occurrences" });

        self.selectedType = ko.observable(self.types[0]);

        self.diseases = _(availableLayers).sortBy("disease");
        self.selectedDisease = ko.observable(self.diseases[0]);

        self.runs = ko.computed(function () {
            return _(self.selectedDisease().runs).sortBy("date").reverse();
        }, self);
        self.selectedRun = ko.observable(self.runs()[0]);

        self.selectedLayer = ko.computed(function () {
            return !self.selectedRun().id || !self.selectedType().id ? undefined : {
                type: self.selectedType().id,
                run: self.selectedRun()
            };
        }, self).publishOn("active-atlas-layer");
    };
});
