/* A factory for generating the standard ABRAID WMS request/layer options for a given layer name.
 * Copyright (c) 2014 University of Oxford
 */
define(["underscore", "L", "moment"], function (_, L, moment) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        var urls = {
            occurrences: baseUrl + "atlas/data/modelrun/{{run_id}}/geojson"
        };

        var layerBuilders = {
            occurrences: function (featureCollection, layer) {
                // Sort features
                featureCollection.features = _(featureCollection.features)
                    .sortBy(function (f) {return f.properties.occurrenceDate; });

                // Build markers
                var runDate = moment(layer.run.date);
                var markerFactory = function (feature) {
                    var latlng = L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0]);
                    return L.circleMarker(latlng, {
                        stroke: false,
                        fillColor: (function (date) {
                            var occurrenceDate = moment(date);
                            var ageInMonths = runDate.diff(occurrenceDate, "months");
                            if (ageInMonths < 12) {
                                return "#0F2540";
                            } else if (ageInMonths < 34) {
                                return "#4B6584";
                            } else if (ageInMonths < 68) {
                                return "#87A5C8";
                            } else {
                                return "#A6C5EA";
                            }
                        })(Date.parse(feature.properties.occurrenceDate)),
                        fillOpacity: 1,
                        radius: 3,
                        clickable: false
                    });
                };
                var markers = _(featureCollection.features).map(markerFactory);
                var leafletLayer = L.layerGroup(markers);
                return leafletLayer;
            }
        };

        self.showGeoJsonLayer = function (layer) {
            return _(urls).has(layer.type);
        };

        self.buildGeoJsonUrl = function (layer) {
            return urls[layer.type].replace("{{run_id}}", layer.run.id);
        };

        self.buildGeoJsonLayer = function (featureCollection, layer) {
            return layerBuilders[layer.type](featureCollection, layer);
        };
    };
});
