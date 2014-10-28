/* A suite of tests for the AtlasInformationViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/AtlasInformationViewModel"
], function (ko, AtlasInformationViewModel) {
    "use strict";

    describe("The 'AtlasInformationViewModel", function () {
        it("holds the three sub-view-models", function () {
            // Act
            var vm = new AtlasInformationViewModel("covariateInfluences", "downloadLinks", "submodelStatistics");
            // Assert
            expect(vm.covariateInfluencesViewModel).toBe("covariateInfluences");
            expect(vm.downloadLinksViewModel).toBe("downloadLinks");
            expect(vm.submodelStatisticsViewModel).toBe("submodelStatistics");
        });

        describe("holds the current layer, which", function () {
            // Arrange
            var vm = new AtlasInformationViewModel({}, {}, {});

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
    });
});