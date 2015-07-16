/* A suite of tests for the CovariateInfluencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/CovariateInfluencesViewModel"
], function (ko, CovariateInfluencesViewModel) {
    "use strict";

    describe("The Covariate Influences View Model", function () {
        var baseUrl = "/";
        var vm;
        beforeEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            vm = new CovariateInfluencesViewModel(baseUrl);
        });

        afterEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
        });

        describe("holds the effect curve covariate influences link which", function () {
            it("is observable", function () {
                expect(vm.effectCurvesLink).toBeObservable();
            });

            it("starts empty with '#'", function () {
                expect(vm.effectCurvesLink()).toEqual("#");
            });

            it("is updated when the 'selected-run' event is fired", function () {
                // Arrange
                var modelRunId = "abc";
                var expected = baseUrl + "atlas/details/modelrun/" + modelRunId + "/effectcurves.csv";
                // Act
                ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                // Assert
                expect(vm.effectCurvesLink()).toEqual(expected);
            });
        });

        describe("holds the list of covariate influences which", function () {
            it("is observable", function () {
                expect(vm.covariateInfluences).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.covariateInfluences()).toBeUndefined();
            });

            describe("is updated", function () {
                var modelRunId = "modelRunId";

                it("with a GET request when the 'active-atlas-layer' event is fired", function () {
                    // Arrange
                    var expectedUrl = "/atlas/details/modelrun/" + modelRunId + "/covariates";
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                });

                it("when successful", function () {
                    // Arrange
                    var expectation = [1, 2, 3];
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        "contentType": "application/json",
                        "responseText": JSON.stringify(expectation)
                    });
                    // Assert
                    expect(vm.covariateInfluences()).toEqual(expectation);
                });

                it("when unsuccessful, to empty", function () {
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    jasmine.Ajax.requests.mostRecent().response({ status: 400 });
                    // Assert
                    expect(vm.covariateInfluences()).toBeUndefined();
                });
            });
        });
    });
});
