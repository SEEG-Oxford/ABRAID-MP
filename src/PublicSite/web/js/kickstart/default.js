/*global require:false, baseUrl:false*/
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "navbar",
        "bootstrap",
        "domReady!"
    ], function (setupNavbar) {
            setupNavbar();
        }
    );
});
