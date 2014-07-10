/* An AMD defining the view-model for the list of disease groups.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (baseUrl, initialData) {
        var self = this;

        self.diseases = ko.observableArray(initialData);
        self.selectedDisease = ko.observable(self.diseases()[0]);
    };
});
