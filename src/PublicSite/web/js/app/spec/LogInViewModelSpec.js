/* A suite of tests for the LogInViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/LogInViewModel",
    "ko",
    "app/spec/util/observableMatcher"
], function (LogInViewModel, ko, observableMatcher) {
    "use strict";

    describe("The log in view model", function () {
        var baseUrl = "foo";
        var vm;
        var setUpTest = function () {
            jasmine.addMatchers({ toBeObservable: observableMatcher });
            vm = new LogInViewModel(baseUrl);
            jasmine.Ajax.install();
        };
        var tearDownTest = function () {
            jasmine.Ajax.uninstall();
        };

        describe("holds a username which", function () {
            beforeEach(setUpTest);
            afterEach(tearDownTest);

            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.formUsername()).toBeUndefined();
            });
        });

        describe("holds a password which", function () {
            beforeEach(setUpTest);
            afterEach(tearDownTest);

            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.formUsername()).toBeUndefined();
            });
        });

        describe("has a submit method which", function () {
            beforeEach(setUpTest);
            afterEach(tearDownTest);

            it("POSTs to the spring security url, with the correct parameters", function () {
                // Arrange
                var expectedUrl = baseUrl + "j_spring_security_check";
                vm.formUsername("username");
                vm.formPassword("password");
                var expectedParams = "j_username=username&j_password=password";

                // Act
                vm.submit();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });
        });

        describe("holds an alert text which", function () {
            beforeEach(setUpTest);
            afterEach(tearDownTest);

            it("starts blank", function () {
                expect(vm.formAlert()).toBe(" ");
            });

            describe("is updated on an unsuccessful submit", function () {

                it("due to a missing username", function () {
                    // Arrange
                    vm.formUsername("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.formAlert()).toBe("Enter username and/or password");
                });

                it("due to a missing password", function () {
                    // Arrange
                    vm.formPassword("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.formAlert()).toBe("Enter username and/or password");
                });

                it("due to an unauthorised login attempt", function () {
                    var xhrResponseText = "foo";
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ status: 401, xhr: {responseText: xhrResponseText}});
                    expect(vm.formAlert()).toBe(xhrResponseText);
                });
            });
        });
    });
});