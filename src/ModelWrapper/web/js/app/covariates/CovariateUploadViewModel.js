/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
/* global FormData:false */
define([
    "ko",
    "app/covariates/CovariatesListRowViewModel",
    "shared/app/BaseFormViewModel"
], function (ko, CovariatesListRowViewModel, BaseFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        self.name = ko.observableArray("").extend({ required: true });
        self.subdirectory = ko.observable("./").extend({ required: true });
        self.file = ko.observable("").extend({ required: true });

        BaseFormViewModel.call(self, false, true, baseUrl, "covariates/add");
        self.buildSubmissionData = function () {
            var data = new FormData();
            data.append("name", self.name());
            data.append("subdirectory", self.subdirectory());
            data.append("file", self.file());
            return data;
        };

        var baseBuildAjaxArgs = self.buildAjaxArgs;
        self.buildAjaxArgs = function () {
            var args = baseBuildAjaxArgs();
            args.processData = false; // prevent jquery from turning the FormData in to key/value pairs
            args.contentType = null; // prevent jquery adding
            return args;
        };
    };
});
