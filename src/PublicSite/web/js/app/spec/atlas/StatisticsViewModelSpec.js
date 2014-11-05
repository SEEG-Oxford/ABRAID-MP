/* A suite of tests for the StatisticsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/StatisticsViewModel"
], function (ko, StatisticsViewModel) {
    "use strict";

    describe("The Statistics View Model", function () {
        describe("holds the statistics object which", function () {
            it("is observable", function () {
                var vm = new StatisticsViewModel("");
                expect(vm.statistics).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new StatisticsViewModel("");
                expect(vm.statistics()).toBeUndefined();
            });

            describe("is updated", function () {
                var baseUrl = "/";
                var modelRunId = "modelRunId";
                var vm;
                beforeEach(function () {
                    // Clear postbox subscriptions (prevents test from bleeding into each other).
                    ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line

                    vm = new StatisticsViewModel(baseUrl);
                });

                it("with a GET request when the 'selected-run' event is fired", function () {
                    // Arrange
                    var expectedUrl = "/atlas/details/modelrun/" + modelRunId + "/statistics";
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                });

                it("when successful", function () {
                    // Arrange
                    var expectation = { foo: "bar" };
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        "contentType": "application/json",
                        "responseText": JSON.stringify(expectation)
                    });
                    // Assert
                    expect(vm.statistics()).toEqual(expectation);
                });

                it("when unsuccessful, to empty", function () {
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    jasmine.Ajax.requests.mostRecent().response({ status: 400 });
                    // Assert
                    expect(vm.statistics()).toBeUndefined();
                });
            });
        });
    });
});