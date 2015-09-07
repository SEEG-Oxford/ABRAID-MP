/* Kick-start JS for the data extractor tool page.
 * Copyright (c) 2015 University of Oxford
 */
/* global require:false, baseUrl: false, data: false */
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/tools/DataExtractorViewModel",
        "app/atlas/WmsLayerParameterFactory",
        "domReady!",
        "shared/navbar",
        "analytics"
    ], function (ko, DataExtractorViewModel, WmsLayerParameterFactory, doc) {
        var wmsParameterFactory = new WmsLayerParameterFactory();
        ko.applyBindings(
            ko.validatedObservable(
                new DataExtractorViewModel(baseUrl, data.wmsUrl, data.runs, data.countries, wmsParameterFactory)),
            doc.getElementById("page"));
    });
});
