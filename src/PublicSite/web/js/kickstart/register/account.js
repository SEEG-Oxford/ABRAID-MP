/*global require:false, baseUrl:false, initialAlerts:false, initialExpert:false, Recaptcha:false*/
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "navbar",
        "app/register/AccountRegistrationFormViewModel",
        "domReady!"
    ], function (ko, $, setupNavbar, AccountRegistrationFormViewModel, doc) {
            setupNavbar();

            var redirectPage = function (newURL) {
                doc.location = newURL;
            };

            ko.applyBindings(
                ko.validatedObservable(new AccountRegistrationFormViewModel(baseUrl, initialExpert, initialAlerts, Recaptcha, redirectPage)),
                doc.getElementById("account-body"));
        }
    );
});
