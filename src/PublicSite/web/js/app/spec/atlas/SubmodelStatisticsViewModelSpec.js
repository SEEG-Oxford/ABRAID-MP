/* A suite of tests for the SubmodelStatisticsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/SubmodelStatisticsViewModel"
], function (ko, SubmodelStatisticsViewModel) {
    "use strict";

    describe("The 'SubmodelStatisticsViewModel", function () {
        describe("holds the submodel statistics object which", function () {
            var vm = new SubmodelStatisticsViewModel();
            it("is observable", function () {
                expect(vm.statistics).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.statistics()).toBe({});
            });

            it("updates its values on the 'selected-run' event", function () {
                // Arrange
                var statistics = {};
                // Act
                ko.postbox.publish("selected-run", { statistics: statistics });
                // Assert
                expect(vm.statistics()).toBe(statistics);
            });
        });
    });
});