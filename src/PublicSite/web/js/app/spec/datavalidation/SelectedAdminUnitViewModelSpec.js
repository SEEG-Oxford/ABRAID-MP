/* A suite of tests for the SelectedAdminUnitViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/datavalidation/SelectedAdminUnitViewModel",
    "app/datavalidation/CounterViewModel",
    "ko"
], function (SelectedAdminUnitViewModel, CounterViewModel, ko) {
    "use strict";

    describe("The 'selected admin unit' view model", function () {
        var adminUnit = { id: 1013965, name: "South Africa", count: 10 };
        var vm = {};
        var baseUrl = "";
        var initialCount = 0;
        var eventName = "admin-unit-reviewed";
        beforeEach(function () {
            vm = new SelectedAdminUnitViewModel(baseUrl, function () {}, new CounterViewModel(initialCount, eventName));
        });

        describe("holds the disease occurrence counter view model which", function () {
            it("takes the expected initial value", function () {
                expect(vm.counter).not.toBeNull();
                expect(vm.counter.count()).toEqual(0);
            });

            it("increments its value when the event is fired", function () {
                // Arrange
                // Act
                ko.postbox.publish(eventName);
                // Assert
                var expectedCount = initialCount + 1;
                expect(vm.counter.count()).toBe(expectedCount);
            });
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
                var featureA = { name : "a" };
                var featureB = { name : "b" };
                ko.postbox.publish("admin-units-to-be-reviewed", { data : [ featureB, featureA ] });
                expect(vm.adminUnits()).toEqual([ featureA, featureB ]);
            });
        });

        describe("holds the number of admin units in the table, which", function () {
            it("responds to changes in the list of admin units", function () {
                vm.adminUnits([]);
                expect(vm.adminUnitsCount()).toEqual(0);

                vm.adminUnits([ 1, 2, 3 ]);
                expect(vm.adminUnitsCount()).toEqual(3);

                vm.adminUnits([ { name: "a" }, { name: "b" }, { name: "c" }, { name: "d" }, { name: "e" } ]);
                expect(vm.adminUnitsCount()).toEqual(5);

                vm.adminUnits([ wrap({ name: "a" }), wrap({ name: "b" }) ]);
                expect(vm.adminUnitsCount()).toEqual(2);
            });
        });

        function wrap() {
            return function (arg) { return arg; };
        }

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

        describe("has a submit review method which", function () {
            beforeEach(function () {
                vm.selectedAdminUnit(adminUnit);
            });

            it("POSTs to the specified URL, with the correct parameters", function () {
                // Arrange
                var diseaseId = 1;
                ko.postbox.publish("layers-changed", { diseaseId: diseaseId });
                var gaulCode = vm.selectedAdminUnit().id;
                var expectedUrl = baseUrl + "datavalidation/diseases/" + diseaseId + "/adminunits/" + gaulCode +
                    "/validate";
                var review = "foo";
                var expectedParams = "review=" + review;

                // Act
                vm.submitReview(review);

                // Assert
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });

            it("when unsuccessful, displays an alert", function () {
                // Arrange
                var spy = jasmine.createSpy();
                vm = new SelectedAdminUnitViewModel(baseUrl, spy, new CounterViewModel(initialCount, eventName));
                vm.selectedAdminUnit(adminUnit);
                var message = "Something went wrong. Please try again.";

                // Act
                vm.submitReview("foo");
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });

                // Assert
                expect(spy).toHaveBeenCalledWith(message);
            });

            describe("when successful", function () {
                it("fires the 'admin-unit-reviewed' event", function () {
                    // Arrange
                    var expectation = vm.selectedAdminUnit().id;
                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("admin-unit-reviewed", function (value) {
                        expect(value).toBe(expectation);
                    });
                    // Act
                    vm.submitReview("foo");
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    subscription.dispose();
                });

                it("resets the selected admin unit to null", function () {
                    expect(vm.selectedAdminUnit()).not.toBeNull();
                    // Act
                    vm.submitReview("foo");
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    // Assert
                    expect(vm.selectedAdminUnit()).toBeNull();
                });

                it("removes the admin unit from the table", function () {
                    // Arrange
                    var adminUnit = {id: "gaulCode"};
                    vm.adminUnits([adminUnit]);
                    vm.selectedAdminUnit(adminUnit);
                    // Act
                    vm.submitReview("review");
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    // Assert
                    expect(vm.selectedAdminUnit()).toBeNull();
                    expect(vm.adminUnits()).not.toContain(adminUnit);
                });
            });
        });
    });
});