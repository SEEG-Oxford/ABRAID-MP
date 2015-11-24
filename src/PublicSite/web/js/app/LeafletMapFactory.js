/* foo.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "L",
    "jquery"
], function (L, $) {
    "use strict";

    return {
        create: function (elementId) {
            var dragBounds = L.latLngBounds(L.latLng(-60, -220), L.latLng(85, 220));
            var worldBounds = L.latLngBounds(L.latLng(-60, -180), L.latLng(85, 180));
            var acceptableBounds = L.latLngBounds(L.latLng(-60, -120), L.latLng(85, 120));

            var element = $("#" + elementId);
            var elementSize = new L.Point(element.width(), element.height());
            var maxZoomValue = 8;
            var calcZoom = function (bounds) {
                var zoom = 0;
                var nw = bounds.getNorthWest();
                var se = bounds.getSouthEast();
                var boundsSize;
                var crs = L.CRS.EPSG4326;
                do {
                    zoom = zoom + 1;
                    boundsSize = crs.latLngToPoint(se, zoom).subtract(crs.latLngToPoint(nw, zoom));
                } while (elementSize.contains(boundsSize) && zoom <= maxZoomValue);
                return zoom > 1 ? zoom - 1 : 1;
            };
            var minZoomValue = calcZoom(worldBounds);
            var initialZoomValue = calcZoom(acceptableBounds);

            return L.map(elementId, {
                attributionControl: false,
                zoomControl: false,
                zoomsliderControl: true,
                minZoom: minZoomValue,
                maxZoom: maxZoomValue,
                maxBounds: dragBounds,
                center: L.latLng(12.5, 0),
                zoom: initialZoomValue,
                animate: true,
                bounceAtZoomLimits: false,
                crs: L.CRS.EPSG4326
            });
        }
    };
});
