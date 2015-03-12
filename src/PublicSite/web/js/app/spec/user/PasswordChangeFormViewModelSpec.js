/* A suite of tests for the PasswordChangeFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/user/PasswordChangeFormViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (PasswordChangeFormViewModel, BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'password change' form view model", function () {
        var baseUrl = "baseUrl/";

        describe("has an 'old password' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.oldPassword).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.oldPassword()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.oldPassword).toHaveValidationRule({ name: "required", params: true });
            });
        });

        describe("has an 'new password' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.newPassword).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.newPassword()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.newPassword).toHaveValidationRule({ name: "required", params: true });
                expect(vm.newPassword).toHaveValidationRule({ name: "passwordComplexity", params: true });
            });
        });

        describe("has an 'confirm password' field, which ", function () {
            it("is an observable", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.confirmPassword).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.confirmPassword()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new PasswordChangeFormViewModel(baseUrl);
                expect(vm.confirmPassword).toHaveValidationRule({ name: "required", params: true });
                expect(vm.confirmPassword).toHaveValidationRule({ name: "passwordComplexity", params: true });
                expect(vm.confirmPassword).toHaveValidationRule({ name: "areSame", params: vm.newPassword });
            });
        });

        describe("has the behaviour of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseFormViewModel);
                    injector.mock("shared/app/BaseFormViewModel", baseSpy);

                    injector.require(["app/user/PasswordChangeFormViewModel"],
                        function (PasswordChangeFormViewModel) {
                            vm = new PasswordChangeFormViewModel(baseUrl);
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
                expect(args[3]).toBe("account/password");
            });

            it("it specifies to not send JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0]).toBe(false);
            });

            it("it specifies to receive JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[1]).toBe(true);
            });

            it("it uses the standard UI messages", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[4]).toBeUndefined();
            });

            it("it does not exclude the generic failure messages", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[5]).toBeUndefined();
            });

            it("it specifies to prevent form resubmission", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[6]).toBe(true);
            });

            it("it overrides the 'buildSubmissionData' function to generate the correct data object", function () {
                vm.oldPassword("oldPassword");
                vm.newPassword("newPassword");
                vm.confirmPassword("confirmPassword");

                var data = vm.buildSubmissionData();
                expect(data).toEqual({
                    oldPassword: "oldPassword",
                    newPassword: "newPassword",
                    confirmPassword: "confirmPassword"
                });
            });
        });
    });
});
