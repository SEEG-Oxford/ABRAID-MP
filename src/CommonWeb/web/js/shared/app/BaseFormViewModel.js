/* A base view model to provide a common implementation of knockout client-side form behaviour.
 * Use "call" to apply. E.g. BaseFormViewModel.call(self, args..)
 * Copyright (c) 2014 University of Oxford.
 */
define([
    "knockout",
    "jquery",
    "underscore"
], function (ko, $, _) {
    "use strict";

    return function (sendJson, receiveJson, baseUrl, targetUrl, messages,
                     excludeGenericFailureMessage, preventRepeatSubmissions) {
        var self = this;

        excludeGenericFailureMessage = excludeGenericFailureMessage || false;
        preventRepeatSubmissions = preventRepeatSubmissions || false;
        messages = messages || {};

        // It is assumed that self.isValid will be overridden by the ko.validation mixin, this is provided as a
        // fallback for the form* custom bindings when ko.validation isn't used.
        self.isValid = true;

        self.isSubmitting = ko.observable(false);
        self.notices = ko.observableArray([]);

        self.allowSubmit = ko.observable(true).extend({ areSame: true });

        // This function should be overridden in the concrete viewmodels
        self.buildSubmissionData = function () {
            return {};
        };

        self.buildSubmissionUrl = function () {
            return baseUrl + targetUrl;
        };

        self.buildAjaxArgs = function () {
            var args = {};

            args.method = "POST";
            args.url = self.buildSubmissionUrl();

            if (sendJson) {
                args.data = JSON.stringify(self.buildSubmissionData());
                args.contentType = "application/json";
            } else {
                args.data = self.buildSubmissionData();
            }

            return args;
        };

        var processResponse = function (xhr, priority) {
            if (xhr.responseText && xhr.responseText.length !== 0) {
                if (receiveJson) {
                    var response = JSON.parse(xhr.responseText);
                    if (typeof response === "string") {
                        self.pushNotice(response, priority);
                    } else {
                        self.pushNotices(response, priority);
                    }
                } else {
                    self.pushNotice(xhr.responseText, priority);
                }
            }
        };

        self.buildNotice = function (message, priority) {
            return { "message": message, "priority": priority };
        };

        self.pushNotice = function (message, priority) {
            self.notices.push(self.buildNotice(message, priority));
        };

        self.pushNotices = function (messages, priority) {
            _(messages).each(function (message) {self.pushNotice(message, priority); });
        };

        self.successHandler = function (data, textStatus, xhr) {
            if (preventRepeatSubmissions) {
                self.allowSubmit(false);
            }
            
            self.pushNotice(messages.success || "Form saved successfully.", "success");
            try {
                processResponse(xhr, "success");
            } catch (e) {
                // Ignore failed json parse
            }
        };

        self.failureHandler = function (xhr) {
            if (xhr.status === 500) {
                self.pushNotice(messages.error || "Server error.", "warning");
            } else if (xhr.status === 401) {
                self.pushNotice(messages.auth || "Authentication error.", "warning");
            } else {
                if (!excludeGenericFailureMessage) {
                    self.pushNotice(messages.fail || "Failed to save form.", "warning");
                }

                try {
                    processResponse(xhr, "warning");
                } catch (e) {
                    if (excludeGenericFailureMessage) {
                        // Make sure that there is an error message.
                        // Even if the json fails to parse (eg 404) and the subclass says no generic messages
                        self.pushNotice(messages.fail || "Failed to save form.", "warning");
                    }
                }
            }
        };

        self.alwaysHandler = function () {};

        self.submit = function () {
            self.notices.removeAll();
            self.isSubmitting(true);

            $.ajax(self.buildAjaxArgs())
                .done(self.successHandler)
                .fail(self.failureHandler)
                .always(function (xhr) {
                    self.alwaysHandler(xhr);
                    self.isSubmitting(false);
                });
        };
    };
});
