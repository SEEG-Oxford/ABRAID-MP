/*
 A collection of changes/additions to the behavior of c3js to account for bugs/hardcoded values/missing functionally.
 This is tested against c3 v0.4.11-rc1. Feature requests submitted upstream for "proper" implementation within c3.
 */
define([
    "jquery",
    "c3"
], function ($, c3) {
    "use strict";

    // Remove hardcoded transparency from scatter plot points
    c3.chart.internal.fn.opacityForCircleOriginal = c3.chart.internal.fn.opacityForCircle;
    c3.chart.internal.fn.opacityForCircle = function (d) {
        var $$ = this;
        return this.isScatterType(d) ? 1 : $$.opacityForCircleOriginal(d);
    };

    // Define a new histogram plot type, which is a subtype of bar chart, but with different min/max x bar values
    c3.chart.internal.fn.hasTypeOriginal = c3.chart.internal.fn.hasType;
    c3.chart.internal.fn.hasType = function (type, targets) {
        var $$ = this;
        return $$.hasTypeOriginal(type, targets) || (type === "bar" ? $$.hasTypeOriginal("histogram", targets) : false);
    };
    c3.chart.internal.fn.isHistogramType = function (d) {
        var id = this.isString(d) ? d : d.id;
        return this.config.data_types[id] === 'histogram';
    };
    c3.chart.internal.fn.isBarTypeOriginal = c3.chart.internal.fn.isBarType;
    c3.chart.internal.fn.isBarType = function (d) {
        var $$ = this;
        return $$.isBarTypeOriginal(d) || $$.isHistogramType(d);
    };

    c3.chart.internal.fn.generateGetBarPointsOriginal = c3.chart.internal.fn.generateGetBarPoints;
    c3.chart.internal.fn.generateGetBarPoints = function (barIndices, isSub) {
        var $$ = this;
        var coordFunction = $$.generateGetBarPointsOriginal(barIndices, isSub);
        return function (d, i) {
            if ($$.isHistogramType(d)) {
                var minX = $$.getXValue(d.id, i);
                var maxX = i + 1 < $$.data.xs[d.id].length ? $$.getXValue(d.id, i + 1) : minX;
                var minY = 0;
                var maxY = d.value;

                return [
                    [$$.x(minX) + 1, $$.y(minY)],
                    [$$.x(minX) + 1, $$.y(maxY)],
                    [$$.x(maxX) - 1, $$.y(maxY)],
                    [$$.x(maxX) - 1, $$.y(minY)]
                ];
            } else {
                return coordFunction(d, i);
            }
        };
    };

    // Force grid lines to align to x axis ticks
    c3.chart.internal.fn.generateGridDataOriginal = c3.chart.internal.fn.generateGridData;
    c3.chart.internal.fn.generateGridData = function (type, scale) {
        var $$ = this;
        return $$.xAxisTickValues || $$.generateGridDataOriginal(type, scale);
    };

    // Add a "save" function to export PNGs to c3 plots
    c3.chart.fn.save = function (filename) {
        var realSvg = this.internal.svg[0][0];
        var supportsToDataURL = function () {
            var c = document.createElement("canvas");
            var data = c.toDataURL("image/png");
            return (data.indexOf("data:image/png") == 0);
        };
        var supportsCanvas = function () {
            var c = document.createElement("canvas");
            var data = c.toDataURL("image/png");
            return (data.indexOf("data:image/png") == 0);
        };
        var isIE = function () {
            return navigator.userAgent.indexOf("MSIE ") > -1 ||
                navigator.userAgent.indexOf("Trident/") > -1 ||
                navigator.userAgent.indexOf("Edge/") > -1;
        };

        var getSvgAsText = function (realSvg) {
            // Create copy of the chart (but dont add it the DOM, let it get gc'ed at end of function)
            var svgClone = realSvg.cloneNode(true);

            // Recursively inline the computed style of all svg (sub-)elements
            var allValidSVGStyleProperties = ["alignment-baseline", "baseline-shift", "clip", "clip-path", "clip-rule", "color", "color-interpolation", "color-interpolation-filters", "color-profile", "color-rendering", "cursor", "direction", "display", "dominant-baseline", "enable-background", "fill", "fill-opacity", "fill-rule", "filter", "flood-color", "flood-opacity", "font", "font-family", "font-size", "font-size-adjust", "font-stretch", "font-style", "font-variant", "font-weight", "glyph-orientation-horizontal", "glyph-orientation-vertical", "image-rendering", "kerning", "letter-spacing", "lighting-color", "marker", "marker-end", "marker-mid", "marker-start", "mask", "opacity", "overflow", "pointer-events", "shape-rendering", "stop-color", "stop-opacity", "stroke", "stroke-dasharray", "stroke-dashoffset", "stroke-linecap", "stroke-linejoin", "stroke-miterlimit", "stroke-opacity", "stroke-width", "text-anchor", "text-decoration", "text-rendering", "unicode-bidi", "visibility", "word-spacing", "writing-mode"];
            $(svgClone).css($(realSvg).css(allValidSVGStyleProperties));
            var realSvgElements = $(realSvg).find("*");
            var cloneSvgElements = $(svgClone).find("*");
            for (var i = 0; i < realSvgElements.size(); i = i + 1) {
                // JQUERY ick!
                $(cloneSvgElements[i]).css($(realSvgElements[i]).css(allValidSVGStyleProperties));
            }

            // Make sure the svg clone is full valid svg doc
            var xmlns = "http://www.w3.org/2000/xmlns/";
            svgClone.setAttribute("version", "1.1");
            svgClone.setAttributeNS(xmlns, "xmlns", "http://www.w3.org/2000/svg");
            svgClone.setAttributeNS(xmlns, "xmlns:xlink", "http://www.w3.org/1999/xlink");
            svgClone.setAttribute("width", realSvg.getAttribute("width"));
            svgClone.setAttribute("height", realSvg.getAttribute("height"));
            svgClone.setAttribute("viewBox", "0 0 " + realSvg.getAttribute("width") + " " + realSvg.getAttribute("height"));

            // Get svg as text
            var div = document.createElement("div");
            div.appendChild(svgClone);
            var doctype = '<?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">';
            return doctype + div.innerHTML;
        };

        function convertSvgTextToSvgDataUrl(svgText) {
            return "data:image/svg+xml;base64," + btoa(unescape(encodeURIComponent(svgText)));
        }

        if (isIE() || !(supportsCanvas() && supportsToDataURL())) {
            alert("This functionality is not available in your web browser");
        } else {
            var svgText = getSvgAsText(realSvg);
            // Create a canvas
            var canvas = document.createElement("canvas");
            canvas.setAttribute("width", realSvg.getAttribute("width"));
            canvas.setAttribute("height", realSvg.getAttribute("height"));
            var canvasContext = canvas.getContext("2d");

            // Draw svg into canvas
            var image = new Image();
            image.src = convertSvgTextToSvgDataUrl(svgText);
            $(image).on("load", function () {
                canvasContext.drawImage(
                    image, 0, 0, canvas.getAttribute("width"), canvas.getAttribute("height"));

                // Trigger download
                var filenameClean = filename.replace(/[^\w\d]+/gi, "-").toLowerCase() + ".png";
                var a = document.createElement("a");
                a.download = filenameClean;
                var canvasData = canvas.toDataURL("image/png");
                a.href = canvasData;
                document.body.appendChild(a);
                a.addEventListener("click", function () {
                    a.parentNode.removeChild(a);
                });
                a.click();
            });
        }
    };
});
