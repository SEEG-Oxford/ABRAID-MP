/* A suite of tests for the AuthViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false, jasmine:false*/
define([ 'app/AuthViewModel', 'underscore', 'ko' ], function(AuthViewModel, _, ko) {
    "use strict";

    var customMatchers = {
        toHaveValidationRule: function(util, customEqualityTesters) {
            return {
                compare: function(actual, expected) {

                    if (expected === undefined) {
                        expected = {};
                    }
                    if (expected.name === undefined) {
                        expected.name = '';
                    }
                    if (expected.params === undefined) {
                        expected.name = null;
                    }

                    var result = {};

                    var matchingRules = _(actual.rules()).where({ rule: expected.name });
                    if (matchingRules.length === 1) {
                        if(util.equals(matchingRules[0].params, expected.params, customEqualityTesters)) {
                            result.pass = true;
                        } else {
                            result.pass = false;
                            result.message = "Expected rule '" + expected.name + "' was present but the parameter was '" + matchingRules[0].params + "' instead of the expected '" + expected.params + "'.";
                        }
                    } else {
                        result.pass = false;
                        result.message = "Expected rule '" + expected.name + "' be present but it was not in: [" + _(actual.rules()).pluck('rule') + "].";
                    }

                    return result;
                }
            };
        }
    };

    describe("The auth view model", function() {
        var vm = {};
        var initVM = function(makeValidatable) {
            return function() {
                jasmine.addMatchers(customMatchers);

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
                jasmine.addMatchers(customMatchers);
                expect(vm.username).toHaveValidationRule({name:'required', params:true});
            });

            it("has the minimum username complexity enforced", function() {
                expect(vm.username).toHaveValidationRule({name:'usernameComplexity', params:true});
            });
        });

        describe("holds a password which", function() {
            beforeEach(initVM(false));

            it("starts empty", function() {
                expect(vm.password()).toBeUndefined();
            });

            it("is required", function() {
                expect(vm.password).toHaveValidationRule({name:'required', params:true});
            });

            it("has the minimum password complexity enforced", function() {
                expect(vm.password).toHaveValidationRule({name:'passwordComplexity', params:true});
            });
        });

        describe("holds a password confirmation which", function() {
            beforeEach(initVM(false));

            it("starts empty", function() {
                expect(vm.passwordConfirmation()).toBeUndefined();
            });

            it("is required", function() {
                expect(vm.passwordConfirmation).toHaveValidationRule({name:'required', params:true});
            });

            it("has the minimum password complexity enforced", function() {
                expect(vm.passwordConfirmation).toHaveValidationRule({name:'passwordComplexity', params:true});
            });

            it("must match the other password", function() {
                expect(vm.passwordConfirmation).toHaveValidationRule({name:'areSame', params:vm.password});
            });
        });


    });
});