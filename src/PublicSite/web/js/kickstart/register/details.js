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

            // Marshal the incoming json
            _(initialExpert).defaults({ name: "", job: "", institution: "", publiclyVisible: false, validatorDiseaseGroups: []});

            var diseaseInterestListViewModel = new DiseaseInterestListViewModel(initialExpert, diseases);
            var accountDetailsFormViewModel = new AccountDetailsFormViewModel(baseUrl, initialExpert, redirectPage, diseaseInterestListViewModel);
            ko.applyBindings(
                ko.validatedObservable(accountDetailsFormViewModel),
                doc.getElementById("account-body"));
        }
    );
});
