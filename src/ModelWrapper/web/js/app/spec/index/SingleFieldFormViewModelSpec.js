/* Tests for SingleFieldFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/index/SingleFieldFormViewModel",
    "ko",
    "underscore"
], function (SingleFieldFormViewModel, ko, _) {
    "use strict";

    describe("A single field form view model", function () {
        describe("holds a value which", function () {
            it("is an observable", function () {
                var vm = new SingleFieldFormViewModel("", "", "", {});
                expect(vm.value).toBeObservable();
            });

            it("starts with the value provided to the constructor", function () {
                var expectation = 12345;
                var vm = new SingleFieldFormViewModel("", "", expectation, {});
                expect(vm.value()).toBe(expectation);
            });

            it("has the validation rules provided to the constructor applied", function () {
                var expectation = { required: true, number: true};
                var vm = new SingleFieldFormViewModel("", "", 0, expectation);

                _(expectation).each(function (value, key) {
                    expect(vm.value).toHaveValidationRule({name: key, params: value});
                });
            });
        });

        describe("holds a field indicating the form saving state which", function () {
            var vm = {};
            beforeEach(function () {
                vm = new SingleFieldFormViewModel("", "", "", {});
            });

            it("is an observable", function () {
                expect(vm.saving).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.saving()).toBe(false);
            });
        });

        describe("holds a field for user notices which", function () {
            var vm = {};
            beforeEach(function () {
                vm = new SingleFieldFormViewModel("", "", "", {});
            });

            it("is an observable", function () {
                expect(vm.notices).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.notices()).toEqual([]);
            });
        });

        describe("has a submit method which", function () {
            var vm = {};
            beforeEach(function () {
                vm = ko.validatedObservable(new SingleFieldFormViewModel("", "", "", {}))();
            });

            it("updates the saving field correctly", function () {
                expect(vm.saving()).toBe(false);
                vm.submit();
                expect(vm.saving()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.saving()).toBe(false);
            });

            it("POSTs to the specified url, with the correct value", function () {
                var expectation1 = "foo";
                var expectation2 = "bar";
                var expectation3 = "bob";
                vm = ko.validatedObservable(
                    new SingleFieldFormViewModel(expectation1, expectation2, expectation3, {}))();
                vm.submit();
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectation1 + expectation2);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe("value=" + expectation3);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });

            it("clears the current notices", function () {
                spyOn(vm.notices, "removeAll");
                vm.submit();
                expect(vm.notices.removeAll).toHaveBeenCalled();
            });

            it("adds a success notice", function () {
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.notices()[0].priority).toBe("success");
            });

            it("adds a fail notice", function () {
                vm.submit();
                jasmine.Ajax.requests.mostRecent().response({ "status": 400 });
                expect(vm.notices()[0].priority).toBe("warning");
            });

            it("doesnt POST for invalid states", function () {
                spyOn(vm, "isValid").and.returnValue(false);
                vm.submit();
                expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
            });

            it("add a warning notice for invalid states", function () {
                spyOn(vm, "isValid").and.returnValue(false);
                vm.submit();
                expect(vm.notices()[0].priority).toBe("warning");
            });
        });
    });
});