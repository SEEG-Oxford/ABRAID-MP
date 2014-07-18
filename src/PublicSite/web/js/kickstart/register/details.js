/* Apply KO bindings for the account registration details page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, initialExpert:false, diseases:false*/
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "underscore",
        "navbar",
        "app/register/AccountDetailsFormViewModel",
        "app/register/DiseaseInterestListViewModel",
        "domReady!"
    ], function (ko, $, _, setupNavbar, AccountDetailsFormViewModel, DiseaseInterestListViewModel, doc) {
            setupNavbar();

            var redirectPage = function (newURL) {
                doc.location = newURL;
            };

            var diseaseInterestListViewModel =
                new DiseaseInterestListViewModel(initialExpert, diseases);

            var accountDetailsFormViewModel =
                new AccountDetailsFormViewModel(baseUrl, initialExpert, redirectPage, diseaseInterestListViewModel);

            ko.applyBindings(
                ko.validatedObservable(accountDetailsFormViewModel),
                doc.getElementById("account-body"));
        }
    );
});
