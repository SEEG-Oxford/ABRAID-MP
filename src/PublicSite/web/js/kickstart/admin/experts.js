/* Kick-start JS for the administration experts page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, experts:false*/
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/admin/ExpertAdministrationViewModel",
        "domReady!",
        "shared/navbar",
        "analytics"
    ], function (ko, ExpertAdministrationViewModel, doc) {

        ko.applyBindings(
            ko.validatedObservable(new ExpertAdministrationViewModel(baseUrl, experts)),
            doc.getElementById("experts-body"));
    });
});
