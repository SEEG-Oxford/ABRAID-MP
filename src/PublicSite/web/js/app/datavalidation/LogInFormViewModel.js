/* View model for the login form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "shared/app/BaseFormViewModel"
], function (ko, $, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, refresh, forceRebind) {
        var self = this;
        BaseFormViewModel.call(self, false, false, baseUrl, "j_spring_security_check");

        // Field state
        self.username = ko.observable("");
        self.password = ko.observable("");

        self.message = ko.computed(function () {
            if (self.isSubmitting()) {
                return "<i class='fa fa-spinner'></i>&nbsp;Attempting  login ...";
            }

            if (self.notices().length !== 0) {
                return "<span class='text-" + self.notices()[0].priority + "'>" + self.notices()[0].message + "</span>";
            }

            return "Log in via ABRAID account";
        }, self);

        // Actions
        self.buildSubmissionData = function () {
            return {
                "j_username": self.username(),
                "j_password": self.password()
            };
        };

        self.successHandler = function () {
            self.pushNotice("Success.", "success");
            refresh();
        };

        self.failureHandler = function (xhr) {
            self.pushNotice(xhr.responseText, "danger");
        };

        var baseSubmit = self.submit;
        self.submit = function () {
            // Force the observables to bind "NOW!" this works around the fact that FF's form auto fill doesn't trigger
            // the events that would cause the bind. See:
            // https://github.com/knockout/knockout/issues/648
            // https://bugzilla.mozilla.org/show_bug.cgi?id=87943
            forceRebind();

            if (self.username() !== "" && self.password() !== "") {
                baseSubmit();
            } else {
                self.notices.removeAll();
                self.pushNotice("Username &amp; password required!", "warning");
            }
        };
    };
});
