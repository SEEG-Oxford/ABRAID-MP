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

        self.username = ko.observable("");
        self.password = ko.observable("");

        self.notice = ko.observable("Log in via ABRAID account");
        self.isSubmitting = ko.observable(false);

        self.isValid = function () {
            // Provided for compatibility with custom form* bindings. Not using ko.validation
            return true;
        };

        self.submit = function () {
            self.notice("<i class='fa fa-spinner'></i>&nbsp;Attempting  login ...");

            // Force the observables to bind "NOW!" this works around the fact that FF's form auto fill doesn't trigger
            // the events that would cause the bind. See:
            // https://github.com/knockout/knockout/issues/648
            // https://bugzilla.mozilla.org/show_bug.cgi?id=87943
            forceRebind();

            if (self.username() !== "" && self.password() !== "") {
                self.isSubmitting(true);
                $.post(baseUrl + "j_spring_security_check", {
                        "j_username": self.username(),
                        "j_password": self.password()
                    })
                    .done(function () {
                        // Status 2xx
                        self.notice("<span class='text-success'>Success</span>");
                        refresh();
                    })
                    .fail(function (xhr) {
                        if (xhr.status === 500) {
                            self.notice("<span class='text-danger'>Server Error!</span>");
                        } else {
                            // Status Unauthorized 401 - Display authentication error message to user: eg Bad Credentials
                            self.notice("<span class='text-danger'>" + xhr.responseText + "</span>");
                        }
                    })
                    .always(function () {
                        self.isSubmitting(false);
                    });
            } else {
                self.notice("<span class='text-warning'>Username &amp; password required!</span>");
            }
        };
    };
});
