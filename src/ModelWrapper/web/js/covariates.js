/* Kick-start JS for the covariates page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl: false, initialData:false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require([
        "ko",
        "app/CovariatesViewModel",
        "navbar",
        "domReady!"
    ], function (ko, CovariatesViewModel, setupNavbar, doc) {
        setupNavbar();

        ko.applyBindings(
            ko.validatedObservable(new CovariatesViewModel(baseUrl, initialData)),
            doc.getElementById("covariate-body"));
    });
});
