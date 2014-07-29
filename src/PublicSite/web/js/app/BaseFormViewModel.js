/*
 * Copyright (c) 2014 University of Oxford.
 */
define([
    "knockout",
    "jquery",
    "underscore"
], function (ko, $, _) {
    "use strict";

    return function (baseUrl, targetUrl, sendJson, receiveJson, messages) {
        var self = this;

        // It is assumed that self.isValid will be overridden by the ko.validation mixin, this is provided as a
        // fallback for the form* custom bindings when ko.validation isn't used.
        self.isValid = true;

        self.isSubmitting = ko.observable(false);
        self.notices = ko.observableArray([]);

        // This function should be overridden in the concrete viewmodels
        self.buildSubmissionData = function () {
            return {};
        };

        var buildAjaxArgs = function () {
            var args = {};

            args.method = "POST";
            args.url = baseUrl + targetUrl;

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
                    self.pushNotices(JSON.parse(xhr.responseText), priority);
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
            self.pushNotice(messages.success || "Form saved successfully.", "success");
            processResponse(xhr, "success");
        };

        self.failureHandler = function (xhr) {
            self.pushNotice(messages.fail || "Failed to save form.", "warning");
            if (xhr.status === 500) {
                self.pushNotice(messages.success || "Server error.", "warning");
            } else if (xhr.status === 401) {
                self.pushNotice(messages.success || "Authentication error.", "warning");
            } else {
                processResponse(xhr, "warning");
            }
        };

        self.alwaysHandler = function () {};

        self.submit = function () {
            self.notices.removeAll();
            self.isSubmitting(true);

            $.ajax(buildAjaxArgs())
                .done(self.successHandler)
                .fail(self.failureHandler)
                .always(function (xhr) {
                    self.alwaysHandler(xhr);
                    self.isSubmitting(false);
                });
        };
    };
});
