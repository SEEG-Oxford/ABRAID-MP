/* A suite of tests for the AuthViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/AuthViewModel"], function (AuthViewModel) {
    "use strict";

    describe("The auth view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new AuthViewModel("foo");
        });

        describe("holds a username which", function () {
            it("starts empty", function () {
                expect(vm.username()).toBeUndefined();
            });

            it("is required", function () {
                expect(vm.username).toHaveValidationRule({name: "required", params: true});
            });

            it("has the minimum username complexity enforced", function () {
                expect(vm.username).toHaveValidationRule({name: "usernameComplexity", params: true});
            });
        });

        describe("holds a password which", function () {
            it("starts empty", function () {
                expect(vm.password()).toBeUndefined();
            });

            it("is required", function () {
                expect(vm.password).toHaveValidationRule({name: "required", params: true});
            });

            it("has the minimum password complexity enforced", function () {
                expect(vm.password).toHaveValidationRule({name: "passwordComplexity", params: true});
            });
        });

        describe("holds a password confirmation which", function () {
            it("starts empty", function () {
                expect(vm.passwordConfirmation()).toBeUndefined();
            });

            it("is required", function () {
                expect(vm.passwordConfirmation).toHaveValidationRule({name: "required", params: true});
            });

            it("has the minimum password complexity enforced", function () {
                expect(vm.passwordConfirmation).toHaveValidationRule({name: "passwordComplexity", params: true});
            });

            it("must match the other password", function () {
                expect(vm.passwordConfirmation).toHaveValidationRule({name: "areSame", params: vm.password});
            });
        });
    });
});