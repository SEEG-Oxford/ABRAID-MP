/*
 */
define([
    "ko",
    "shared/app/BaseFormViewModel"
], function (ko, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, id, key) {
        var self = this;

        BaseFormViewModel.call(self, false, true, baseUrl, "account/reset/process", {
            success: "Password updated successfully"
        }, true, true);

        self.newPassword = ko.observable("")
            .extend({ required: true, passwordComplexity: true });
        self.confirmPassword = ko.observable("")
            .extend({ required: true, passwordComplexity: true, areSame: self.newPassword });


        self.buildSubmissionData = function () {
            return {
                id: id,
                key: key,
                newPassword: self.newPassword(),
                confirmPassword: self.confirmPassword()
            };
        };
    };
});
