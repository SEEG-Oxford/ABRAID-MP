/* Generates c3 plot options for rendering effect curve data.
 * Copyright (c) 2014 University of Oxford
 */
define(["underscore"], function (_) {
    "use strict";

    return {
        createOptions: function (data, name, isDiscrete, isLarge, isHistogram, ymin, ymax) {
            var formatTick = function (isLarge) {
                return function (n) {
                    return Number(n.toPrecision(isLarge ? 3 : 2));
                };
            };

            var createDataOptions = function (data, isDiscrete, isLarge, isHistogram) {
                return {
                    json: isHistogram ? data.valuesHistogram: data.effectCurve,
                    keys: {
                        x: isHistogram ? "min" : "covariateValue",
                        value: isHistogram ?
                            [ "count" ] :
                            [ "upperQuantile", "meanInfluence", "lowerQuantile" ].concat(isDiscrete ? [] : [ "range" ])
                    },
                    names: isHistogram ? {
                        "count": "Count"
                    } : {
                        "upperQuantile": "Upper Quantile",
                        "meanInfluence": "Mean Influence",
                        "lowerQuantile": "Lower Quantile",
                        "range": "Range of data modelled"
                    },
                    colors: isHistogram ? {
                        "count": "#88A07C"
                    } : {
                        "upperQuantile": "#b0c6a4",
                        "meanInfluence": "#607953",
                        "lowerQuantile": "#b0c6a4",
                        "range": "rgba(0,0,0,0.1)"
                    },
                    type: isHistogram ? (isDiscrete ? "bar" : "histogram") : (isDiscrete ? "scatter" : "line")
                };
            };

            var createXAxisOptions = function (isDiscrete, isLarge, isHistogram, name, range) {
                return  {
                    label: {
                        text: !isLarge || isHistogram ? name : undefined,
                        position: isLarge ? "outer-center" : "outer-right"
                    },
                    tick: {
                        count: isDiscrete ? 2 : (isLarge ? data.valuesHistogram.length : 6),
                        format: formatTick(isLarge)
                    },
                    padding: isDiscrete ?
                    { left: 0.4 * range.extent, right: 0.4 * range.extent } :
                    { left: 0, right: 0 }
                };
            };

            var createYAxisOptions = function (isLarge, isHistogram, ymin, ymax) {
                return {
                    label: {
                        text: isHistogram ? "Count of Pixels" : "Relative Influence (%)",
                        position: isLarge ? "outer-middle" : "outer-top"
                    },
                    tick: {
                        count: isLarge ? (isHistogram ? 8 : 13) : 6,
                        format: formatTick(isLarge)
                    },
                    min: isLarge ? undefined : ymin,
                    max: isLarge ? undefined: ymax
                };
            };

            return {
                data: createDataOptions(data, isDiscrete, isLarge, isHistogram),
                size: isLarge ? { height: isHistogram ? 300 : 453, width: 668 } : { height: 198, width: 264 },
                padding: isLarge ?
                    { right: 40, top: 5, left: 90, bottom: isHistogram ? 15 : 0 } :
                    { right: 20, top: 5 },
                axis: {
                    x: createXAxisOptions(isDiscrete, isLarge, isHistogram, name, data.range),
                    y: createYAxisOptions(isLarge, isHistogram, ymin, ymax)
                },
                regions: isDiscrete ? undefined : [
                    {axis: "x", start: data.range.dataMin, end: data.range.dataMax}
                ],
                interaction: { enabled: isLarge && !isHistogram },
                legend: { show: isLarge && !isHistogram  },
                point: {
                    show: isLarge,
                    r: isLarge && isDiscrete && !isHistogram ? 3.5 : undefined
                },
                bar: {
                    width: isHistogram && isDiscrete ? 6 : undefined
                },
                grid: {
                    x: { show: isLarge },
                    y: { show: isLarge }
                },
                zoom: { enabled: false }
            };
        },
        prepareData: function (curveData, histogramData, discrete) {
            // Calculate the range
            var histogram = _(histogramData).sortBy("min");
            var curve = _(curveData).sortBy("covariateValue");
            var trueMin = histogram[0].min;
            var trueMax = histogram[histogram.length - 1].max;
            var dataMin = curve[0].covariateValue;
            var dataMax = curve[curve.length - 1].covariateValue;

            // Extend the effect curve to match the range of the histogram (instead of presence data range)
            if (trueMin < dataMin) {
                curve.splice(0, 0, {
                    covariateValue: trueMin,
                    upperQuantile: curve[0].upperQuantile,
                    meanInfluence: curve[0].meanInfluence,
                    lowerQuantile: curve[0].lowerQuantile
                });
            }
            if (trueMax > dataMax) {
                curve.push({
                    covariateValue: trueMax,
                    upperQuantile: curve[curve.length - 1].upperQuantile,
                    meanInfluence: curve[curve.length - 1].meanInfluence,
                    lowerQuantile: curve[curve.length - 1].lowerQuantile
                });
            }

            // Add a hidden bar to the max of the histogram (to get a tick on the axis)
            if (!discrete) {
                histogram.push({
                    min: trueMax,
                    max: trueMax,
                    count: 0
                });
            }

            return {
                range: {min: trueMin, max: trueMax, extent: trueMax - trueMin, dataMin: dataMin, dataMax: dataMax},
                valuesHistogram: histogram,
                effectCurve: curve
            };
        }
    };
});
