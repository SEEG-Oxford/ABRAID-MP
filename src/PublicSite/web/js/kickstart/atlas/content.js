/* Apply KO bindings for the atlas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "app/atlas/DownloadLinksViewModel",
        "app/atlas/LayerSelectorViewModel",
        "app/atlas/ModelRunPredictorsViewModel",
        "domReady!"
    ], function (ko, AtlasView, DownloadLinksViewModel, LayerSelectorViewModel, ModelRunPredictorsViewModel, doc) {
        var map = new AtlasView(data.wmsUrl); // jshint ignore:line

        ko.applyBindings(
            new DownloadLinksViewModel(baseUrl, data.wmsUrl),
            doc.getElementById("downloadLinks")
        );

        ko.applyBindings(
            new ModelRunPredictorsViewModel(data.layers),
            doc.getElementById("covariates")
        );

        // NB. ViewModels subscribing to events published by LayerSelector must be defined first.
        ko.applyBindings(
            new LayerSelectorViewModel(data.layers),
            doc.getElementById("layerSelector")
        );
    });
});
