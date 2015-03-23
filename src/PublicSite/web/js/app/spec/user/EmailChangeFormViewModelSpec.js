/* A suite of tests for the EmailChangeFormViewModel.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "app/user/EmailChangeFormViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (EmailChangeFormViewModel, BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'email change' form view model", function () {
        var baseUrl = "baseUrl/";

        describe("has an 'email' field, which ", function () {
            it("is an observable", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "");
                expect(vm.email).toBeObservable();
            });

            it("starts with the value of a constructor argument", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "abc");
                expect(vm.email()).toBe("abc");
            });

            it("is validated appropriately", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "initialEmail");
                expect(vm.email).toHaveValidationRule({ name: "required", params: true });
                expect(vm.email).toHaveValidationRule({ name: "email", params: true });
                expect(vm.email).toHaveValidationRule({ name: "maxLength", params: 320 });
                expect(vm.email).toHaveValidationRule({ name: "emailChanged", params: "initialEmail" });
            });
        });

        describe("has a 'password' field, which ", function () {
            it("is an observable", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "");
                expect(vm.password).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "");
                expect(vm.password()).toBe("");
            });

            it("is validated appropriately", function () {
                var vm = new EmailChangeFormViewModel(baseUrl, "");
                expect(vm.password).toHaveValidationRule({ name: "required", params: true });
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

                    injector.require(["app/user/EmailChangeFormViewModel"],
                        function (EmailChangeFormViewModel) {
                            vm = new EmailChangeFormViewModel(baseUrl, "email");
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
                expect(args[3]).toBe("account/email");
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
                vm.email("email");
                vm.password("password");

                var data = vm.buildSubmissionData();
                expect(data).toEqual({
                    email: "email",
                    password: "password"
                });
            });
        });
    });
});
