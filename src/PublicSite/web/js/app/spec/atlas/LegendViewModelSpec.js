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

            it("defaults to undefined", function () {
                expect(vm.type()).toBe(undefined);
            });

            it("responds to the active-atlas-type event", function () {
                var type = "mean";
                ko.postbox.publish("active-atlas-type", type);
                expect(vm.type()).toBe(type);
            });
        });
    });
});
