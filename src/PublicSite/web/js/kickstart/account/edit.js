/* Apply KO bindings for the edit account details page.
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
        "shared/navbar"
    ], function (ko, $, _, AccountDetailsFormViewModel, DiseaseInterestListViewModel, doc) {
            var redirectPage = function () {
                // In this version of the view, we don't want a redirect.
            };

            var diseaseInterestListViewModel =
                new DiseaseInterestListViewModel(initialExpert, diseases);

            var accountDetailsFormViewModel = new AccountDetailsFormViewModel(
                    baseUrl, "account/edit", {}, initialExpert, redirectPage, diseaseInterestListViewModel);

            ko.applyBindings(
                ko.validatedObservable(accountDetailsFormViewModel),
                doc.getElementById("account-body"));
        }
    );
});
