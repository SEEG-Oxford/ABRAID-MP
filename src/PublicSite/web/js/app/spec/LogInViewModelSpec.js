/* A suite of tests for the LogInViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/LogInViewModel"], function (LogInViewModel) {
    "use strict";

    describe("The log in view model", function () {
        var baseUrl = "foo";
        var refreshSpy;
        var rebindSpy;
        var vm = {};
        beforeEach(function () {
            refreshSpy = jasmine.createSpy();
            rebindSpy = jasmine.createSpy();
            vm = new LogInViewModel(baseUrl, refreshSpy, rebindSpy);
        });

        describe("holds a username which", function () {
            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts as empty string", function () {
                expect(vm.formUsername()).toBe("");
            });
        });

        describe("holds a password which", function () {
            it("is an observable", function () {
                expect(vm.formUsername).toBeObservable();
            });

            it("starts as empty string", function () {
                expect(vm.formUsername()).toBe("");
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

            it("does not POST if username and password are not provided", function () {
                // Arrange
                vm.formUsername("");
                vm.formPassword("");

                // Act
                vm.submit();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
            });

            it("forces a rebind of observables before POST is sent (firefox autofill workaround)", function () {
                // Arrange
                vm.formUsername("username");
                vm.formPassword("password");

                // Act
                vm.submit();

                // Assert
                expect(rebindSpy).toHaveBeenCalled();
            });

            it("refreshes the page on login success", function () {
                // Arrange
                vm.formUsername("a");
                vm.formPassword("b");

                // Act
                vm.submit();
                expect(refreshSpy).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });

                // Assert
                expect(refreshSpy).toHaveBeenCalled();
            });
        });

        describe("holds a 'submitting' field", function () {
            it("starts false", function () {
                expect(vm.formAlert()).toBe("Log in via ABRAID account");
            });

            it("is set to ", function () {
                expect(vm.formAlert()).toBe("Log in via ABRAID account");
            });
        });

        describe("holds an alert text which", function () {
            it("starts with a welcome message", function () {
                expect(vm.formAlert()).toBe("Log in via ABRAID account");
            });

            it("is updated during submit", function () {
                // Arrange
                vm.formUsername("b");
                vm.formPassword("a");

                // Act
                vm.submit();

                // Assert
                expect(vm.formAlert()).toContain("Attempting  login");
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
            });

            it("is updated after a successful submit", function () {
                // Arrange
                vm.formPassword("a");
                vm.formUsername("b");

                // Act
                vm.submit();

                // Assert
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                expect(vm.formAlert()).toContain("Success");
            });

            describe("is updated on an unsuccessful submit", function () {
                it("due to a missing username", function () {
                    // Arrange
                    vm.formUsername("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.formAlert()).toContain("Username &amp; password required");
                });

                it("due to a missing password", function () {
                    // Arrange
                    vm.formPassword("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.formAlert()).toContain("Username &amp; password required");
                });

                it("due to an unauthorised login attempt", function () {
                    // Arrange
                    var xhrResponseText = "foo";
                    vm.formPassword("a");
                    vm.formUsername("b");

                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ status: 401, responseText: xhrResponseText});

                    // Assert
                    expect(vm.formAlert()).toContain(xhrResponseText);
                });
            });
        });
    });
});
