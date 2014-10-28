/* A suite of tests for the CovariateInfluencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/CovariateInfluencesViewModel"
], function (ko, CovariateInfluencesViewModel) {
    "use strict";

    describe("The 'CovariateInfluencesViewModel", function () {
        describe("holds the list of covariate influences which", function () {
            var vm = new CovariateInfluencesViewModel();
            it("is observable", function () {
                expect(vm.covariateInfluences).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.covariateInfluences()).toBe([]);
            });

            it("updates its values on the 'selected-run' event", function () {
                // Arrange
                var covariates = {};
                // Act
                ko.postbox.publish("selected-run", { covariates: covariates });
                // Assert
                expect(vm.covariateInfluences()).toBe(covariates);
            });
        });
    });
});