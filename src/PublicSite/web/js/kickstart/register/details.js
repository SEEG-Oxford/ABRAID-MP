/* Apply KO bindings for the account registration details page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, initialExpert:false, diseases:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "underscore",
        "app/user/AccountDetailsFormViewModel",
        "app/user/DiseaseInterestListViewModel",
        "domReady!",
        "shared/navbar",
        "login"
    ], function (ko, $, _, AccountDetailsFormViewModel, DiseaseInterestListViewModel, doc) {
            var redirectPage = function (newURL) {
                doc.location = newURL;
            };

            var diseaseInterestListViewModel =
                new DiseaseInterestListViewModel(initialExpert, diseases);

            var accountDetailsFormViewModel =
                new AccountDetailsFormViewModel(baseUrl, "register/details", {
                    success: "Account creation step 2/2 successfully completed.",
                    fail: "Account creation step 2/2 unsuccessful."
                }, initialExpert, redirectPage, diseaseInterestListViewModel);

            ko.applyBindings(
                ko.validatedObservable(accountDetailsFormViewModel),
                doc.getElementById("details-form"));
        }
    );
});
