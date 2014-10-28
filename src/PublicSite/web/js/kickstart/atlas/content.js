/* Apply KO bindings for the atlas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "app/atlas/AtlasViewModel",
        "app/atlas/CovariateInfluencesViewModel",
        "app/atlas/DownloadLinksViewModel",
        "app/atlas/LayerSelectorViewModel",
        "app/atlas/SubmodelStatisticsViewModel",
        "domReady!"
    ], function (ko, AtlasView, AtlasViewModel, CovariateInfluencesViewModel, DownloadLinksViewModel, LayerSelectorViewModel,
                 SubmodelStatisticsViewModel, doc) {
        ko.applyBindings(
            new AtlasViewModel(
                new CovariateInfluencesViewModel(),
                new DownloadLinksViewModel(baseUrl, data.wmsUrl),
                new SubmodelStatisticsViewModel()
            ),
            doc.getElementById("infoButtons")
        );

        var map = new AtlasView(data.wmsUrl); // jshint ignore:line

        // NB. ViewModels subscribing to events published by LayerSelector must be defined first.
        ko.applyBindings(
            new LayerSelectorViewModel(data.layers),
            doc.getElementById("layerSelector")
        );
    });
});
