/* foo.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define(["ko", "jquery"], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        self.username = ko.observable().extend({ required: true,  usernameComplexity: true });
        self.password = ko.observable().extend({ required: true, passwordComplexity: true });
        self.passwordConfirmation = ko.observable().extend({ required: true, passwordComplexity: true, areSame: self.password });
        self.saving = ko.observable(false);
        self.notices = ko.observableArray();
        self.submit = function () {
            self.notices.removeAll();
            if (self.isValid()) {
                self.saving(true);
                $.post(baseUrl + "auth", { username: self.username(), password: self.password(), passwordConfirmation: self.passwordConfirmation() })
                    .done(function () { self.notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { self.notices.push({ 'message': "Authentication details could not be saved.", 'priority': 'warning'}); })
                    .always(function () { self.saving(false); });
            } else {
                self.notices.push({ message: "All field must be valid before saving.", priority: 'warning'});
            }
        };
    };
});
