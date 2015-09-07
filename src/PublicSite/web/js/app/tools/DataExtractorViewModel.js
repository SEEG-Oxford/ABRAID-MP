/* An AMD defining a view model for the data extractor tool.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "ko",
    "underscore",
    "jquery",
    "shared/app/BaseFormViewModel"
], function (ko, _, $, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, wmsUrl, runs, countries, wmsParameterFactory) {
        var self = this;
        BaseFormViewModel.call(self, false, false, baseUrl, "tools/location/precise", {}, true, false, "GET");

        self.buildSubmissionData = function () {
            return {
                lat: self.lat(),
                lng: self.lng(),
                run: self.run().name
            };
        };

        self.successHandler = function (data) {
            self.score((data === "-9999.0") ? "No data" : data);
        };
        self.score = ko.observable();
        self.diseases = _(runs).chain().pluck("disease").unique().sort().value();
        self.disease = ko.observable("dengue");
        self.runs = ko.computed(function () {
            return _(runs).chain().where({disease: self.disease()}).sortBy("date").reverse().value();
        });
        self.run = ko.observable(self.runs()[0]);
        self.countries = _(countries).sortBy("name");
        self.country = ko.observable(_(countries).findWhere({name: "Afghanistan"}));

        self.lat = ko.observable().extend({ required: true, number: true, min: -60, max: 85 });
        self.lng = ko.observable().extend({ required: true, number: true, min: -180, max: 180 });

        var getBox = function (extent, size, padding) {
            // Creates a padded version of the extent, which is aligned to pixel limits
            var width = extent.maxX - extent.minX;
            var height = extent.maxY - extent.minY;
            var widthInPixels = size;
            var heightInPixels = size;
            var ratio = Math.max(width, height) / (size - (padding * 2));
            var minX = extent.minX - (padding * ratio);
            var maxX = extent.maxX + (padding * ratio);
            var minY = extent.minY - (padding * ratio);
            var maxY = extent.maxY + (padding * ratio);
            if (width > height) {
                var chp = (maxY - minY) / ratio;
                heightInPixels = Math.ceil(chp);
                var dh = (heightInPixels - chp) * ratio;
                maxY = maxY + (dh / 2);
                minY = minY - (dh / 2);
            } else {
                var cwp = (maxX - minX) / ratio;
                widthInPixels = Math.ceil(cwp);
                var dw = (widthInPixels - cwp) * ratio;
                maxX = maxX + (dw / 2);
                minX = minX - (dw / 2);
            }
            return {
                bbox: minX + "," + minY + "," + maxX + "," + maxY,
                width: widthInPixels,
                height: heightInPixels
            };
        };

        self.mode = ko.observable("precise");

        self.pngUrl = ko.computed(function () {
            var country = self.country();
            var run = self.run();
            var box = getBox(country, 600, 50);
            return wmsUrl + "?" + $.param(
                wmsParameterFactory.createLayerParametersForCroppedDownload(run.name, country.gaulCode, box)
            );
        });

        self.tifUrl = ko.computed(function () {
            return baseUrl + "tools/location/adminUnit?" + $.param({
                gaul: self.country().gaulCode,
                run: self.run().name
            });
        });
    };
});
