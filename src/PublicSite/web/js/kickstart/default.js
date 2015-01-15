/*global require:false, baseUrl:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "shared/navbar",
        "login",
        "analytics"
    ], function () {
    });
});
