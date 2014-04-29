/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "L",
    "jquery",
    "ko",
    "underscore"
], function (L, $, ko, _) {
    "use strict";

    return function (baseUrl, wmsUrl, loggedIn) {
        // Initialise map at "map" div
        var map = L.map("map", {
            attributionControl: false,
            zoomControl: false,
            zoomsliderControl: true,
            maxBounds: [ [-89, -179], [89, 179] ],
            maxZoom: 7,
            minZoom: 3,
            animate: true
        }).fitWorld();

        // Add the simplified shapefile base layer with WMS GET request
        L.tileLayer.wms(wmsUrl, {
            layers: ["abraid:simplified_base_layer"],
            format: "image/png",
            reuseTiles: true
        }).addTo(map);

        // Global colour variables
        var defaultColour = "#c478a9";      // Lighter pink/red
        var highlightColour = "#9e1e71";    // The chosen pink/red compatible with colourblindness
        var strokeColour = "#ce8eb8";       // Darker pink/red

        // Define default style for unselected points
        var diseaseOccurrenceLayerStyle = {
            stroke: false,
            fill: true,
            color: strokeColour,
            fillColor: defaultColour,
            fillOpacity: 0.8,
            radius: 8
        };

        // Change a point's colour on roll-over
        function highlightFeature(e) {
            e.target.setStyle({
                stroke: true,
                color: highlightColour
            });
        }

        // Return to default colour
        function resetHighlight(e) {
            e.target.setStyle({
                stroke: false,
                color: defaultColour
            });
        }

        // Change the point's colour and size when clicked
        function selectFeature(marker) {
            // First, reset the style of all other points on layer, so only one point is animated as selected at a time
            resetLayerStyle();
            if (loggedIn) {
                marker.setStyle({
                    stroke: false,
                    fillColor: highlightColour,
                    radius: 13
                });
            } else {
                marker._icon.classList.add("marker-question-selected"); // jshint ignore:line
            }
        }

        // Define a circle, instead of the default leaflet marker, with listeners for mouse events
        function diseaseOccurrenceLayerPoint(feature, latlng) {
            var marker;
            if (loggedIn) {
                marker = L.circleMarker(latlng).on({
                    mouseover: highlightFeature,
                    mouseout: resetHighlight
                });
            } else {
                // Display a question mark marker
                marker = L.marker(latlng, { icon:
                    L.divIcon({
                        html: "<div><span>?</span></div>",
                        className: "marker-question",
                        iconAnchor: [10, 10]
                    })
                });
            }
            marker.on({
                click: function () {
                    ko.postbox.publish("point-selected", feature);
                    selectFeature(this);
                }
            });
            return marker;
        }

        // Define a cluster icon, so that nearby occurrences are grouped into one marker.
        // Text displays the number of points in the cluster. Styling in separate CSS file.
        function clusterLayerPoint(cluster) {
            return new L.DivIcon({
                html: "<div><span>" + cluster.getChildCount() + "</span></div>",
                className: "marker-cluster",
                iconSize: new L.Point(40, 40)
            });
        }

        // Map from the id of a feature to its marker layer, so the layer object can be used in removeReviewedPoint.
        // Also serves to keep track of the number of markers on the diseaseOccurrenceLayer (for the selected disease).
        var layerMap = {};
        // Define styling of the layer to which occurrence data (feature collection) is later added via AJAX request
        var diseaseOccurrenceLayer = L.geoJson([], {
            pointToLayer: diseaseOccurrenceLayerPoint,
            style: diseaseOccurrenceLayerStyle,
            onEachFeature: function (feature, layer) {
                layer.on("add", function () {
                    layerMap[feature.id] = layer;
                });
            }
        });

        // Add this geoJson layer to the styled markerClusterGroup layer, so that nearby occurrences are grouped
        var clusterLayer = L.markerClusterGroup({
            maxClusterRadius: 10,
            polygonOptions: {
                color: highlightColour,
                weight: 2,
                fillColor: defaultColour,
                dashArray: 3
            },
            spiderfyDistanceMultiplier: 1.5,
            iconCreateFunction: clusterLayerPoint
        });

        // Reset the style of all markers on diseaseOccurrenceLayer
        function resetLayerStyle() {
            if (loggedIn) {
                diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
            } else {
                Object.keys(layerMap).forEach(function (key) {
                    layerMap[key]._icon.classList.remove("marker-question-selected"); // jshint ignore:line
                });
            }
        }

        // Reset to default style when a point is unselected (by clicking anywhere else on the map)
        function resetSelectedPoint() {
            ko.postbox.publish("point-selected", null);
            resetLayerStyle();
        }

        map.on("click", resetSelectedPoint);
        clusterLayer.on("clusterclick", resetSelectedPoint);

        // Return the corresponding colour for the disease extent class of the admin unit
        function getColour(diseaseExtentClass) {
            switch (diseaseExtentClass) {
                case "PRESENCE":
                    return "#8e1b65";  // darkPink
                case "POSSIBLE_PRESENCE":
                    return "#c478a9";  // lightPink
                case "UNCERTAIN":
                    return "#ffffbf";  // yellow
                case "POSSIBLE_ABSENCE":
                    return "#b5caaa";  // lightGreen
                case "ABSENCE":
                    return "#769766";  // darkGreen
            }
        }

        function diseaseExtentLayerStyle(feature) {
            return {
                fillColor: getColour(feature.properties.diseaseExtentClass),
                fillOpacity: 0.7,
                weight: 2,
                opacity: 1,
                color: "#ffffff"
            };
        }

        // Define the geoJson layer, to which the disease extent data will be added via AJAX request
        var diseaseExtentLayer = L.geoJson([], {
            style: diseaseExtentLayerStyle
        });

        // Display the layer corresponding to the selected validation type (disease occurrences, or disease extent)
        function switchValidationTypeView(validationType) {
            if (validationType === "disease occurrences") {
                map.removeLayer(diseaseExtentLayer);
                clusterLayer.addLayer(diseaseOccurrenceLayer).addTo(map);
            } else {
                map.removeLayer(clusterLayer);
                diseaseExtentLayer.addTo(map);
            }
        }

        // Remove the layers from the map, and clear the record of markers on the layer.
        // The new markers are added to layerMap on addLayer thanks to onEachFeature.
        function clearDiseaseOccurrenceLayer() {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.clearLayers();
            layerMap = {};
        }

        function getDiseaseOccurrenceRequestUrl(diseaseId) {
            if (loggedIn) {
                return baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences";
            } else {
                return baseUrl + "static/defaultDiseaseOccurrences.json";
            }
        }

        // Add the new feature collection to the clustered layer, and zoom to its bounds
        function switchDiseaseOccurrenceLayer(diseaseId) {
            clearDiseaseOccurrenceLayer();
            var geoJsonRequestUrl = getDiseaseOccurrenceRequestUrl(diseaseId);
            $.getJSON(geoJsonRequestUrl, function (featureCollection) {
                if (featureCollection.features.length !== 0) {
                    ko.postbox.publish("no-features-to-review", false);
                    clusterLayer.addLayer(diseaseOccurrenceLayer.addData(featureCollection));
                    map.fitBounds(diseaseOccurrenceLayer.getBounds());
                } else {
                    ko.postbox.publish("no-features-to-review", true);
                    map.fitWorld();
                }
            });
        }

        // Display the admin units, and disease extent class, for the selected validator disease group.
        function switchDiseaseExtentLayer(diseaseId) {
            diseaseExtentLayer.clearLayers();
            var geoJsonRequestUrl = baseUrl + "datavalidation/diseases/" + diseaseId + "/adminunits";
            $.getJSON(geoJsonRequestUrl, function (featureCollection) {
                if (featureCollection.features.length !== 0) {
                    ko.postbox.publish("no-features-to-review", false);
                    diseaseExtentLayer.addData(featureCollection);
                    map.fitBounds(diseaseExtentLayer.getBounds());
                    //TODO: Fit bounds to the polygons with PRESENCE and POSSIBLE_PRESENCE class, and their neighbours
                } else {
                    ko.postbox.publish("no-features-to-review", true);
                    map.fitWorld();
                }
            });
        }

        ko.postbox.subscribe("layers-changed", function (layers) {
            switchValidationTypeView(layers.type);
            if (layers.type === "disease occurrences") {
                switchDiseaseOccurrenceLayer(layers.diseaseSet.id);
            } else {
                switchDiseaseExtentLayer(layers.disease.id);
            }
        });

        // Remove marker from map
        ko.postbox.subscribe("point-reviewed", function (id) {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.removeLayer(layerMap[id]);
            delete layerMap[id];
            if (_(layerMap).isEmpty()) {
                ko.postbox.publish("no-features-to-review", true);
            } else {
                clusterLayer.addLayer(diseaseOccurrenceLayer);
            }
        });
    };
});
