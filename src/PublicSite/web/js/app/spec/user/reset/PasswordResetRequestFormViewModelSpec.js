/* A suite of tests for the PasswordResetRequestFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/user/reset/PasswordResetRequestFormViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (PasswordResetRequestFormViewModel, BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'password reset request' form view model", function () {
        describe("has an 'email' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordResetRequestFormViewModel("");
                expect(vm.email).toBeObservable();
            });

            it("is starts empty", function () {
                var vm = new PasswordResetRequestFormViewModel("");
                expect(vm.email()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordResetRequestFormViewModel("");
                expect(vm.email).toHaveValidationRule({ name: "required", params: true });
                expect(vm.email).toHaveValidationRule({ name: "email", params: true });
                expect(vm.email).toHaveValidationRule({ name: "maxLength", params: 320 });
            });
        });

        describe("has the behavior of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            var baseUrl = "baseUrl/";
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseFormViewModel);
                    injector.mock("shared/app/BaseFormViewModel", baseSpy);

                    injector.require(["app/user/reset/PasswordResetRequestFormViewModel"],
                        function (PasswordResetRequestFormViewModel) {
                            vm = new PasswordResetRequestFormViewModel(baseUrl);
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("when created", function () {
                expect(baseSpy).toHaveBeenCalled();
            });

            it("it specifies the correct 'baseUrl'", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[2]).toBe(baseUrl);
            });

            it("it specifies the correct form url", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[3]).toBe("account/reset/request");
            });

            it("it specifies to not send JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0]).toBe(false);
            });

            it("it specifies to receive JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[1]).toBe(true);
            });

            it("it uses a custom success UI message", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[4].success)
                    .toBe("A message has been sent to you by email with instructions on how to reset your password.");
                expect(args[4].fail).toBeUndefined();
                expect(args[4].error).toBeUndefined();
            });

            it("it overrides the 'buildSubmissionData' function to generate the correct data object", function () {
                vm.email("email");
                var data = vm.buildSubmissionData();
                expect(data).toEqual({
                    email: "email"
                });
            });
        });
    });
});
