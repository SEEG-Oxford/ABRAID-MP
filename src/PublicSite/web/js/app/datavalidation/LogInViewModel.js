/**
 * The AJAX call to Spring Security when login button is clicked.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, refresh, forceRebind) {
        var self = this;
        self.formUsername = ko.observable("");
        self.formPassword = ko.observable("");
        self.formAlert = ko.observable("Log in via ABRAID account");
        self.submitting = ko.observable(false);
        self.submit = function () {
            self.formAlert("<i class='fa fa-spinner'></i>&nbsp;Attempting  login ...");

            // Force the observables to bind "NOW!" this works around the fact that FF's form auto fill doesn't trigger
            // the events that would cause the bind. See:
            // https://github.com/knockout/knockout/issues/648
            // https://bugzilla.mozilla.org/show_bug.cgi?id=87943
            forceRebind();

            if (self.formUsername() !== "" && self.formPassword() !== "") {
                self.submitting(true);
                $.post(baseUrl + "j_spring_security_check", {
                        "j_username": self.formUsername(),
                        "j_password": self.formPassword()
                    })
                    .done(function () {
                        // Status 2xx
                        self.formAlert("<span class='text-success'>Success</span>");
                        refresh();
                    })
                    .fail(function (xhr) {
                        // Status Unauthorized 401 - Display authentication error message to user: eg Bad Credentials
                        self.formAlert("<span class='text-danger'>" + xhr.responseText + "</span>");
                    })
                    .always(function () {
                        self.submitting(false);
                    });
            } else {
                self.formAlert("<span class='text-warning'>Username &amp; password required!</span>");
            }
        };
    };
});
