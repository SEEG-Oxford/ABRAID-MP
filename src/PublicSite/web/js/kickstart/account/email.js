/* Apply KO bindings for the email change page.
 * Copyright (c) 2015 University of Oxford
 */
/*global require:false, baseUrl:false, currentEmail:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/user/EmailChangeFormViewModel",
        "domReady!",
        "analytics"
    ], function (ko, EmailChangeFormViewModel, doc) {
            ko.applyBindings(
                ko.validatedObservable(new EmailChangeFormViewModel(baseUrl, currentEmail)),
                doc.getElementById("email-body")
            );
        }
    );
});
