/**
 * An AMD used to display the (up to) 5 occurrences defining the selected admin unit's disease extent class.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-selected' - published by MapView.
 * -- 'layers-changed'      - published by SelectedLayerViewModel.
 */
define([
    "ko",
    "jquery",
    "underscore"
], function (ko, $, _) {
    "use strict";

    return function (baseUrl, loggedIn) {
        var self = this;
        var DISEASE_EXTENT = "disease extent";

        self.count = ko.observable(0);
        self.occurrences = ko.observableArray([]);
        self.showOccurrences = ko.observable(true);

        self.toggle = function () {
            self.showOccurrences(!self.showOccurrences());
        };

        var ajax;
        var diseaseGroupId;

        var getUrl = function (adminUnitId) {
            var adminUnits = loggedIn ? ("diseases/" + diseaseGroupId + "/adminunits/") : "defaultadminunits/";
            return baseUrl + "datavalidation/" + adminUnits + adminUnitId + "/occurrences";
        };

        var clear = function () {
            self.occurrences([]);
            self.count(0);
        };

        ko.postbox.subscribe("admin-unit-selected", function (adminUnit) {
            clear();
            if (adminUnit && adminUnit.count > 0) {
                self.count(adminUnit.count);
                if (ajax) {
                    ajax.abort();
                }
                ajax = $.getJSON(
                    getUrl(adminUnit.id))
                    .done(function (data) {
                        var properties = _(data.features).pluck("properties");
                        var sortedProperties = _(properties).sortBy("occurrenceDate").reverse();
                        self.occurrences(sortedProperties);
                    });
            }
        });

        ko.postbox.subscribe("layers-changed", function (layer) {
            diseaseGroupId = (layer.type === DISEASE_EXTENT) ? layer.diseaseId : undefined;
            clear();
        });
    };
});
