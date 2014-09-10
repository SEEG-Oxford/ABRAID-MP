/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
/* global FormData:false */
define([
    "ko",
    "app/covariates/CovariatesListRowViewModel",
    "shared/app/BaseFormViewModel",
    "jquery",
    "jquery.iframe-transport"
], function (ko, CovariatesListRowViewModel, BaseFormViewModel, $) {
    "use strict";

    return function (baseUrl, useFormData) {
        var self = this;

        self.name = ko.observableArray("").extend({ required: true });
        self.subdirectory = ko.observable("./").extend({ required: true });
        self.file = ko.observable("").extend({ required: true });
        self.useFormData = useFormData;

        BaseFormViewModel.call(self, false, true, baseUrl, "covariates/add");
        self.buildSubmissionData = function () {
            if (self.useFormData) {
                var data = new FormData();
                data.append("name", self.name());
                data.append("subdirectory", self.subdirectory());
                data.append("file", self.file());
                return data;
            } else {
                return {
                    name: self.name(),
                    subdirectory: self.subdirectory()
                };
            }
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
        self.successHandler = function (data, textStatus, xhr) {
            if (data === "SUCCESS") {
                baseSuccessHandler(data, data, { responseText : data });
            } else {
                self.failureHandler({ responseText : data });
            }

        };
    };
});
