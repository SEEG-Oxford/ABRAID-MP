/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/admin/covariates/CovariatesListRowViewModel",
    "shared/app/BaseFormViewModel",
    "shared/app/BaseTableViewModel"
], function (ko, CovariatesListRowViewModel, BaseFormViewModel, BaseTableViewModel) {
    "use strict";

    return function (baseUrl, initialValue) {
        var self = this;

        self.diseases = ko.observableArray(initialValue.diseases);
        self.selectedDisease = ko.observable(self.diseases()[0]);

        self.hasUnsavedChanges = ko.observable(false);

        BaseFormViewModel.call(self, true, true, baseUrl, "admin/covariates/config");

        BaseTableViewModel.call(self, initialValue.files, "path", false, [ "name", "path" ], function (file) {
            return new CovariatesListRowViewModel(self, file, self.selectedDisease().id);
        });

        self.buildSubmissionData = function () {
            return {
                files: self.entries()
            };
        };

        var baseSuccessHandler = self.successHandler;
        self.successHandler = function (data, textStatus, xhr) {
            baseSuccessHandler(data, textStatus, xhr);
            self.hasUnsavedChanges(false);
        };
    };
});
