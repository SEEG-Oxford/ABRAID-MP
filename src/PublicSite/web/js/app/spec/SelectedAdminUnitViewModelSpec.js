/* A suite of tests for the SelectedAdminUnitViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/SelectedAdminUnitViewModel",
    "ko"
], function (SelectedAdminUnitViewModel, ko) {
    "use strict";

    describe("The 'selected admin unit' view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new SelectedAdminUnitViewModel();
        });

        describe("holds the list of admin units to be displayed in the table, which", function () {
            it("is an observable", function () {
                expect(vm.adminUnits).toBeObservable();
            });

            it("subscribes to the 'admin-units-to-be-reviewed' event by " +
               "updating its value when the event fires", function () {
                var featureArray = [ { properties : { name : "foo" } } ];
                expect(vm.adminUnits()).not.toBeDefined();
                ko.postbox.publish("admin-units-to-be-reviewed", { data : featureArray });
                expect(vm.adminUnits()).toEqual(featureArray);
            });

            it("is sorted alphabetically", function () {
                var featureA = { properties : { name : "a" }};
                var featureB = { properties : { name : "b" }};
                ko.postbox.publish("admin-units-to-be-reviewed", { data : [ featureB, featureA ] });
                expect(vm.adminUnits()).toEqual([ featureA, featureB ]);
            });
        });

        describe("holds the selected administrative unit which", function () {
            it("is an observable", function () {
                expect(vm.selectedAdminUnit).toBeObservable();
            });

            it("initially takes null", function () {
                expect(vm.selectedAdminUnit()).toBeNull();
            });

            it("subscribes to the 'admin-unit-selected' event by updating its value when the event fires", function () {
                var expectation = "foo";
                expect(vm.selectedAdminUnit()).toBeNull();
                ko.postbox.publish("admin-unit-selected", expectation);
                expect(vm.selectedAdminUnit()).toBe(expectation);
            });
        });

        describe("holds a boolean value which", function () {
            it("indicates whether an admin unit is selected", function () {
                vm.selectedAdminUnit(null);
                expect(vm.hasSelectedAdminUnit()).toBeFalsy();
                vm.selectedAdminUnit("foo");
                expect(vm.hasSelectedAdminUnit()).toBeTruthy();
            });
        });
    });
});