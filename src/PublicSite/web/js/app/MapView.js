/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-reviewed' - published by SelectedAdminUnitViewModel
 * -- 'admin-unit-selected' - published by MapView
 * -- 'layers-changed' - published by SelectedLayerViewModel
 * -- 'occurrence-reviewed' - published by SelectedPointViewModel
 * - Events published:
 * -- 'admin-unit-selected'
 * -- 'admin-units-to-be-reviewed'
 * -- 'point-selected'
 * -- 'no-features-to-review' if
 * --- the FeatureCollection data is empty
 * --- the last point is remove from its layer
 */
define([
    "L",
    "jquery",
    "ko",
    "underscore"
], function (L, $, ko, _) {
    "use strict";

    return function (baseUrl, wmsUrl, loggedIn) {

        /** MAP AND BASE LAYER */

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

        /** DISEASE OCCURRENCE LAYER */

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
        function highlightPoint(e) {
            e.target.setStyle({
                stroke: true,
                color: highlightColour
            });
        }

        // Return to default colour
        function resetHighlightedPoint(e) {
            e.target.setStyle({
                stroke: false,
                color: defaultColour
            });
        }

        // Change the point's colour and size when clicked
        function selectPoint(marker) {
            // First, reset the style of all other points on layer, so only one point is animated as selected at a time
            resetDiseaseOccurrenceLayerStyle();
            if (loggedIn) {
                marker.setStyle({
                    stroke: false,
                    fillColor: highlightColour,
                    radius: 13
                });
            } else {
                $(marker._icon).addClass("marker-question-selected"); // jshint ignore:line
            }
        }

        // Define a circle, instead of the default leaflet marker, with listeners for mouse events
        function diseaseOccurrenceLayerPoint(feature, latlng) {
            var marker;
            if (loggedIn) {
                marker = L.circleMarker(latlng).on({
                    mouseover: highlightPoint,
                    mouseout: resetHighlightedPoint
                });
            } else {
                // Display a question mark marker
                marker = L.marker(latlng, { icon:
                    L.divIcon({
                        html: "?",
                        className: "marker-question"
                    })
                });
            }
            marker.on("click", function () {
                ko.postbox.publish("point-selected", feature);
                selectPoint(this);
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
                layerMap[feature.id] = layer;
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
        function resetDiseaseOccurrenceLayerStyle() {
            if (loggedIn) {
                diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
            } else {
                $(".marker-question-selected").removeClass("marker-question-selected");
            }
        }

        // Remove the layers from the map, and clear the record of markers on the layer.
        // The new markers are added to layerMap on addLayer thanks to onEachFeature.
        function clearDiseaseOccurrenceLayer() {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.clearLayers();
            layerMap = {};
        }

        function getDiseaseOccurrencesRequestUrl(diseaseId) {
            if (loggedIn) {
                return baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences";
            } else {
                return baseUrl + "static/defaultOccurrences.json";
            }
        }

        // Add the new feature collection to the clustered layer, and zoom to its bounds
        function switchDiseaseOccurrenceLayer(diseaseId) {
            clearDiseaseOccurrenceLayer();
            $.getJSON(getDiseaseOccurrencesRequestUrl(diseaseId), function (featureCollection) {
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

        // Remove the feature's marker layer from the disease occurrence layer, and delete record of the feature.
        function removeMarkerFromDiseaseOccurrenceLayer(id) {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.removeLayer(layerMap[id]);
            delete layerMap[id];
            if (_(layerMap).isEmpty()) {
                ko.postbox.publish("no-features-to-review", true);
            } else {
                clusterLayer.addLayer(diseaseOccurrenceLayer);
            }
        }

        /** DISEASE EXTENT LAYER */

        // Return the corresponding colour for the disease extent class of the admin unit
        var diseaseExtentClassColourScale = {
            "Presence":          "#8e1b65",  // dark pink
            "Possible presence": "#c478a9",  // light pink
            "Uncertain":         "#ffffbf",  // yellow
            "Possible absence":  "#b5caaa",  // light green
            "Absence":           "#769766"   // dark green
        };

        function diseaseExtentLayerStyle(feature, clickable) {
            return {
                fillColor: diseaseExtentClassColourScale[feature.properties.diseaseExtentClass],
                fillOpacity: 0.7,
                opacity: 0.7,
                weight: 2,
                color: "#ffffff", // White border
                clickable: clickable
            };
        }

        function resetDiseaseExtentLayerStyle() {
            adminUnitsNeedReviewLayer.setStyle({
                weight: 2,
                color: "#ffffff" // Return to a white border
            });
        }

        function selectAdminUnit(layer) {
            resetDiseaseExtentLayerStyle();
            layer.setStyle({
                weight: 2.5,
                color: "#333333" // Highlight the admin unit with a grey border
            });
            if (!L.Browser.ie && !L.Browser.opera) {
                layer.bringToFront();
            }
            map.fitBounds(layer.getBounds(), { maxZoom: 5 });
        }

        // Define the geoJson layer, to which the disease extent data will be added via AJAX request
        var adminUnitLayerMap = {};
        var adminUnitsNeedReviewLayer = L.geoJson([], {
            style: function (feature) { return diseaseExtentLayerStyle(feature, true); },
            onEachFeature: function (feature, layer) {
                adminUnitLayerMap[feature.id] = layer;
                var data = { id: feature.id, name: feature.properties.name, count: feature.properties.occurrenceCount };
                layer.on({
                    click: function () { ko.postbox.publish("admin-unit-selected", data); },
                    mouseover: function () { layer.setStyle({ fillOpacity: 1 }); },
                    mouseout: function () { layer.setStyle({ fillOpacity: 0.7 }); }
                });
            }
        });

        var centroidMarkers = L.layerGroup();

        function addCentroidMarkerToMap(layer) {
            var centroid = layer.getBounds().getCenter();
            L.marker([centroid.lat, centroid.lng], {
                clickable: false,
                opacity: 0.8,
                icon: L.divIcon({
                    html: "<i class='fa fa-check'></i>",
                    className: "marker-centroid"
                })
            }).addTo(centroidMarkers);
        }

        var adminUnitsReviewedLayer = L.geoJson([], {
            style: function (feature) { return diseaseExtentLayerStyle(feature, false); },
            onEachFeature: function (feature, layer) { addCentroidMarkerToMap(layer); }
        });

        var diseaseExtentLayer = L.layerGroup([adminUnitsNeedReviewLayer, adminUnitsReviewedLayer, centroidMarkers]);

        function clearDiseaseExtentLayers() {
            adminUnitsNeedReviewLayer.clearLayers();
            adminUnitsReviewedLayer.clearLayers();
            centroidMarkers.clearLayers();
            adminUnitLayerMap = {};
        }

        function getDiseaseExtentRequestUrl(diseaseId) {
            if (loggedIn) {
                return baseUrl + "datavalidation/diseases/" + diseaseId + "/adminunits";
            } else {
                return baseUrl + "static/defaultAdminUnits.json";
            }
        }

        function createFeatureCollection(type, crs, features) {
            return { type: type, crs: crs, features: features };
        }

        // Display the admin units, and disease extent class, for the selected validator disease group.
        function switchDiseaseExtentLayer(diseaseId) {
            clearDiseaseExtentLayers();
            $.getJSON(getDiseaseExtentRequestUrl(diseaseId), function (fc) {
                var featuresNeedReview = _(fc.features).select(function (f) { return f.properties.needsReview; });
                var featureCollectionNeedReview = createFeatureCollection(fc.type, fc.crs, featuresNeedReview);
                adminUnitsNeedReviewLayer.addData(featureCollectionNeedReview);

                var featuresReviewed = _(fc.features).reject(function (f) { return f.properties.needsReview; });
                var featureCollectionReviewed = createFeatureCollection(fc.type, fc.crs, featuresReviewed);
                adminUnitsReviewedLayer.addData(featureCollectionReviewed);

                var data = _(featuresNeedReview).map(function (f) {
                    return { id: f.id, name: f.properties.name, count: f.properties.occurrenceCount };
                });
                ko.postbox.publish("admin-units-to-be-reviewed", { data: data, skipSerialize: true });
                ko.postbox.publish("no-features-to-review", featuresNeedReview.length === 0);
            });
        }

        /** LEGEND */

        function createLegendRow(className, colour) {
            var colourBox = "<i style='background:" + colour + "'></i>";
            var displayName = "<span>" + className + "</span><br>";
            return colourBox + displayName;
        }

        // Add a legend to display the disease extent class colour scale with corresponding class names
        var legend = L.control({position: "bottomleft"});
        legend.onAdd = function () {
            var div = L.DomUtil.create("div", "legend");
            div.innerHTML = "<span style='text-decoration:underline'>Current classification</span><br>" +
                _((_(diseaseExtentClassColourScale).pairs()).map(function (pair) {
                    return createLegendRow(pair[0], pair[1]);
                })).join("");
            div.innerHTML += "<i class='fa fa-check' style='padding-left:3px'></i>Admin unit reviewed";
            return div;
        };

        /** REACT TO EVENTS */

        var validationTypeIsDiseaseOccurrenceLayer;

        // Display the layer corresponding to the selected validation type (disease occurrences, or disease extent)
        function switchValidationTypeView() {
            if (validationTypeIsDiseaseOccurrenceLayer) {
                if (map.hasLayer(diseaseExtentLayer)) { map.removeControl(legend); }
                map.removeLayer(diseaseExtentLayer);
                clusterLayer.addLayer(diseaseOccurrenceLayer).addTo(map);
            } else {
                if (!map.hasLayer(diseaseExtentLayer)) { legend.addTo(map); }
                diseaseExtentLayer.addTo(map);
                map.removeLayer(clusterLayer);
            }
        }

        // Reset to default style when a point or admin unit is unselected (by clicking anywhere else on the map)
        function resetSelectedPoint() {
            ko.postbox.publish("point-selected", null);
            resetDiseaseOccurrenceLayerStyle();
        }

        function resetSelectedAdminUnit() {
            ko.postbox.publish("admin-unit-selected", null);
            resetDiseaseExtentLayerStyle();
        }

        function resetSelectedFeature() {
            if (validationTypeIsDiseaseOccurrenceLayer) {
                resetSelectedPoint();
            } else {
                resetSelectedAdminUnit();
            }
        }

        clusterLayer.on("clusterclick", resetSelectedPoint);
        map.on("click", resetSelectedFeature);

        ko.postbox.subscribe("layers-changed", function (data) {
            $("#spinner").show();
            validationTypeIsDiseaseOccurrenceLayer = (data.type === "disease occurrences");
            switchValidationTypeView();
            resetSelectedFeature();
            if (validationTypeIsDiseaseOccurrenceLayer) {
                switchDiseaseOccurrenceLayer(data.diseaseId);
            } else {
                switchDiseaseExtentLayer(data.diseaseId);
            }
            $("#spinner").fadeOut(800);
        });

        ko.postbox.subscribe("admin-unit-selected", function (data) {
            if (data !== null) { selectAdminUnit(adminUnitLayerMap[data.id]); }
        });

        ko.postbox.subscribe("occurrence-reviewed", function (id) {
            removeMarkerFromDiseaseOccurrenceLayer(id);
        });

        ko.postbox.subscribe("admin-unit-reviewed", function (id) {
            var layer = adminUnitLayerMap[id];
            adminUnitsNeedReviewLayer.removeLayer(layer);
            adminUnitsReviewedLayer.addData(layer.feature);
        });
    };
});
