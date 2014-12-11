/* A suite of tests for the PasswordResetProcessFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/user/reset/PasswordResetProcessFormViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (PasswordResetProcessFormViewModel, BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'password reset process' form view model", function () {
        describe("has a 'password' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.newPassword).toBeObservable();
            });

            it("is starts empty", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.newPassword()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.newPassword).toHaveValidationRule({ name: "required", params: true });
                expect(vm.newPassword).toHaveValidationRule({ name: "passwordComplexity", params: true });
            });
        });

        describe("has a 'password confirmation' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.confirmPassword).toBeObservable();
            });

            it("is starts empty", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.confirmPassword()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordResetProcessFormViewModel("", 0, "");
                expect(vm.confirmPassword).toHaveValidationRule({ name: "required", params: true });
                expect(vm.confirmPassword).toHaveValidationRule({ name: "passwordComplexity", params: true });
                expect(vm.confirmPassword).toHaveValidationRule({ name: "areSame", params: vm.newPassword });
            });
        });

        describe("has the behavior of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            var id = 7;
            var key = "theKey";
            var baseUrl = "baseUrl/";
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseFormViewModel);
                    injector.mock("shared/app/BaseFormViewModel", baseSpy);

                    injector.require(["app/user/reset/PasswordResetProcessFormViewModel"],
                        function (PasswordResetProcessFormViewModel) {
                            vm = new PasswordResetProcessFormViewModel(baseUrl, id, key);
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
                expect(args[3]).toBe("account/reset/process");
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
                expect(args[4].success).toBe("Password updated successfully");
                expect(args[4].fail).toBeUndefined();
                expect(args[4].error).toBeUndefined();
            });

            it("it overrides the 'buildSubmissionData' function to generate the correct data object", function () {
                vm.newPassword("newPassword");
                vm.confirmPassword("confirmPassword");
                var data = vm.buildSubmissionData();
                expect(data).toEqual({
                    id: id,
                    key: key,
                    newPassword: "newPassword",
                    confirmPassword: "confirmPassword"
                });
            });
        });
    });
});
