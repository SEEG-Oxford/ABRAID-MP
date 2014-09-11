/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFileFormViewModel",
    "jquery",
    "jquery.iframe-transport"
], function (ko, BaseFileFormViewModel, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        self.name = ko.observableArray("").extend({ required: true });
        self.subdirectory = ko.observable("./").extend({ required: true });

        BaseFileFormViewModel.call(self, baseUrl, "covariates/add");

        self.buildSubmissionData = function () {
            return {
                name: self.name(),
                subdirectory: self.subdirectory()
            };
        };

        var baseBuildAjaxArgs = self.buildAjaxArgs;
        self.buildAjaxArgs = function () {
            var args = baseBuildAjaxArgs();
            args.processData = false; // prevent jquery from turning the FormData in to key/value pairs
            args.contentType = null; // prevent jquery adding
            if (!self.useFormData) {
                args.iframe = true;
                args.files = $(":file[name=file]").filter(function (i, el) { return el.files[0] === self.file(); });
            }
            return args;
        };

        var baseSuccessHandler = self.successHandler;
        self.successHandler = function (data) {
            if (data === "SUCCESS") {
                baseSuccessHandler(data, data, { responseText : data });
            } else {
                self.failureHandler({ responseText : data });
            }

        };
    };
});
