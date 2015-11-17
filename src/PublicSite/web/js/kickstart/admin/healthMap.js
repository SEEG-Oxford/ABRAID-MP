/* Kick-start JS for the administration HealthMap page.
 * Copyright (c) 2015 University of Oxford
 */
/*global require:false, baseUrl:false, healthMapDiseases:false, healthMapSubDiseases:false, abraidDiseases:false */
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/admin/HealthMapAdministrationViewModel",
        "domReady!",
        "shared/navbar",
        "analytics"
    ], function (ko, HealthMapAdministrationViewModel, doc) {

        ko.applyBindings(
            ko.validatedObservable(
                new HealthMapAdministrationViewModel(baseUrl, healthMapDiseases, healthMapSubDiseases, abraidDiseases)),
            doc.getElementById("healthMapAdminPage"));
    });
});
