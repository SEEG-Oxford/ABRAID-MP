/*global require:false, baseUrl:false*/
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "navbar",
        "app/register/AccountRegistrationFormViewModel",
        "domReady!"
    ], function (setupNavbar) {
            setupNavbar();


        }
    );
});
