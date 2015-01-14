/* Kick-start JS for the upload CSV page.
 * Copyright (c) 2014 University of Oxford
 */
/* global require:false, baseUrl: false */
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/tools/UploadCsvViewModel",
        "domReady!",
        "shared/navbar",
        "analytics"
    ], function (ko, UploadCsvViewModel, doc) {
        ko.applyBindings(
            ko.validatedObservable(new UploadCsvViewModel(baseUrl)),
            doc.getElementById("upload-csv-form"));
    });
});
