/* Kick-start JS for the page on which password reset requests are completed.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
            "ko",
            "app/user/reset/PasswordResetProcessFormViewModel",
            "domReady!",
            "shared/navbar",
            "login"
        ], function (ko, PasswordResetProcessFormViewModel, doc) {
            ko.applyBindings(
                ko.validatedObservable(new PasswordResetProcessFormViewModel(baseUrl, data.id, data.key)),
                doc.getElementById("request-account-reset-form"));
        }
    );
});