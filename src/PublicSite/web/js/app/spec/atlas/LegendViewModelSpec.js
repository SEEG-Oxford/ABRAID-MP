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

            it("responds to the active-atlas-layer event", function () {
                var type = "mean";
                ko.postbox.publish("active-atlas-layer", { type: type, run: {} });
                expect(vm.type()).toBe(type);
            });
        });

        describe("holds a field for the 'startDate' of the selected model run's input data, which", function () {
            it("is observable", function () {
                expect(vm.startDate).toBeObservable();
            });

            it("defaults to '???'", function () {
                expect(vm.startDate()).toBe("???");
            });

            it("responds to the active-atlas-layer event", function () {
                var date = "date";
                ko.postbox.publish("active-atlas-layer", { type: "type", run: { "rangeStart": date }});
                expect(vm.startDate()).toBe(date);
            });
        });

        describe("holds a field for the 'endDate' of the selected model run's input data, which", function () {
            it("is observable", function () {
                expect(vm.endDate).toBeObservable();
            });

            it("defaults to '???'", function () {
                expect(vm.endDate()).toBe("???");
            });

            it("responds to the active-atlas-layer event", function () {
                var date = "date";
                ko.postbox.publish("active-atlas-layer", { type: "type", run: { "rangeEnd": date }});
                expect(vm.endDate()).toBe(date);
            });
        });
    });
});
