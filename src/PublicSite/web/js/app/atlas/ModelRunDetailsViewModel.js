/* AMD defining the view models containing additional information about the active layer on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define(["ko"], function (ko) {
    "use strict";

    return function (covariateInfluencesViewModel, downloadLinksViewModel, statisticsViewModel) {
        var self = this;

        self.activeLayer = ko.observable().subscribeTo("active-atlas-layer");

        self.covariateInfluencesViewModel = covariateInfluencesViewModel;
        self.downloadLinksViewModel = downloadLinksViewModel;
        self.statisticsViewModel = statisticsViewModel;
    };
});
