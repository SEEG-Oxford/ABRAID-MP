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
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        ko.postbox.subscribe("layers-changed", function (data) {
            if (data.type === "disease extent") {
                var url = baseUrl + "datavalidation/diseases/" + data.diseaseId + "/adminunits";
                $.getJSON(url, function (featureCollection) {
                    // Filter only those where the boolean property indicating it is in need of review is true
                    self.adminUnits(featureCollection.features);
                });
            }
        });

        self.adminUnits = ko.observableArray();
        self.selectedAdminUnit = ko.observable(null).subscribeTo("admin-unit-selected");
        self.hasSelectedAdminUnit = ko.computed(function () {
            return self.selectedAdminUnit() !== null;
        });
    };
});
