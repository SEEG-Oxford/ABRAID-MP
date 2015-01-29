/* AMD defining the view model for the atlas legend.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-type'
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;
        self.type = ko.observable().subscribeTo("active-atlas-type");

        var activeRun = ko.observable().subscribeTo("selected-run");
        self.startDate = ko.computed(function () {
            return (typeof activeRun() !== "undefined") ? activeRun().rangeStart : "???";
        });
        self.endDate = ko.computed(function () {
            return (typeof activeRun() !== "undefined") ? activeRun().rangeEnd : "???";
        });
    };
});
