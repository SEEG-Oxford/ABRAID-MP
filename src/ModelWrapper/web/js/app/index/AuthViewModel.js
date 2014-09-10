/* An AMD defining the AuthViewModel to hold the state of the authorisation view.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "shared/app/BaseFormViewModel"
], function (ko, $, BaseFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        BaseFormViewModel.call(self, false, false, baseUrl, "auth");

        self.username = ko.observable().extend({ required: true,  usernameComplexity: true });
        self.password = ko.observable().extend({ required: true, passwordComplexity: true });
        self.passwordConfirmation = ko.observable()
            .extend({ required: true, passwordComplexity: true, areSame: self.password });

        self.buildSubmissionData = function () {
            return {
                username: self.username(),
                password: self.password(),
                passwordConfirmation: self.passwordConfirmation()
            };
        };
    };
});
