/* Apply KO bindings for the password change page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/user/PasswordChangeFormViewModel",
        "domReady!"
    ], function (ko, PasswordChangeFormViewModel, doc) {
            ko.applyBindings(
                ko.validatedObservable(new PasswordChangeFormViewModel(baseUrl)),
                doc.getElementById("account-body"));
        }
    );
});
