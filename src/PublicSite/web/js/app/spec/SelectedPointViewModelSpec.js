/* A suite of tests for the SelectedLayerViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
/*global alert:true*/
define([
    "app/SelectedPointViewModel",
    "ko"
], function (SelectedPointViewModel, ko) {
    "use strict";

    describe("The selected point view model", function () {

        var feature = {
            "type": "Feature",
            "id": 1,
            "geometry": {
                "type": "Point",
                "coordinates": [
                    -53.08982,
                    -10.77311
                ]
            },
            "properties": {
                "diseaseGroupPublicName": "Dengue",
                "locationName": "Brazil",
                "alert": {
                    "title": "PRO/EDR> Dengue/DHF update (16): Americas",
                    "summary": null,
                    "url": "http://promedmail.org/direct.php?id=20140224.2297942",
                    "feedName": "ProMED Mail"
                },
                "occurrenceDate": "2014-02-25T02:22:21.000Z"
            }
        };
        var baseUrl = "";
        var vm = {};
        beforeEach(function () {
            vm = new SelectedPointViewModel(baseUrl);
        });

        describe("holds the selected disease occurrence point which", function () {
            it("is an observable", function () {
                expect(vm.selectedPoint).toBeObservable();
            });

            it("is initially null", function () {
                expect(vm.selectedPoint()).toBe(null);
            });

            describe("syncs with the 'point-selected' event by", function () {
                it("changing its value when the event is fired externally", function () {
                    // Arrange
                    vm.selectedPoint(null);
                    // Act
                    ko.postbox.publish("point-selected", feature);
                    // Assert
                    expect(vm.selectedPoint()).toBe(feature);
                });

                it("firing the event when its value changes", function () {
                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                        expect(value).toBe(feature);
                    });
                    // Act
                    vm.selectedPoint(feature);
                    subscription.dispose();
                });
            });
        });

        describe("holds the translation URL which", function () {
            it("contains the relevant components", function () {
                vm.selectedPoint(feature);
                expect(vm.translationUrl()).toContain("http://translate.google.com/?");
                expect(vm.translationUrl()).toContain("langpair=auto|auto");
                expect(vm.translationUrl()).toContain("text=");
            });

            it("is truncated to the correct length", function () {
                // Arrange
                feature.properties.alert.summary = "This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated. This summary is too long and should be truncated."; // jshint ignore:line
                // Act
                vm.selectedPoint(feature);
                // Assert
                expect(feature.properties.alert.summary.length < 2048).toBeFalsy();
                expect(vm.translationUrl.length < 2048).toBeTruthy();
            });
        });

        describe("has a submit method which", function () {
            beforeEach(function () {
                vm.selectedPoint(feature);
            });

            it("POSTs to the specified URL, with the correct parameters", function () {
                // Arrange
                var diseaseId = 1;
                ko.postbox.publish("layers-changed", {diseaseSet : { id: diseaseId}});
                var occurrenceId = vm.selectedPoint().id;
                var expectedUrl = baseUrl + "datavalidation/diseases/" + diseaseId + "/occurrences/" + occurrenceId +
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
                alert = jasmine.createSpy();
                var message = "Something went wrong. Please try again.";
                // Act
                vm.submitReview("foo");
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(alert).toHaveBeenCalledWith(message);
            });

            describe("when successful,", function () {
                it("fires the 'point-reviewed' event", function () {
                    // Arrange
                    var expectation = vm.selectedPoint().id;
                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("point-reviewed", function (value) {
                        expect(value).toBe(expectation);
                    });
                    // Act
                    vm.submitReview("foo");
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    subscription.dispose();
                });

                it("resets the selected point to null", function () {
                    expect(vm.selectedPoint()).not.toBeNull();
                    // Act
                    vm.submitReview("foo");
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    // Assert
                    expect(vm.selectedPoint()).toBeNull();
                });
            });
        });
    });
});
