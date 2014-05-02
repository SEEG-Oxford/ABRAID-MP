/**
 * An AMD defining the SelectedAdminUnitModel to hold the state of the selected administrative region on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-selected' - published by MapView.
 * - Events published:
 * -- none
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.selectedAdminUnit = ko.observable(null).subscribeTo("admin-unit-selected"); // Published by MapView
        self.hasSelectedAdminUnit = ko.computed(function () {
            return self.selectedAdminUnit() !== null;
        });
    };
});
