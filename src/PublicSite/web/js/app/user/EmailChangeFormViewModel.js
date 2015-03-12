/* View model for the email change form.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFormViewModel"
], function (ko, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, currentEmail) {
        var self = this;

        BaseFormViewModel.call(self, false, true, baseUrl, "account/email", undefined, undefined, true);

        self.email = ko.observable(currentEmail).extend(
            { required: true,  email: true, maxLength: 320, emailChanged: currentEmail });
        self.password = ko.observable("").extend({ required: true });

        self.buildSubmissionData = function () {
            return {
                email: self.email(),
                password: self.password()
            };
        };
    };
});
