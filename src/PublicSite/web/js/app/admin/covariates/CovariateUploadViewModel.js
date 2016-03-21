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

        BaseFileFormViewModel.call(self, baseUrl, "admin/covariates/add");
        self.buildSubmissionData = function () {
            return {
                name: self.name(),
                discrete:  self.discrete(),
                subdirectory: self.subdirectory(),
                qualifier: self.qualifier(),
                parentId: self.parent() ? self.parent().id : -1
            };
        };
        self.postSuccessAction = refresh;

        self.discrete = ko.observable(false);

        self.parentList = covariatesListViewModel.entries;
        self.parent = ko.observable(undefined);
        self.parent.subscribe(function (value) {
            self.name(value ? "not-used" : "");
        });

        self.name = ko.observable("")
            .extend({ required: true, isUniqueProperty: {
                array: covariatesListViewModel.visibleEntries,
                property: "name"
            }});

        self.qualifier = ko.observable("").extend({ required: true });

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
