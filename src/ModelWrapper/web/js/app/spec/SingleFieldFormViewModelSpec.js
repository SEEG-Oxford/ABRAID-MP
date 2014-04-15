/* Tests for SingleFieldFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false, afterEach:false, jasmine:false*/
define([ 'app/SingleFieldFormViewModel', 'ko', 'underscore', 'app/spec/util/ruleMatcher', 'app/spec/util/observableMatcher' ], function(SingleFieldFormViewModel, ko, _, ruleMatcher, observableMatcher) {
    "use strict";

    describe("A single field form view model", function() {
        var addCustomMatchers = function() {
            jasmine.addMatchers({ toHaveValidationRule: ruleMatcher });
            jasmine.addMatchers({ toBeObservable: observableMatcher });
        };

        describe("holds a value which", function() {
            beforeEach(addCustomMatchers);

            it("is an observable", function() {
                var vm = new SingleFieldFormViewModel("", "", "", {});
                expect(vm.value).toBeObservable();
            });

            it("starts with the value provided to the constructor", function() {
                var expectation = 12345;
                var vm = new SingleFieldFormViewModel("", "", expectation, {});
                expect(vm.value()).toBe(expectation);
            });

            it("has the validation rules provided to the constructor applied", function() {
                var expectation = { required: true, number: true};
                var vm = new SingleFieldFormViewModel("", "", 0, expectation);

                _(expectation).each(function (value, key) {
                    expect(vm.value).toHaveValidationRule({name: key, params: value});
                });
            });
        });

        describe("holds a field indicating the form saving state which", function() {
            var vm = {};
            beforeEach(function() {
                vm = new SingleFieldFormViewModel("", "", "", {});
                addCustomMatchers();
            });

            it("is an observable", function() {
                expect(vm.saving).toBeObservable();
            });

            it("starts false", function() {
                expect(vm.saving()).toBe(false);
            });
        });

        describe("holds a field for user notices", function() {
            var vm = {};
            beforeEach(function() {
                vm = new SingleFieldFormViewModel("", "", "", {});
                addCustomMatchers();
            });

            it("is an observable", function() {
                expect(vm.notices).toBeObservable();
            });

            it("starts empty", function() {
                expect(vm.notices()).toEqual([]);
            });
        });

        describe("has a submit method which", function() {
            var vm = {};
            beforeEach(function() {
                vm = ko.validatedObservable(new SingleFieldFormViewModel("", "", "", {}))();
                addCustomMatchers();
                jasmine.Ajax.install();
            });

            afterEach(function() {
                jasmine.Ajax.uninstall();
            });

            it("updates the saving field correctly", function() {
                expect(vm.saving()).toBe(false);
                vm.submit();
                expect(vm.saving()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.saving()).toBe(false);
            });

            it("POSTs to the specified url, with the correct value", function() {
                var expection1 = "foo";
                var expection2 = "bar";
                var expection3 = "bob";
                vm = ko.validatedObservable(new SingleFieldFormViewModel(expection1, expection2, expection3, {}))();
                vm.submit();
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expection1+expection2);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe("value="+expection3);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });

            it("clears the current notices", function() {
                spyOn(vm.notices, 'removeAll');
                vm.submit();
                expect(vm.notices.removeAll).toHaveBeenCalled();
            });

            it("adds a success notice", function() {
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.notices()[0].priority).toBe("success");
            });

            it("adds a fail notice", function() {
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ "status": 400 });
                expect(vm.notices()[0].priority).toBe("warning");
            });

            it("doesnt POST for invalid states", function() {
                spyOn(vm, 'isValid').and.returnValue(false);
                vm.submit();
                expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
            });

            it("add a warning notice for invalid states", function() {
                spyOn(vm, 'isValid').and.returnValue(false);
                vm.submit();
                expect(vm.notices()[0].priority).toBe("warning");
            });
        });
    });
});