/* An AMD defining the MiscViewModel to hold the state of misc configuration objects.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "shared/app/SingleFieldFormViewModel"
], function (ko, $, SingleFieldFormViewModel) {
    "use strict";

    return function (initialData, basePath) {
        var self = this;

        self.RExecutableViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(initialData.rPath, { required: true },
                                         false, false, basePath, "misc/rpath"));

        self.ModelDurationViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(initialData.runDuration, { required: true, number: true, min: 1000 },
                                         false, false, basePath, "misc/runduration"));

        self.CovariateDirectoryViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(initialData.covariateDirectory, { required: true },
                                         false, false, basePath, "misc/covariatedirectory"));
    };
});