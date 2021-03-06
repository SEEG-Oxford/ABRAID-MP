/* An AMD defining the Covariates List Row, a vm to back one row in the covariates table.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/admin/covariates/CovariatesSubFileViewModel"
], function (ko, _, CovariatesSubFileViewModel) {
    "use strict";

    return function (parentViewModel, parentFile, activeDiseaseId) {
        var self = this;
        self.name = ko.observable(parentFile.name).extend({ required: true });
        self.name.subscribe(function (value) {
            parentFile.name = value;
            parentViewModel.hasUnsavedChanges(true);
        });
        self.hide = ko.observable(parentFile.hide);
        self.hide.subscribe(function (value) {
            parentFile.hide = value;
            parentViewModel.hasUnsavedChanges(true);
            parentViewModel.entries.valueHasMutated(); //Force view refresh
        });
        self.discrete = parentFile.discrete; // Read only
        self.mouseOver = ko.observable(false);
        self.count = parentFile.subFiles ? parentFile.subFiles.length : 0;
        self.files = _(parentFile.subFiles).map(function (subFile) {
            return new CovariatesSubFileViewModel(parentViewModel, subFile);
        });

        self.info = ko.observable(parentFile.info);
        self.info.subscribe(function (value) {
            parentFile.info = value;
            parentViewModel.hasUnsavedChanges(true);
        });
        self.usageCount = ko.observable(parentFile.enabled.length);
        self.state = ko.observable(_(parentFile.enabled).contains(activeDiseaseId));
        self.state.subscribe(function (value) {
            if (value) {
                parentFile.enabled.push(activeDiseaseId);
                self.usageCount(self.usageCount() + 1);
            } else {
                // Note: using underscore's indexOf instead of native indexOf because native
                //       indexOf is flakey in old IEs.
                parentFile.enabled.splice(_(parentFile.enabled).indexOf(activeDiseaseId), 1);
                self.usageCount(self.usageCount() - 1);
            }
            parentViewModel.hasUnsavedChanges(true);
        });
    };
});
