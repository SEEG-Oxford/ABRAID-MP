/* Apply KO bindings for the altas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, alert:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "domReady!"
    ], function (ko, setupMap) {
            setupMap(baseUrl, data.wmsUrl, data.layers);
        }
    );
});
