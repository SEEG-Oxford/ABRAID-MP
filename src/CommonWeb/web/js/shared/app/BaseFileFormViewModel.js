/* A base view model to provide a common implementation of knockout client-side form behaviour when the form
 * contains a file.
 * Use "call" to apply. E.g. BaseFileFormViewModel.call(self, args..)
 * Copyright (c) 2014 University of Oxford.
 */
/* global window: false, FormData: false */
define([
    "ko",
    "underscore",
    "shared/app/BaseFormViewModel",
    "jquery",
    "jquery.iframe-transport"
], function (ko, _, BaseFormViewModel, $) {
    "use strict";

    return function (baseUrl, targetUrl, messages, excludeGenericFailureMessage) {
        var self = this;

        // Currently limited to one file, with name='file' on the <input>
        self.file = ko.observable().extend({ required: true });
        self.useFormData = (window.FormData !== undefined);

        BaseFormViewModel.call(self, false, true, baseUrl, targetUrl, messages, excludeGenericFailureMessage);

        var buildFormData = function (subclassData) {
            var data = new FormData();
            // Add the subclass data key value pairs to the FormData object
            _(subclassData).chain().pairs().map(function (kvp) {
                data.append(kvp[0], kvp[1]);
            });
            // Add the file to the FormData object
            data.append("file", self.file());
            return data;
        };

        var baseBuildAjaxArgs = self.buildAjaxArgs;
        self.buildAjaxArgs = function () {
            var args = baseBuildAjaxArgs();
            // Prevent jquery from turning the data into key/value pairs
            args.processData = false;
            // Prevent jquery from adding its default content type
            args.contentType = false;
            if (!self.useFormData) {
                // Set the transport mechanism to iframe
                args.iframe = true;
                // Add the file to args.files and replace File object with bound <input>
                args.files = $(":file[name=file]").filter(function (i, el) { return el.files[0] === self.file(); });
            } else {
                // Use FormData for the content
                args.data = buildFormData(args.data);
            }
            return args;
        };

        var baseFailureHandler = self.failureHandler;
        self.failureHandler = function (xhr) {
            try {
                // Emulate other (non-file) forms
                var data = JSON.parse(xhr.responseText);
                if (data.messages !== undefined) {
                    xhr.responseText = JSON.stringify(data.messages);
                }
            } catch (e) {
                // Ignore
            }

            baseFailureHandler(xhr);
        };

        self.postSuccessAction = function () {};

        var baseSuccessHandler = self.successHandler;
        self.successHandler = function (data) {
            // One limitation of the iframe transport mechanism is that it doesn't see the status code
            // and calls success for all cases. It also adds html surrounding tags to the response body
            if (typeof data === "string") {
                try {
                    data = JSON.parse($(data).text());
                } catch (e) {
                    self.failureHandler({});
                    return;
                }
            }

            var json = JSON.stringify(data.messages); // Emulate other (non-file) forms
            if (data.status === "SUCCESS") {
                baseSuccessHandler(json, json, { responseText : json });
                self.postSuccessAction();
            } else {
                baseFailureHandler({ responseText : json });
            }
        };
    };
});
