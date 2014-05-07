/* A suite of tests for the LogInViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/LogInViewModel"], function (LogInViewModel) {
    "use strict";

    describe("The log in view model", function () {
        var baseUrl = "foo";
        var refreshSpy;
        var vm = {};
        beforeEach(function () {
            refreshSpy = jasmine.createSpy();
            vm = new LogInViewModel(baseUrl, refreshSpy);
        });

        describe("holds a username which", function () {
            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.formUsername()).toBeUndefined();
            });
        });

        describe("holds a password which", function () {
            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.formUsername()).toBeUndefined();
            });
        });

        describe("has a submit method which", function () {
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

            it("refreshes the page on login success", function () {
                vm.submit();
                expect(refreshSpy).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                expect(refreshSpy).toHaveBeenCalled();
            });
        });

        describe("holds an alert text which", function () {
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
                    jasmine.Ajax.requests.mostRecent().response({ status: 401, responseText: xhrResponseText});
                    expect(vm.formAlert()).toBe(xhrResponseText);
                });
            });
        });
    });
});
