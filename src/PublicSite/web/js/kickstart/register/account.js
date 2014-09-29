/* Apply KO bindings for the account registration page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, initialAlerts:false, initialExpert:false, Recaptcha:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/user/AccountRegistrationFormViewModel",
        "domReady!",
        "shared/navbar",
        "login"
    ], function (ko, $, AccountRegistrationFormViewModel, doc) {
            var redirectPage = function (newURL) {
                doc.location = newURL;
            };

            var viewModel =
                new AccountRegistrationFormViewModel(baseUrl, initialExpert, initialAlerts, Recaptcha, redirectPage);

            ko.applyBindings(
                ko.validatedObservable(viewModel),
                doc.getElementById("account-body"));
        }
    );
});
