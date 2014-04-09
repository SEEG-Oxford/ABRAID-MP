/* A suite of tests for the AuthViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false*/
define([ 'app/AuthViewModel', 'underscore', 'ko' ], function(AuthViewModel, _, ko) {
    "use strict";

    describe("The auth view model", function() {
        var vm = {};
        var initVM = function(makeValidatable) {
            return function() {
                if (!makeValidatable) {
                    vm = new AuthViewModel("foo");
                } else {
                    vm = ko.validatedObservable(new AuthViewModel("foo"));
                }
            };
        };

        describe("holds a username which", function() {
            beforeEach(initVM(false));

            it("starts empty", function() {
                expect(vm.username()).toBeUndefined();
            });

            it("is required", function() {
                expect(_(vm.username.rules()).pluck('rule')).toContain('required');
            });

            it("has the minimum username complexity enforced", function() {
                expect(_(vm.username.rules()).pluck('rule')).toContain('usernameComplexity');
            });
        });

        describe("holds a password which", function() {
            beforeEach(initVM(false));

            it("starts empty", function() {
                expect(vm.password()).toBeUndefined();
            });

            it("is required", function() {
                expect(_(vm.password.rules()).pluck('rule')).toContain('required');
            });

            it("has the minimum password complexity enforced", function() {
                expect(_(vm.password.rules()).pluck('rule')).toContain('passwordComplexity');
            });
        });

        describe("holds a password confirmation which", function() {
            beforeEach(initVM(false));

            it("starts empty", function() {
                expect(vm.passwordConfirmation()).toBeUndefined();
            });

            it("is required", function() {
                expect(_(vm.passwordConfirmation.rules()).pluck('rule')).toContain('required');
            });

            it("has the minimum password complexity enforced", function() {
                expect(_(vm.passwordConfirmation.rules()).pluck('rule')).toContain('passwordComplexity');
            });

            it("must match the other password", function() {
                expect(_(vm.passwordConfirmation.rules()).pluck('rule')).toContain('areSame');
                expect(_(vm.passwordConfirmation.rules()).findWhere({ rule: "areSame" }).params).toBe(vm.password);
            });
        });


    });
});