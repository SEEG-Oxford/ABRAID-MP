/*
 * AMD to represent the current value to be displayed on the counter.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- incrementEventName - the name of the event to which the view model should subscribe to instigate count increment.
 * - Practically this is either:
 * -- 'occurrence-reviewed' - published by SelectedPointViewModel.
 * -- 'admin-unit-reviewed' - published by SelectedAdminUnitViewModel.
 * - Events published:
 * -- none
 */
define(["ko"], function (ko) {
    "use strict";

    return function (initialValue, incrementEventName) {
        var self = this;
        self.count = ko.observable(initialValue);

        ko.postbox.subscribe(incrementEventName, function () {
            self.count(self.count() + 1);
        });
    };
});
