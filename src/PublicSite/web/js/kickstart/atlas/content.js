/* Apply KO bindings for the altas page.
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
        "domReady!"
    ], function (ko, AtlasView, DownloadLinksViewModel, LayerSelectorViewModel, doc) {
        var map = new AtlasView(data.wmsUrl); // jshint ignore:line

        ko.applyBindings(
            new DownloadLinksViewModel(baseUrl, data.wmsUrl),
            doc.getElementById("download-links"));

        ko.applyBindings(
            new LayerSelectorViewModel(data.layers),
            doc.getElementById("layerSelector"));
    });
});
