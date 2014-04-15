/* foo.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define(["ko", "jquery", "app/SingleFieldFormViewModel"], function (ko, $, SingleFieldFormViewModel) {
    "use strict";

    return function (initialData, basePath) {
        var self = this;
        self.RExecutableViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(basePath, "misc/rpath", initialData.rPath, { required: true }));
        self.ModelDurationViewModel = ko.validatedObservable(
            new SingleFieldFormViewModel(basePath, "misc/runduration", initialData.runDuration, { required: true, number: true }));
    };
});