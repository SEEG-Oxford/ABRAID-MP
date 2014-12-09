/* Kick-start JS for the page on which new password reset requests are issued.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
            "ko",
            "app/user/reset/PasswordResetRequestFormViewModel",
            "domReady!",
            "shared/navbar",
            "login"
        ], function (ko, PasswordResetRequestFormViewModel, doc) {
            ko.applyBindings(
                ko.validatedObservable(new PasswordResetRequestFormViewModel(baseUrl)),
                doc.getElementById("request-account-reset-form"));
        }
    );
});