/* Kick-start JS for the model admin page.
 * Copyright (c) 2015 University of Oxford
 */
/* global require:false, baseUrl: false, initialRepoData:false */
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/admin/RepositoryViewModel",
        "domReady!",
        "shared/navbar",
        "analytics"
    ], function (ko, RepositoryViewModel, doc) {
        ko.applyBindings(
            ko.validatedObservable(new RepositoryViewModel(initialRepoData, baseUrl)),
            doc.getElementById("repo-body"));
    });
});
