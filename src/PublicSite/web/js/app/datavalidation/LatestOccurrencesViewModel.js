/**
 * An AMD used to display the (up to) 5 occurrences defining the selected admin unit's disease extent class.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-selected'        - published by MapView.
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.count = ko.observable(0);
        self.occurrences = ko.observableArray([]);
        self.showOccurrences = ko.observable(true);

        self.toggle = function () {
            self.showOccurrences(!self.showOccurrences());
        };

        ko.postbox.subscribe("admin-unit-selected", function (adminUnit) {
            if (adminUnit) {
                self.count(adminUnit.count);
                self.occurrences(adminUnit.occurrences);
            } else {
                self.count(0);
                self.occurrences([]);
            }
        });
    };
});
