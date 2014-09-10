/* View model for the password change form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFormViewModel"
], function (ko, BaseFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        BaseFormViewModel.call(self, false, true, baseUrl, "/account/password");

        self.oldPassword = ko.observable("").extend({ required: true });
        self.newPassword = ko.observable("")
            .extend({ required: true, passwordComplexity: true });
        self.confirmPassword = ko.observable("")
            .extend({ required: true, passwordComplexity: true, areSame: self.newPassword });

        self.buildSubmissionData = function () {
            return {
                oldPassword: self.oldPassword(),
                newPassword: self.newPassword(),
                confirmPassword: self.confirmPassword()
            };
        };
    };
});
