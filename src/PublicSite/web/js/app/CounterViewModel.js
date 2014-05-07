/*
 * AMD to represent the current value to be displayed on the counter.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'point-reviewed' - published by SelectedPointViewModel.
 * - Events published:
 * -- none
 */
define(["ko"], function (ko) {
    "use strict";

    return function (initialValue) {
        var self = this;
        self.count = ko.observable(initialValue);

        ko.postbox.subscribe("point-reviewed", function () {
            self.count(self.count() + 1);
        });
    };
});
