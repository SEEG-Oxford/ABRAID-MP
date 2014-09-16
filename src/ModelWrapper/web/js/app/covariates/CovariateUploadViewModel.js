/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFileFormViewModel"
], function (ko, BaseFileFormViewModel) {
    "use strict";

    return function (baseUrl, covariatesListViewModel, refresh) {
        var self = this;

        BaseFileFormViewModel.call(self, baseUrl, "covariates/add");
        self.buildSubmissionData = function () {
            return {
                name: self.name(),
                subdirectory: self.subdirectory()
            };
        };
        self.postSuccessAction = refresh;

        self.name = ko.observable("")
            .extend({ required: true, isUniqueProperty: {
                array: covariatesListViewModel.visibleEntries,
                property: "name"
            }});

        self.subdirectory = ko.observable("./")
            .extend({ required: true, startWith: "./", endWith: "/", notContain: ["/../", "/./", "//", "\\"] });

        self.unsavedWarning = ko.computed(function () {
            return covariatesListViewModel.hasUnsavedChanges();
        }).extend({ equal: false });

        self.uploadPath = ko.computed(function () {
            if (self.file() && self.subdirectory()) {
                var path = self.subdirectory() + self.file().name;
                if (path.indexOf("./") === 0) {
                    path = path.substring(2);
                }
                return path;
            } else {
                return "";
            }
        }).extend({ isUniqueProperty: { array: covariatesListViewModel.entries, property: "path" }});

    };
});
