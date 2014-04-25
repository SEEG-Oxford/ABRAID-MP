/*
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define(["ko"], function (ko) {
    "use strict";

    return function (initialValue) {
        var self = this;
        self.count = ko.observable(initialValue);
        self.incrementCount = function () {
            self.count(self.count() + 1);
        };
    };
});
