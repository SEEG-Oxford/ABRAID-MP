/* A suite of tests for the atlas LegendViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/LegendViewModel",
    "ko"
], function (LegendViewModel, ko) {
    "use strict";

    describe("The atlas 'legend' view model", function () {
        var vm;
        beforeEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            vm = new LegendViewModel();
        });

        afterEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
        });

        describe("holds a field for the 'type' of legend to display, which", function () {
            it("is observable", function () {
                expect(vm.type).toBeObservable();
            });

            it("defaults to 'continuous'", function () {
                expect(vm.type()).toBe("continuous");
            });

            describe("responds to active-atlas-layer", function () {
                it("with 'continuous', when a non-extent layer is active", function () {
                    ko.postbox.publish("active-atlas-layer", "a_model_run_name_mean");
                    expect(vm.type()).toBe("continuous");
                    ko.postbox.publish("active-atlas-layer", "a_model_run_name_uncertainty");
                    expect(vm.type()).toBe("continuous");
                });

                it("with 'discrete', when an extent layer is active", function () {
                    ko.postbox.publish("active-atlas-layer", "a_model_run_name_extent");
                    expect(vm.type()).toBe("discrete");
                });

                it("with 'continuous', when no layer is active", function () {
                    ko.postbox.publish("active-atlas-layer", undefined);
                    expect(vm.type()).toBe("continuous");
                });

            });
        });
    });
});
