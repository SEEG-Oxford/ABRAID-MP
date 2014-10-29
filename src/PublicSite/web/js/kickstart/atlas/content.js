/* Apply KO bindings for the atlas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "app/atlas/AtlasInformationViewModel",
        "app/atlas/CovariateInfluencesViewModel",
        "app/atlas/DownloadLinksViewModel",
        "app/atlas/LayerSelectorViewModel",
        "app/atlas/SubmodelStatisticsViewModel",
        "domReady!"
    ], function (ko, AtlasView, AtlasInformationViewModel, CovariateInfluencesViewModel, DownloadLinksViewModel,
                 LayerSelectorViewModel, SubmodelStatisticsViewModel, doc) {
        ko.applyBindings(
            new AtlasInformationViewModel(
                new CovariateInfluencesViewModel(baseUrl),
                new DownloadLinksViewModel(baseUrl, data.wmsUrl),
                new SubmodelStatisticsViewModel(baseUrl)
            ),
            doc.getElementById("atlasInformation")
        );

        var map = new AtlasView(data.wmsUrl); // jshint ignore:line

        // NB. ViewModels subscribing to events published by LayerSelector must be defined first.
        ko.applyBindings(
            new LayerSelectorViewModel(data.layers),
            doc.getElementById("layerSelector")
        );
    });
});
