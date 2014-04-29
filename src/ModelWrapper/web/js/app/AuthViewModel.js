/* An AMD defining the AuthViewModel to hold the state of the authorisation view.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        self.username = ko.observable().extend({ required: true,  usernameComplexity: true });
        self.password = ko.observable().extend({ required: true, passwordComplexity: true });
        self.passwordConfirmation = ko.observable()
            .extend({ required: true, passwordComplexity: true, areSame: self.password });
        self.saving = ko.observable(false);
        self.notices = ko.observableArray();

        var pushMessage = function (message, priority) {
            self.notices.push({ "message": message, "priority": priority});
        };

        self.submit = function () {
            self.notices.removeAll();
            if (self.isValid()) {
                self.saving(true);
                var postData = {
                    username: self.username(),
                    password: self.password(),
                    passwordConfirmation: self.passwordConfirmation()
                };

                $.post(baseUrl + "auth", postData)
                    .done(function () { pushMessage("Saved successfully.", "success"); })
                    .fail(function () { pushMessage("Authentication details could not be saved.", "warning"); })
                    .always(function () { self.saving(false); });
            } else {
                self.notices.push({ message: "All field must be valid before saving.", priority: "warning"});
            }
        };
    };
});
