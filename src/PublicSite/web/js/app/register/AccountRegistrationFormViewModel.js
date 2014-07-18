/* AMD to represent the data in the account registration page.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore", "jquery"], function (ko, _, $) {
    "use strict";

    return function (baseUrl, initialExpert, initialAlerts, captcha, redirectPage) {
        var self = this;

        // Function to convert the alerts into knockout.bootstrap notices.
        var buildNotices = function (alerts, priority) {
            return _(alerts).map(function (alert) {
                return { "message": alert, "priority": priority};
            });
        };

        // Field state
        self.email = ko.observable(initialExpert.email || "")
            .extend({ required: true, email: true });

        self.password = ko.observable(initialExpert.password || "")
            .extend({ required: true, passwordComplexity: true });

        self.passwordConfirmation = ko.observable(initialExpert.password || "")
            .extend({ required: true, passwordComplexity: true, areSame: self.password });

        // Meta state
        self.notices = ko.observableArray(buildNotices(initialAlerts || [], "warning"));
        self.isSubmitting = ko.observable(false);

        var buildSubmissionData = function () {
            return {
                email: self.email(),
                password: self.password(),
                passwordConfirmation: self.passwordConfirmation(),
                captchaChallenge: captcha.get_challenge(),
                captchaResponse: captcha.get_response()
            };
        };

        // Actions
        self.submit = function () {
            self.notices.removeAll();
            var captchaResponse = captcha.get_response();
            if (captchaResponse === undefined || captchaResponse === null || captchaResponse === "") {
                self.notices.push({ message: "Captcha is required.", priority: "warning"});
            } else if (!self.isValid()) {
                self.notices.push({ message: "Fields must be valid before saving.", priority: "warning"});
            } else {
                self.isSubmitting(true);
                $.ajax({
                    method: "POST",
                    url: baseUrl + "register/account",
                    data: JSON.stringify(buildSubmissionData()),
                    contentType : "application/json"
                })
                    .done(function () {
                        self.notices.push({
                            "message": "Account creation step 1/2 successfully completed.",
                            "priority": "success"
                        });
                        redirectPage(baseUrl + "/register/details");
                    })
                    .fail(function (xhr) {
                        var alerts = buildNotices(JSON.parse(xhr.responseText), "warning");
                        _(alerts).each(function (alert) { self.notices.push(alert); });
                        captcha.reload();
                    })
                    .always(function () { self.isSubmitting(false); });
            }
        };
    };
});
