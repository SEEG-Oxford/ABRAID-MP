/* A suite of tests for the ModelRunDetailsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/ModelRunDetailsViewModel"
], function (ko, ModelRunDetailsViewModel) {
    "use strict";

    describe("The 'ModelRunDetailsViewModel", function () {
        it("holds the three sub-view-models", function () {
            // Act
            var vm = new ModelRunDetailsViewModel("covariateInfluences", "downloadLinks", "statistics");
            // Assert
            expect(vm.covariateInfluencesViewModel).toBe("covariateInfluences");
            expect(vm.downloadLinksViewModel).toBe("downloadLinks");
            expect(vm.statisticsViewModel).toBe("statistics");
        });

        describe("holds the current layer, which", function () {
            // Arrange
            var vm = new ModelRunDetailsViewModel({}, {}, {});

            it("is observable", function () {
                expect(vm.activeLayer).toBeObservable();
            });

            it("reacts to the 'active-atlas-layer' event", function () {
                // Arrange
                var payload = {};
                // Act
                expect(vm.activeLayer()).toBeUndefined();
                ko.postbox.publish("active-atlas-layer", payload);
                // Assert
                expect(vm.activeLayer()).toBe(payload);
            });
        });

        describe("holds the show covariate table field which", function () {
            var vm = new ModelRunDetailsViewModel({}, {}, {});

            it("is observable", function () {
                expect(vm.showCovariateTable).toBeObservable();
            });

            it("starts true", function () {
                expect(vm.showCovariateTable()).toBe(true);
            });
        });
    });
});
