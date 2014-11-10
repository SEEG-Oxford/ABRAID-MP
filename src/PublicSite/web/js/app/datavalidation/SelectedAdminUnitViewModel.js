/**
 * An AMD defining the SelectedAdminUnitModel to hold the state of the selected administrative region on the map.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-selected'        - published by MapView.
 * -- 'admin-units-to-be-reviewed' - published by MapView.
 * -- 'layers-changed'             - published by SelectedLayerViewModel.
 * - Events published:
 * -- 'admin-unit-reviewed'
 * -- 'admin-unit-selected' - as triggered by side panel table.
 */
define([
    "ko",
    "underscore",
    "jquery"
], function (ko, _, $) {
    "use strict";

    return function (baseUrl, alert, counter) {
        var self = this;

        var diseaseId = null;

        function removefromSelfAdminUnits(gaulCode) {
            self.adminUnits(_(self.adminUnits()).reject(function (f) { return f.id === gaulCode; }));
        }

        self.counter = counter;
        self.adminUnits = ko.observableArray();
        self.selectedAdminUnit = ko.observable(null).syncWith("admin-unit-selected");
        self.hasSelectedAdminUnit = ko.computed(function () {
            return self.selectedAdminUnit() !== null;
        }, self);

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

        ko.postbox.subscribe("admin-units-to-be-reviewed", function (event) {
            self.adminUnits(_(event.data).sortBy(function (adminUnit) { return adminUnit.name; }));
        });

        ko.postbox.subscribe("layers-changed", function (value) {
            diseaseId = value.diseaseId;
        });
    };
});
