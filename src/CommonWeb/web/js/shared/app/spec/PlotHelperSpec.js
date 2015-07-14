/* A suite of tests for the PlotHelper AMD.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "shared/app/PlotHelper"
], function (helper) {
    "use strict";

    describe("PlotHelper defines", function () {
        describe("the 'createOptions' function which", function () {
            var data = {
                range: { min: -1, max: 5, extent: 6, dataMin: 0, dataMax: 4 },
                valuesHistogram: [
                    { min: -1, max: 1, count: 2 },
                    { min: 1, max: 2, count: 4 },
                    { min: 3, max: 5, count: 1 },
                    { min: 5, max: 5, count: 0 }
                ],
                effectCurve: [
                    { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                    { covariateValue: 3, upperQuantile: 4, meanInfluence: 4, lowerQuantile: 1 },
                    { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                ]
            };
            var discreteData = {
                range: { min: 0, max: 4, extent: 4, dataMin: 0, dataMax: 4 },
                valuesHistogram: [
                    { min: 0, max: 0, count: 2 },
                    { min: 4, max: 4, count: 1 }
                ],
                effectCurve: [
                    { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                    { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                ]
            };

            var compareOptions = function (actual, expectedTickFormatPrecision, expected) {
                var xFormat = actual.axis.x.tick.format;
                actual.axis.x.tick.format = undefined;
                var yFormat = actual.axis.y.tick.format;
                actual.axis.y.tick.format = undefined;
                expect(actual).toEqual(expected);

                if (expectedTickFormatPrecision === 2) {
                    expect(xFormat(11111.11)).toEqual(11000);
                    expect(yFormat(11111.11)).toEqual(11000);
                    expect(xFormat(0.000011111)).toEqual(0.000011);
                    expect(yFormat(0.000011111)).toEqual(0.000011);
                } else if (expectedTickFormatPrecision === 3) {
                    expect(xFormat(0.000011111)).toEqual(0.0000111);
                    expect(yFormat(0.000011111)).toEqual(0.0000111);
                } else {
                    expect(true).toBe(false);
                }
            };

            it("returns the correct options for small effect curve plot", function () {
                compareOptions(helper.createOptions(data, "name", false, false, false, 3, 7), 2, {
                    data: {
                        json: [
                            { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                            { covariateValue: 3, upperQuantile: 4, meanInfluence: 4, lowerQuantile: 1 },
                            { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                        ],
                        keys: {
                            x: "covariateValue",
                            value: ["upperQuantile", "meanInfluence", "lowerQuantile", "range"]
                        },
                        names: {
                            upperQuantile: "Upper Quantile",
                            meanInfluence: "Mean Influence",
                            lowerQuantile: "Lower Quantile",
                            range: "Range of data modelled"
                        },
                        colors: {
                            upperQuantile: "#b0c6a4",
                            meanInfluence: "#607953",
                            lowerQuantile: "#b0c6a4",
                            range: "rgba(0,0,0,0.1)"
                        },
                        type: "line"
                    },
                    size: { height: 198, width: 264 },
                    padding: { right: 20, top: 5 },
                    axis: {
                        x: {
                            label: { text: "name", position: "outer-right" },
                            tick: { count: 6, format: undefined },
                            padding: { left: 0, right: 0 }
                        },
                        y: {
                            label: { text: "Relative Influence (%)", position: "outer-top" },
                            tick: { count: 6, format: undefined },
                            min: 3,
                            max: 7
                        }
                    },
                    regions: [{ axis: "x", start: 0, end: 4 }],
                    interaction: { enabled: false },
                    legend: { show: false },
                    point: { show: false, r : undefined },
                    bar : { width : undefined },
                    grid: {
                        x: { show: false },
                        y: { show: false }
                    },
                    zoom: { enabled: false }
                });
            });

            it("returns the correct options for small discrete effect curve plot", function () {
                compareOptions(helper.createOptions(discreteData, "name", true, false, false, 3, 7), 2, {
                    data: {
                        json: [
                            { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                            { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                        ],
                        keys: {
                            x: "covariateValue",
                            value: ["upperQuantile", "meanInfluence", "lowerQuantile"]
                        },
                        names: {
                            upperQuantile: "Upper Quantile",
                            meanInfluence: "Mean Influence",
                            lowerQuantile: "Lower Quantile",
                            range: "Range of data modelled"
                        },
                        colors: {
                            upperQuantile: "#b0c6a4",
                            meanInfluence: "#607953",
                            lowerQuantile: "#b0c6a4",
                            range: "rgba(0,0,0,0.1)"
                        },
                        type: "scatter"
                    },
                    size: { height: 198, width: 264 },
                    padding: { right: 20, top: 5 },
                    axis: {
                        x: {
                            label: { text: "name", position: "outer-right" },
                            tick: { count: 2, format: undefined },
                            padding: { left: 1.6, right: 1.6 }
                        },
                        y: {
                            label: { text: "Relative Influence (%)", position: "outer-top" },
                            tick: { count: 6, format: undefined },
                            min: 3,
                            max: 7
                        }
                    },
                    regions: undefined,
                    interaction: { enabled: false },
                    legend: { show: false },
                    point: { show: false, r : undefined },
                    bar : { width : undefined },
                    grid: {
                        x: { show: false },
                        y: { show: false }
                    },
                    zoom: { enabled: false }
                });
            });
            it("returns the correct options for large effect curve plot", function () {
                compareOptions(helper.createOptions(data, "name", false, true, false, 3, 7), 3, {
                    data: {
                        json: [
                            { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                            { covariateValue: 3, upperQuantile: 4, meanInfluence: 4, lowerQuantile: 1 },
                            { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                        ],
                        keys: {
                            x: "covariateValue",
                            value: ["upperQuantile", "meanInfluence", "lowerQuantile", "range"]
                        },
                        names: {
                            upperQuantile: "Upper Quantile",
                            meanInfluence: "Mean Influence",
                            lowerQuantile: "Lower Quantile",
                            range: "Range of data modelled"
                        },
                        colors: {
                            upperQuantile: "#b0c6a4",
                            meanInfluence: "#607953",
                            lowerQuantile: "#b0c6a4",
                            range: "rgba(0,0,0,0.1)"
                        },
                        type: "line"
                    },
                    size: { height: 453, width: 668 },
                    padding: { right : 40, top : 5, left : 90, bottom : 0 },
                    axis: {
                        x: {
                            label: { text: undefined, position: "outer-center" },
                            tick: { count: 4, format: undefined },
                            padding: { left: 0, right: 0 }
                        },
                        y: {
                            label: { text: "Relative Influence (%)", position: "outer-middle" },
                            tick: { count: 13, format: undefined },
                            min: undefined,
                            max: undefined
                        }
                    },
                    regions: [{ axis: "x", start: 0, end: 4 }],
                    interaction: { enabled: true },
                    legend: { show: true },
                    point: { show: true, r : undefined },
                    bar : { width : undefined },
                    grid: {
                        x: { show: true },
                        y: { show: true }
                    },
                    zoom: { enabled: false }
                });
            });
            it("returns the correct options for large discrete effect curve plot", function () {
                compareOptions(helper.createOptions(discreteData, "name", true, true, false, 3, 7), 3, {
                    data: {
                        json: [
                            { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                            { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
                        ],
                        keys: {
                            x: "covariateValue",
                            value: ["upperQuantile", "meanInfluence", "lowerQuantile"]
                        },
                        names: {
                            upperQuantile: "Upper Quantile",
                            meanInfluence: "Mean Influence",
                            lowerQuantile: "Lower Quantile",
                            range: "Range of data modelled"
                        },
                        colors: {
                            upperQuantile: "#b0c6a4",
                            meanInfluence: "#607953",
                            lowerQuantile: "#b0c6a4",
                            range: "rgba(0,0,0,0.1)"
                        },
                        type: "scatter"
                    },
                    size: { height: 453, width: 668 },
                    padding: { right : 40, top : 5, left : 90, bottom : 0 },
                    axis: {
                        x: {
                            label: { text: undefined, position: "outer-center" },
                            tick: { count: 2, format: undefined },
                            padding: { left: 1.6, right: 1.6 }
                        },
                        y: {
                            label: { text: "Relative Influence (%)", position: "outer-middle" },
                            tick: { count: 13, format: undefined },
                            min: undefined,
                            max: undefined
                        }
                    },
                    regions: undefined,
                    interaction: { enabled: true },
                    legend: { show: true },
                    point: { show: true, r : 3.5 },
                    bar : { width : undefined },
                    grid: {
                        x: { show: true },
                        y: { show: true }
                    },
                    zoom: { enabled: false }
                });
            });
            it("returns the correct options for large values histogram plot", function () {
                compareOptions(helper.createOptions(data, "name", false, true, true, 3, 7), 3, {
                    data: {
                        json: [
                            { min: -1, max: 1, count: 2 },
                            { min: 1, max: 2, count: 4 },
                            { min: 3, max: 5, count: 1 },
                            { min: 5, max: 5, count: 0 }
                        ],
                        keys: {
                            x: "min",
                            value: ["count"]
                        },
                        names: {
                            count: "Count"
                        },
                        colors: {
                            count: "#88A07C"
                        },
                        type: "histogram"
                    },
                    size: { height: 300, width: 668 },
                    padding: { right : 40, top : 5, left : 90, bottom : 15 },
                    axis: {
                        x: {
                            label: { text: "name", position: "outer-center" },
                            tick: { count: 4, format: undefined },
                            padding: { left: 0, right: 0 }
                        },
                        y: {
                            label: { text: "Count of Pixels", position: "outer-middle" },
                            tick: { count: 8, format: undefined },
                            min: undefined,
                            max: undefined
                        }
                    },
                    regions: [{ axis: "x", start: 0, end: 4 }],
                    interaction: { enabled: false },
                    legend: { show: false },
                    point: { show: true, r : undefined },
                    bar : { width : undefined },
                    grid: {
                        x: { show: true },
                        y: { show: true }
                    },
                    zoom: { enabled: false }
                });
            });
            it("returns the correct options for large discrete values histogram plot", function () {
                compareOptions(helper.createOptions(discreteData, "name", true, true, true, 3, 7), 3, {
                    data: {
                        json: [
                            { min: 0, max: 0, count: 2 },
                            { min: 4, max: 4, count: 1 }
                        ],
                        keys: {
                            x: "min",
                            value: ["count"]
                        },
                        names: {
                            count: "Count"
                        },
                        colors: {
                            count: "#88A07C"
                        },
                        type: "bar"
                    },
                    size: { height: 300, width: 668 },
                    padding: { right : 40, top : 5, left : 90, bottom : 15 },
                    axis: {
                        x: {
                            label: { text: "name", position: "outer-center" },
                            tick: { count: 2, format: undefined },
                            padding: { left: 1.6, right: 1.6 }
                        },
                        y: {
                            label: { text: "Count of Pixels", position: "outer-middle" },
                            tick: { count: 8, format: undefined },
                            min: undefined,
                            max: undefined
                        }
                    },
                    regions: undefined,
                    interaction: { enabled: false },
                    legend: { show: false },
                    point: { show: true, r : undefined },
                    bar : { width : 6 },
                    grid: {
                        x: { show: true },
                        y: { show: true }
                    },
                    zoom: { enabled: false }
                });
            });
        });
        describe("the 'prepareData' function which", function () {
            var valuesHistogram = [
                { min: -1, max: 1, count: 2 },
                { min: 1, max: 2, count: 4 },
                { min: 3, max: 5, count: 1 }
            ];
            var effectCurve = [
                { covariateValue: 0, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                { covariateValue: 3, upperQuantile: 4, meanInfluence: 4, lowerQuantile: 1 },
                { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
            ];
            var discreteValuesHistogram = [
                { min: 0, max: 0, count: 2 },
                { min: 1, max: 1, count: 1 },
                { min: 4, max: 4, count: 1 },
                { min: 5, max: 5, count: 1 }
            ];
            var discreteEffectCurve = [
                { covariateValue: 1, upperQuantile: 3, meanInfluence: 2, lowerQuantile: 1 },
                { covariateValue: 4, upperQuantile: 3.2, meanInfluence: 1, lowerQuantile: -1 }
            ];
            it("determines the correct range for continuous data", function () {
                var result = helper.prepareData(effectCurve, valuesHistogram, false);
                expect(result.range.min).toBe(-1);
                expect(result.range.max).toBe(5);
                expect(result.range.extent).toBe(6);
                expect(result.range.dataMin).toBe(0);
                expect(result.range.dataMax).toBe(4);
            });
            it("determines the correct range for discrete data", function () {
                var result = helper.prepareData(discreteEffectCurve, discreteValuesHistogram, true);
                expect(result.range.min).toBe(0);
                expect(result.range.max).toBe(5);
                expect(result.range.extent).toBe(5);
                expect(result.range.dataMin).toBe(1);
                expect(result.range.dataMax).toBe(4);
            });
            it("extends the range of continuous effect curve data to match that of the histogram", function () {
                var result = helper.prepareData(effectCurve, valuesHistogram, false);
                expect(result.effectCurve[0]).toEqual(
                    { covariateValue : -1, upperQuantile : 3, meanInfluence : 2, lowerQuantile : 1 });
                expect(result.effectCurve[4]).toEqual(
                    { covariateValue : 5, upperQuantile : 3.2, meanInfluence : 1, lowerQuantile : -1 });
            });
            it("extends the range of discrete effect curve data to match that of the histogram", function () {
                var result = helper.prepareData(discreteEffectCurve, discreteValuesHistogram, true);
                expect(result.effectCurve[0]).toEqual(
                    { covariateValue : 0, upperQuantile : 3, meanInfluence : 2, lowerQuantile : 1 });
                expect(result.effectCurve[3]).toEqual(
                    { covariateValue : 5, upperQuantile : 3.2, meanInfluence : 1, lowerQuantile : -1 });
            });
            it("add a 0 height bar to the max end of the histogram for continuous data", function () {
                var result = helper.prepareData(effectCurve, valuesHistogram, false);
                expect(result.valuesHistogram[3]).toEqual({ min : 5, max : 5, count : 0 });
            });
            it("does not add a 0 height bar to the max end of the histogram for discrete data", function () {
                var result = helper.prepareData(discreteEffectCurve, discreteValuesHistogram, true);
                expect(result.valuesHistogram.length).toEqual(4);
            });
        });
    });
});
