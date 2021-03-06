/* A suite of tests for the LogInFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/LogInFormViewModel"], function (LogInFormViewModel) {
    "use strict";

    describe("The log in view model", function () {
        var baseUrl = "foo";
        var refreshSpy;
        var rebindSpy;
        var vm = {};
        beforeEach(function () {
            refreshSpy = jasmine.createSpy();
            rebindSpy = jasmine.createSpy();
            vm = new LogInFormViewModel(baseUrl, refreshSpy, rebindSpy);
        });

        describe("holds a username which", function () {
            it("is an observable", function () {
                expect(vm.username).toBeObservable();
            });

            it("starts as empty string", function () {
                expect(vm.username()).toBe("");
            });
        });

        describe("holds a password which", function () {
            it("is an observable", function () {
                expect(vm.password).toBeObservable();
            });

            it("starts as empty string", function () {
                expect(vm.password()).toBe("");
            });
        });

        describe("has a submit method which", function () {
            it("POSTs to the spring security url, with the correct parameters", function () {
                // Arrange
                var expectedUrl = baseUrl + "j_spring_security_check";
                vm.username("username");
                vm.password("password");
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
                vm.username("");
                vm.password("");

                // Act
                vm.submit();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
            });

            it("forces a rebind of observables before POST is sent (firefox autofill workaround)", function () {
                // Arrange
                vm.username("username");
                vm.password("password");

                // Act
                vm.submit();

                // Assert
                expect(rebindSpy).toHaveBeenCalled();
            });

            it("refreshes the page on login success", function () {
                // Arrange
                vm.username("a");
                vm.password("b");

                // Act
                vm.submit();
                expect(refreshSpy).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });

                // Assert
                expect(refreshSpy).toHaveBeenCalled();
            });
        });

        describe("holds an 'isSubmitting' field", function () {
            it("is an observable", function () {
                expect(vm.isSubmitting).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isSubmitting()).toBe(false);
            });

            it("is updated during submit", function () {
                // Arrange
                vm.username("b");
                vm.password("a");

                // Act
                vm.submit();

                // Assert
                expect(vm.isSubmitting()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
            });

            it("is updated after submit", function () {
                // Arrange
                vm.username("b");
                vm.password("a");

                // Act
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });

                // Assert
                expect(vm.isSubmitting()).toBe(false);
            });
        });

        describe("holds a message field which", function () {
            it("starts blank", function () {
                expect(vm.message()).toBe("");
            });

            it("is updated during submit", function () {
                // Arrange
                vm.username("b");
                vm.password("a");

                // Act
                vm.submit();

                // Assert
                expect(vm.message()).toContain("Attempting login");
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
            });

            it("is updated after a successful submit", function () {
                // Arrange
                vm.username("a");
                vm.password("b");

                // Act
                vm.submit();

                // Assert
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                expect(vm.message()).toContain("Success");
            });

            describe("is updated on an unsuccessful submit", function () {
                it("due to a missing username", function () {
                    // Arrange
                    vm.username("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.message()).toContain("Username &amp; password required");
                });

                it("due to a missing password", function () {
                    // Arrange
                    vm.password("");
                    // Act
                    vm.submit();
                    // Assert
                    expect(vm.message()).toContain("Username &amp; password required");
                });

                it("due to an unauthorised login attempt", function () {
                    // Arrange
                    var xhrResponseText = "foo";
                    vm.username("a");
                    vm.password("b");

                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ status: 401, responseText: xhrResponseText});

                    // Assert
                    expect(vm.message()).toContain(xhrResponseText);
                });
            });
        });
    });
});
