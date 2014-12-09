/*
 */
define([
    "ko",
    "shared/app/BaseFormViewModel"
], function (ko, BaseFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        BaseFormViewModel.call(self, false, true, baseUrl, "account/reset/request", {
            success: "A message has been sent to you by email with instructions on how to reset your password."
        }, true, true);

        self.email = ko.observable("").extend({ required: true, email: true, maxLength: 320 });

        self.buildSubmissionData = function () {
            return {
                email: self.email()
            };
        };
    };
});
