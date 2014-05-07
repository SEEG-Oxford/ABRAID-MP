/*global require:false*/
require(["require.conf"], function () {
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
