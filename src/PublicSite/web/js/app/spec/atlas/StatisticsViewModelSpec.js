/* A suite of tests for the StatisticsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/StatisticsViewModel"
], function (ko, StatisticsViewModel) {
    "use strict";

    describe("The 'StatisticsViewModel", function () {
        describe("holds the statistics object which", function () {
            var baseUrl = "/";
            var vm = new StatisticsViewModel();

            it("is observable", function () {
                expect(vm.statistics).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.statistics()).toBe({});
            });

            it("GETs the statistics when the 'selected-run' event is fired", function () {
                // Arrange
                var modelRunId = "modelRunId";
                var expectedUrl = baseUrl + "atlas/details/modelrun/" + modelRunId + "/statistics";
                // Act
                ko.postbox.publish("selected-run", { id: modelRunId });
                // Assert
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
            });
        });
    });
});