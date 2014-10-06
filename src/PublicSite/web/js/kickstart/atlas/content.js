/* Apply KO bindings for the altas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "app/atlas/LayerSelectorViewModel",
        "domReady!"
    ], function (ko, setupMap, LayerSelectorViewModel, doc) {
            setupMap(data.wmsUrl);

            ko.applyBindings(
                new LayerSelectorViewModel(data.layers),
                doc.getElementById("layerSelector"));
        }
    );
});
