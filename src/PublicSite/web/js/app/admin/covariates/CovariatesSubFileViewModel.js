/* An AMD defining the Covariates Sub File view model, a vm to back one row in the covariates sub file tables.
 * Copyright (c) 2015 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (parentViewModel, parentSubFile) {
        var self = this;
        self.path = "./" + parentSubFile.path;
        self.qualifier = ko.observable(parentSubFile.qualifier).extend({ required: true });
        self.qualifier.subscribe(function (value) {
            parentSubFile.qualifier = value;
            parentViewModel.hasUnsavedChanges(true);
        });
    };
});
