/* AMD defining the view models containing additional information about the active layer on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define(["ko"], function (ko) {
    "use strict";

    return function (covariateInfluencesViewModel, downloadLinksViewModel, statisticsViewModel) {
        var self = this;

        var activeLayer = ko.observable().subscribeTo("active-atlas-layer");
        self.hasActiveLayer = ko.computed(function () {
            return (typeof activeLayer() !== "undefined");
        });

        var activeRun = ko.observable().subscribeTo("selected-run");
        self.startDate =  ko.computed(function () {
            return (typeof activeRun() !== "undefined") ? activeRun().rangeStart : "???";
        });
        self.endDate =  ko.computed(function () {
            return (typeof activeRun() !== "undefined") ? activeRun().rangeEnd : "???";
        });


        self.covariateInfluencesViewModel = covariateInfluencesViewModel;
        self.downloadLinksViewModel = downloadLinksViewModel;
        self.statisticsViewModel = statisticsViewModel;
    };
});
