/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-reviewed' - published by SelectedAdminUnitViewModel
 * -- 'admin-unit-selected' - published by MapView
 * -- 'layers-changed'      - published by SelectedLayerViewModel
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
    "underscore",
    "app/LeafletMapFactory"
], function (L, $, ko, _, LeafletMapFactory) {
    "use strict";

    return function (baseUrl, wmsUrl, loggedIn, alert, setTimeout) { // jshint ignore:line

        /** MAP AND BASE LAYER */
        // Initialise map at "map" div
        var map = LeafletMapFactory.create("map");

        // Add the simplified shapefile base layer with WMS GET request
        var baseLayer = L.tileLayer.wms(wmsUrl, {
            layers: ["abraid:base_layer"],
            format: "image/png",
            reuseTiles: true, // Enable Leaflet reuse of tiles within single page view
            tiled: true // Enable GeoWebCaching reuse of tiles between all users/page views
        }).addTo(map);

        var hatchingLayer = L.tileLayer.wms(wmsUrl, {
            layers: ["abraid:hatching"],
            format: "image/png",
            reuseTiles: true, // Enable Leaflet reuse of tiles within single page view
            tiled: true // Enable GeoWebCaching reuse of tiles between all users/page views
        });

        // Track when zooming is happening
        map.isZooming = false;
        map.isPanning = false;
        map.isSpidering = false;
        map.on("zoomstart", function () { map.isZooming = true; });
        map.on("zoomend", function () { map.isZooming = false; });
        map.on("movestart", function () { map.isPanning = true; });
        map.on("moveend", function () { map.isPanning = false; });

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
            marker.bindPopup(feature.properties.locationName, {
                closeButton: false,
                maxWidth: 500,
                offset: [0, 35]
            });
            marker.on("click", function (e) {
                if (e) { L.DomEvent.stop(e); }
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

        function addDiseaseOccurrenceData(diseaseId) {
            if (ajax) {
                ajax.abort();
            }
            ajax = $.getJSON(getDiseaseOccurrencesRequestUrl(diseaseId))
                .done(function (featureCollection) {
                    if (featureCollection.features.length !== 0) {
                        ko.postbox.publish("no-features-to-review", false);
                        clusterLayer.addLayer(diseaseOccurrenceLayer.addData(featureCollection));
                        map.fitBounds(diseaseOccurrenceLayer.getBounds());
                    } else {
                        ko.postbox.publish("no-features-to-review", true);
                        map.fitWorld();
                    }
                }).fail(function (xhr, status) {
                    if (status !== "abort") {
                        alert("Error fetching occurrences");
                    }
                }).always(function () {
                    ko.postbox.publish("map-view-update-in-progress", false);
                });
        }

        // Add the new feature collection to the clustered layer, and zoom to its bounds
        function switchDiseaseOccurrenceLayer(diseaseId) {
            clearDiseaseOccurrenceLayer();
            addDiseaseOccurrenceData(diseaseId);
        }

        function findClosestOccurrenceMarkerToCoordinates(coords) {
            return _.chain(layerMap).values().sortBy(function (marker) {
                return coords.distanceTo(extractLatLng(marker));
            }).first().value();
        }

        function clickOccurrenceMarker(marker) {
            var latlong = marker.getLatLng();
            marker.fireEvent("click", {
                latlng: latlong,
                layerPoint: map.latLngToLayerPoint(latlong),
                containerPoint: map.latLngToContainerPoint(latlong)
            });
        }

        function extractLatLng(marker) {
            return new L.LatLng(marker.feature.geometry.coordinates[1], marker.feature.geometry.coordinates[0]);
        }

        function selectOccurrenceMarker(target) {
            var targetLatLng = extractLatLng(target);
            if (!map.getCenter().equals(targetLatLng)) {
                // Center the target in the view
                map.panTo(targetLatLng);
            }


            var recursivelySelectOccurrenceMarker = function () {
                // It should be able to replace all of this with
                // clusterLayer.zoomToShowLayer(target, function () { clickOccurrenceMarker(target); });
                // but zoomToShowLayer is buggy and doesnt call its callback on all paths

                if (!map.isZooming && !map.isPanning && !map.isSpidering) {
                    var parent = clusterLayer.getVisibleParent(target);

                    if (target._map) { /* jshint ignore:line */
                        // The point is on the map, so click it
                        clickOccurrenceMarker(target);
                        // Complete
                        return;
                    } else if (parent) {
                        // The closest point isn't on the map, but is inside a cluster on the map,
                        // so click the cluster to open it. Then try again, so the spider leg gets clicked
                        if (map.getZoom() !== map.getMaxZoom()) {
                            parent.zoomToBounds();
                        } else {
                            map.isSpidering = true;
                            clusterLayer.once("spiderfied", function () {
                                map.isSpidering = false;
                            });
                            parent.spiderfy();
                        }
                    } else {
                        // Give up
                        return;
                    }
                }

                // If we are in the middle of an animation, wait for it to complete before continuing
                // If we just clicked something that isn't the target occurrence, continue to try and click the target
                setTimeout(recursivelySelectOccurrenceMarker, 50);
            };
            recursivelySelectOccurrenceMarker();
        }

        function selectClosestOccurrenceMarkerToCoordinates(coords) {
            var closest = findClosestOccurrenceMarkerToCoordinates(coords);
            selectOccurrenceMarker(closest);
        }

        // Remove the feature's marker layer from the disease occurrence layer, and delete record of the feature.
        // Then select the next feature for review.
        function removeMarkerFromDiseaseOccurrenceLayerAndSelectNext(id) {
            var coords = extractLatLng(layerMap[id]);

            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.removeLayer(layerMap[id]);
            delete layerMap[id];

            if (_(layerMap).isEmpty()) {
                ko.postbox.publish("no-features-to-review", true);
            } else {
                clusterLayer.addLayer(diseaseOccurrenceLayer);
                selectClosestOccurrenceMarkerToCoordinates(coords);
            }
        }

        /** DISEASE EXTENT LAYER */

        // Return the corresponding colour for the disease extent class of the admin unit
        var extentClassColour = {
            "Presence":          ["#a44883", "#8e1b65"],  // dark pink
            "Possible presence": ["#cf93ba", "#c478a9"],  // light pink
            "Uncertain":         ["#ffffcb", "#ffffbf"],  // yellow
            "Possible absence":  ["#c3d4bb", "#b5caaa"],  // light green
            "Absence":           ["#91ab84", "#769766"]   // dark green
        };
        // The first colour with opacity set to 1, matches the second colour with opacity value of 0.8.
        // Lowering the opacity for reviewed admin units (!needsReview) reveals the hatching layer underneath,
        // giving the effect of a shaded polygon.
        function diseaseExtentLayerStyle(feature, needsReview) {
            return {
                fillColor:  extentClassColour[feature.properties.diseaseExtentClass][needsReview ? 0 : 1],
                fillOpacity: needsReview ? 1 : 0.8,
                color: "#8c8c8c",       // Grey border
                opacity: 1,
                weight: 0.75,
                clickable: needsReview  // Determines whether mouse events are listened for
            };
        }

        // Return to a white border
        function resetDiseaseExtentLayerStyle() {
            adminUnitsNeedReviewLayer.setStyle({ weight: 0.5, color: "#8c8c8c" });
        }

        // Highlight the admin unit with a grey border and centre it on the map
        function selectAdminUnit(layer) {
            resetDiseaseExtentLayerStyle();
            layer.setStyle({ weight: 3.5, color: "#5c5c5c" });
            if (!L.Browser.ie && !L.Browser.opera) { layer.bringToFront(); }
            map.fitBounds(layer.getBounds(), { padding: [300, 300] });
        }

        // Define the geoJson layer, to which the disease extent data will be added via AJAX request
        var adminUnitLayerMap = {};
        var adminUnitsNeedReviewLayer = L.geoJson([], {
            style: function (feature) { return diseaseExtentLayerStyle(feature, true); },
            onEachFeature: function (feature, layer) {
                adminUnitLayerMap[feature.id] = layer;
                var data = {
                    id: feature.id,
                    name: feature.properties.name,
                    count: feature.properties.occurrenceCount
                };
                layer.on({
                    click: function (e) { L.DomEvent.stop(e); ko.postbox.publish("admin-unit-selected", data); },
                    mouseover: function () {
                        layer.setStyle({ fillColor: extentClassColour[feature.properties.diseaseExtentClass][1] });
                    },
                    mouseout: function () {
                        layer.setStyle({ fillColor: extentClassColour[feature.properties.diseaseExtentClass][0] });
                    }
                });
            }
        });

        var adminUnitsReviewedLayer = L.geoJson([], {
            style: function (feature) { return diseaseExtentLayerStyle(feature, false); }
        });

        var diseaseExtentLayer = L.layerGroup([hatchingLayer, adminUnitsNeedReviewLayer, adminUnitsReviewedLayer]);

        function clearDiseaseExtentLayers() {
            adminUnitsNeedReviewLayer.clearLayers();
            adminUnitsReviewedLayer.clearLayers();
            adminUnitLayerMap = {};
        }

        function getDiseaseExtentRequestUrl(diseaseId) {
            var adminUnits = loggedIn ? ("diseases/" + diseaseId  + "/adminunits") : "defaultadminunits";
            return baseUrl + "datavalidation/" + adminUnits;
        }

        function createFeatureCollection(type, crs, features) {
            return { type: type, crs: crs, features: features };
        }

        function publishDiseaseExtentEvents(features) {
            var data = _(features).map(function (f) {
                return {
                    id: f.id,
                    name: f.properties.name,
                    count: f.properties.occurrenceCount
                };
            });
            ko.postbox.publish("admin-units-to-be-reviewed", { data: data, skipSerialize: true });
            ko.postbox.publish("no-features-to-review", features.length === 0);
        }

        function fitMapBounds(features) {
            if (features.length === 0) {
                map.fitWorld();
            } else {
                var presenceFeatures = _(features).filter(function (f) {
                    return _(["Presence", "Possible presence", "Uncertain"]).contains(f.properties.diseaseExtentClass);
                });
                map.fitBounds(L.geoJson(presenceFeatures).getBounds());
            }
        }

        function addDiseaseExtentData(diseaseId) {
            if (ajax) {
                ajax.abort();
            }
            ajax = $.getJSON(getDiseaseExtentRequestUrl(diseaseId))
                .done(function (fc) {
                    fitMapBounds(fc.features);

                    var featuresNeedReview = _(fc.features).select(function (f) { return f.properties.needsReview; });
                    var featureCollectionNeedReview = createFeatureCollection(fc.type, fc.crs, featuresNeedReview);
                    adminUnitsNeedReviewLayer.addData(featureCollectionNeedReview);

                    var featuresReviewed = _(fc.features).reject(function (f) { return f.properties.needsReview; });
                    var featureCollectionReviewed = createFeatureCollection(fc.type, fc.crs, featuresReviewed);
                    adminUnitsReviewedLayer.addData(featureCollectionReviewed);

                    publishDiseaseExtentEvents(featuresNeedReview);
                }).fail(function (xhr, status) {
                    if (status !== "abort") {
                        alert("Error fetching disease extent");
                    }
                }).always(function () {
                    ko.postbox.publish("map-view-update-in-progress", false);
                });
        }

        // Display the admin units, and disease extent class, for the selected validator disease group.
        function switchDiseaseExtentLayer(diseaseId) {
            clearDiseaseExtentLayers();
            addDiseaseExtentData(diseaseId);
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
            var div = L.DomUtil.create("div", "legend leaflet-bar leaflet-control");
            div.innerHTML = "<div style='text-align: center'>Current classification</div><hr>" +
                _((_(extentClassColour).pairs()).map(function (pair) {
                    return createLegendRow(pair[0], pair[1][1]);
                })).join("");
            div.setAttribute("data-bind", "preventBubble: true");
            ko.applyBindings({}, div);
            return div;
        };

        /** REACT TO EVENTS */

        var validationTypeIsDiseaseOccurrenceLayer;
        var ajax;

        // Display the layer corresponding to the selected validation type (disease occurrences, or disease extent)
        function switchValidationTypeView() {
            if (validationTypeIsDiseaseOccurrenceLayer) {
                if (map.hasLayer(diseaseExtentLayer)) { map.removeControl(legend); }
                map.removeLayer(diseaseExtentLayer);
                clusterLayer.addLayer(diseaseOccurrenceLayer).addTo(map);
                map.addLayer(baseLayer);
            } else {
                if (!map.hasLayer(diseaseExtentLayer)) { legend.addTo(map); }
                diseaseExtentLayer.addTo(map);
                map.removeLayer(clusterLayer);
                map.removeLayer(baseLayer);
            }
        }

        // Reset to default style when a point or admin unit is unselected (by clicking anywhere else on the map)
        function resetSelectedPoint(e) {
            if (e) { L.DomEvent.stop(e); }
            ko.postbox.publish("point-selected", null);
            resetDiseaseOccurrenceLayerStyle();
        }

        function resetSelectedAdminUnit() {
            ko.postbox.publish("admin-unit-selected", null);
            resetDiseaseExtentLayerStyle();
        }

        function resetSelectedFeature(e) {
            if (e) { L.DomEvent.stop(e); }
            if (validationTypeIsDiseaseOccurrenceLayer) {
                resetSelectedPoint();
            } else {
                resetSelectedAdminUnit();
            }
        }

        clusterLayer.on("clusterclick", resetSelectedPoint);
        map.on("click", resetSelectedFeature);

        function selectClosestAdminUnitInNeedOfReview(point) {
            var candidateLayers = adminUnitsNeedReviewLayer.getLayers();
            if (_.isEmpty(candidateLayers)) {
                map.fitWorld();
                ko.postbox.publish("no-features-to-review", true);
            } else {
                var closestLayer = _.min(candidateLayers, function (candidateLayer) {
                    return candidateLayer.getBounds().getCenter().distanceTo(point);
                });
                ko.postbox.publish("admin-unit-selected", {
                    id: closestLayer.feature.id,
                    name: closestLayer.feature.properties.name,
                    count: closestLayer.feature.properties.occurrenceCount
                });
            }
        }

        ko.postbox.subscribe("layers-changed", function (data) {
            ko.postbox.publish("map-view-update-in-progress", true);
            validationTypeIsDiseaseOccurrenceLayer = (data.type === "disease occurrences");
            switchValidationTypeView();
            resetSelectedFeature();
            if (validationTypeIsDiseaseOccurrenceLayer) {
                switchDiseaseOccurrenceLayer(data.diseaseId);
            } else {
                switchDiseaseExtentLayer(data.diseaseId);
            }

            ko.postbox.publish("tracking-action-event", {
                "category": "validator",
                "action": validationTypeIsDiseaseOccurrenceLayer ? "layer-view-occurrence" : "layer-view-extent",
                "label": data.diseaseName
            });
        });

        ko.postbox.subscribe("admin-unit-selected", function (data) {
            if (data !== null) { selectAdminUnit(adminUnitLayerMap[data.id]); }
        });

        ko.postbox.subscribe("occurrence-reviewed", function (id) {
            removeMarkerFromDiseaseOccurrenceLayerAndSelectNext(id);
        });

        ko.postbox.subscribe("admin-unit-reviewed", function (id) {
            var layer = adminUnitLayerMap[id];
            var center = layer.getBounds().getCenter();
            adminUnitsNeedReviewLayer.removeLayer(layer);
            adminUnitsReviewedLayer.addData(layer.feature);
            selectClosestAdminUnitInNeedOfReview(center);
        });
    };
});
