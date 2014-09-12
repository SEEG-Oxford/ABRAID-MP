/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFileFormViewModel"
], function (ko, BaseFileFormViewModel) {
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
    };
});
