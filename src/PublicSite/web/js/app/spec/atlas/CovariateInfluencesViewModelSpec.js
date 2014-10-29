/* A suite of tests for the CovariateInfluencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/CovariateInfluencesViewModel"
], function (ko, CovariateInfluencesViewModel) {
    "use strict";

    describe("The Covariate Influences View Model", function () {
        describe("holds the list of covariate influences which", function () {
            it("is observable", function () {
                var vm = new CovariateInfluencesViewModel("");
                expect(vm.covariateInfluences).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new CovariateInfluencesViewModel("");
                expect(vm.covariateInfluences()).toEqual([]);
            });

            describe("is updated", function () {
                var modelRunId = "modelRunId";
                var baseUrl = "/";
                var vm;
                beforeEach(function () {
                    // Clear postbox subscriptions (prevents test from bleeding into each other).
                    ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line

                    vm = new CovariateInfluencesViewModel(baseUrl);
                });

                it("with a GET request when the 'selected-run' event is fired", function () {
                    // Arrange
                    var expectedUrl = "/atlas/details/modelrun/" + modelRunId + "/covariates";
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                });

                it("when successful", function () {
                    // Arrange
                    var expectation = [1, 2, 3];
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        "contentType": "application/json",
                        "responseText": JSON.stringify(expectation)
                    });
                    // Assert
                    expect(vm.covariateInfluences()).toEqual(expectation);
                });

                it("when unsuccessful", function () {
                    // Act
                    ko.postbox.publish("selected-run", { id: modelRunId });
                    jasmine.Ajax.requests.mostRecent().response({ status: 400 });
                    // Assert
                    expect(vm.covariateInfluences()).toEqual([]);
                });
            });
        });
    });
});