/* An AMD defining the MiscViewModel to hold the state of misc configuration objects.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "app/index/SingleFieldFormViewModel"
], function (ko, $, SingleFieldFormViewModel) {
    "use strict";

    return function (initialData, basePath) {
        var self = this;

        self.RExecutableViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(basePath, "misc/rpath", initialData.rPath,
                { required: true }));

        self.ModelDurationViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(basePath, "misc/runduration", initialData.runDuration,
                { required: true, number: true, min: 1000 }));

        self.CovariateDirectoryViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(basePath, "misc/covariatedirectory", initialData.covariateDirectory,
                { required: true }));
    };
});