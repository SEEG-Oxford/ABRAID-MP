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
    "underscore",
    "jquery"
], function (ko, _, $) {
    "use strict";

    return function (baseUrl, alert, counter) {
        var self = this;

        ko.postbox.subscribe("admin-units-to-be-reviewed", function (event) {
            self.adminUnits(_(event.data).sortBy(function (unit) { return unit.properties.name; }));
        });

        var diseaseId = null;
        ko.postbox.subscribe("layers-changed", function (value) {
            diseaseId = value.diseaseId;
        });

        function removefromSelfAdminUnits(gaulCode) {
            self.adminUnits(_(self.adminUnits()).filter(function (f) { return f.id !== gaulCode; }));
        }

        self.counter = counter;
        self.adminUnits = ko.observable();
        self.selectedAdminUnit = ko.observable(null).subscribeTo("admin-unit-selected");
        self.hasSelectedAdminUnit = ko.computed(function () {
            return self.selectedAdminUnit() !== null;
        });
        self.submitReview = function (review) {
            var gaulCode = self.selectedAdminUnit().id;
            var url = baseUrl + "datavalidation/diseases/" + diseaseId + "/adminunits/" + gaulCode + "/validate";
            $.post(url, { review: review })
                .done(function () {
                    // Status 2xx
                    // Display a success alert, publish the event (so that the counter is incremented)
                    self.selectedAdminUnit(null);
                    removefromSelfAdminUnits(gaulCode);
                    ko.postbox.publish("admin-unit-reviewed", gaulCode);
                    $("#submitReviewSuccess").fadeIn(1000).delay(5000).fadeOut();
                })
                .fail(function () {
                    alert("Something went wrong. Please try again.");
                });
        };
    };
});
