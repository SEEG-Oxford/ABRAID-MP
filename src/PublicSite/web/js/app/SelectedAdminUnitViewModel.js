/**
 * An AMD defining the SelectedAdminUnitModel to hold the state of the selected administrative region on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-selected' - published by MapView.
 * -- 'layers-changed' - published by SelectedLayerViewModel.
 * - Events published:
 * -- none
 */
define([
    "ko",
    "underscore"
], function (ko, _) {
    "use strict";

    return function () {
        var self = this;

        ko.postbox.subscribe("admin-units-to-be-reviewed", function (units) {
            self.adminUnits(_(units).sortBy(function (unit) { return unit.properties.name; }));
        });

        self.adminUnits = ko.observable();
        self.selectedAdminUnit = ko.observable(null).subscribeTo("admin-unit-selected");
        self.hasSelectedAdminUnit = ko.computed(function () {
            return self.selectedAdminUnit() !== null;
        });
    };
});
