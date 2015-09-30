/* Kick-start JS for the index page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl: false, initialMiscData:false*/
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require(["ko",
             "app/index/AuthViewModel",
             "app/index/MiscViewModel",
             "domReady!",
             "shared/navbar"
    ], function (ko, AuthViewModel, MiscViewModel, doc) {


        ko.applyBindings(
            ko.validatedObservable(new AuthViewModel(baseUrl)),
            doc.getElementById("auth-body"));

        ko.applyBindings(
            new MiscViewModel(initialMiscData, baseUrl),
            doc.getElementById("misc-body"));
    });
});
