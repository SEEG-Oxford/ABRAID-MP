/**
 * JS file for the login wiring.
 * Copyright (c) 2014 University of Oxford
 */
/*global baseUrl:false*/
define([
    "ko",
    "jquery",
    "app/datavalidation/LogInFormViewModel",
    "domReady!"
], function (ko, $, LogInFormViewModel, doc) {
    "use strict";
    if (!document.getElementById("login-nav") !== null) {
        var refresh = function () {
            // Refresh function may change, according to location of iframe (eg on TGHN site)
            window.top.location.reload();
        };

        var forceRebind = function () {
            // Force the observables to bind "NOW!" this works around the fact that FF's form auto fill doesn't
            // trigger the events that would cause the bind. See:
            // https://github.com/knockout/knockout/issues/648
            // https://bugzilla.mozilla.org/show_bug.cgi?id=87943
            $("input.ffAutoFillHack").trigger("change");
        };

        ko.applyBindings(
            new LogInFormViewModel(baseUrl, refresh, forceRebind),
            doc.getElementById("the-nav")
        );
    }
});
