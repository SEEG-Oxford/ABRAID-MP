/* A suite of tests for the SelectedPointViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/datavalidation/SelectedPointViewModel",
    "app/datavalidation/CounterViewModel",
    "ko"
], function (SelectedPointViewModel, CounterViewModel, ko) {
    "use strict";

    describe("The 'selected point' view model", function () {

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
        var initialCount = 0;
        var eventName = "occurrence-reviewed";
        beforeEach(function () {
            vm = new SelectedPointViewModel(baseUrl, function () {}, new CounterViewModel(initialCount, eventName));
        });

        describe("holds the disease occurrence counter view model which", function () {
            it("takes the expected initial count", function () {
                expect(vm.counter).not.toBeNull();
                expect(vm.counter.count()).toBe(initialCount);
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

        describe("holds an 'submitting' boolean value which", function () {
            it("indicates whether a review is being submitted", function () {
                expect(vm.submitting).toBeObservable();
                expect(vm.submitting()).toBe(false);
                vm.selectedPoint(feature);
                expect(vm.submitting()).toBe(false);
                vm.submitReview();
                expect(vm.submitting()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                expect(vm.submitting()).toBe(false);
                vm.selectedPoint(feature);
                expect(vm.submitting()).toBe(false);
                vm.submitReview();
                expect(vm.submitting()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ status: 400 });
                expect(vm.submitting()).toBe(false);
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
                ko.postbox.publish("layers-changed", { diseaseId: diseaseId });
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
                var spy = jasmine.createSpy();
                vm = new SelectedPointViewModel(baseUrl, spy, new CounterViewModel(initialCount, eventName));
                vm.selectedPoint(feature);

                var message = "Something went wrong. Please try again.";
                // Act
                vm.submitReview("foo");
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(spy).toHaveBeenCalledWith(message);
            });

            describe("when successful,", function () {
                it("fires the 'occurrence-reviewed' event", function () {
                    // Arrange
                    var expectation = vm.selectedPoint().id;
                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("occurrence-reviewed", function (value) {
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
