/* Tests for CovariatesListRowViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([ "app/CovariatesListRowViewModel" ], function (CovariatesListRowViewModel) {
    "use strict";

    describe("The covariates list row view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new CovariatesListRowViewModel({}, { enabled: [] }, 0);
        });

        describe("holds a name which", function () {
            var parentFile = {};
            var parentVM = {};
            beforeEach(function () {
                parentFile = { name: "foo", enabled: [] };
                parentVM = { hasUnsavedChanges: jasmine.createSpy() };
                vm = new CovariatesListRowViewModel(parentVM, parentFile, 0);
            });

            it("is an observable", function () {
                expect(vm.name).toBeObservable();
            });

            it("has the same initial value as the parent file object", function () {
                expect(vm.name()).toBe(parentFile.name);
            });

            it("updates the parent file object when set", function () {
                vm.name("raboof");
                expect(parentFile.name).toBe("raboof");
            });

            it("marks the parent view model as modified when set", function () {
                vm.name("raboof");
                expect(parentVM.hasUnsavedChanges).toHaveBeenCalledWith(true);
            });
        });

        describe("holds a hide field which", function () {
            var parentFile = {};
            var parentVM = {};
            beforeEach(function () {
                parentFile = { hide: false, enabled: [] };
                parentVM = { hasUnsavedChanges: jasmine.createSpy(), files: { valueHasMutated: jasmine.createSpy() } };
                vm = new CovariatesListRowViewModel(parentVM, parentFile, 0);
            });

            it("is an observable", function () {
                expect(vm.hide).toBeObservable();
            });

            it("has the same initial value as the parent file object", function () {
                expect(vm.hide()).toBe(parentFile.hide);
            });

            it("updates the parent file object when set", function () {
                vm.hide(true);
                expect(parentFile.hide).toBe(true);
            });

            it("marks the parent view model as modified when set", function () {
                vm.hide(true);
                expect(parentVM.hasUnsavedChanges).toHaveBeenCalledWith(true);
            });

            it("forces a update of the parent view model's 'files' field dependants", function () {
                vm.hide(true);
                expect(parentVM.files.valueHasMutated).toHaveBeenCalled();
            });
        });

        describe("holds a 'mouse over' field which", function () {
            it("is an observable", function () {
                expect(vm.mouseOver).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.mouseOver()).toBe(false);
            });
        });

        describe("holds a path field which", function () {
            it("has the same value as the parent file (prefixed with './')", function () {
                vm = new CovariatesListRowViewModel({}, { enabled: [], path: "foobar" }, 0);
                expect(vm.path).toBe("./foobar");
            });
        });

        describe("holds an info field which", function () {
            it("has the same value as the parent file", function () {
                vm = new CovariatesListRowViewModel({}, { enabled: [], info: "foobar" }, 0);
                expect(vm.info).toBe("foobar");
            });
        });

        describe("holds a usage count field which", function () {
            it("is an observable", function () {
                expect(vm.usageCount).toBeObservable();
            });

            it("starts with the number disease for which the parent file is enabled", function () {
                vm = new CovariatesListRowViewModel({}, { enabled: [] }, 0);
                expect(vm.usageCount()).toBe(0);

                vm = new CovariatesListRowViewModel({}, { enabled: [1, 2, 3, 10] }, 0);
                expect(vm.usageCount()).toBe(4);
            });
        });

        describe("holds state field which", function () {
            var parentVM = { hasUnsavedChanges: jasmine.createSpy() };

            it("is an observable", function () {
                expect(vm.state).toBeObservable();
            });

            it("initially reflects if the parent file enabled for the active disease", function () {
                vm = new CovariatesListRowViewModel({}, {enabled: []}, 0);
                expect(vm.state).toBeObservable(false);

                vm = new CovariatesListRowViewModel({}, {enabled: [ 10 ]}, 0);
                expect(vm.state).toBeObservable(false);

                vm = new CovariatesListRowViewModel({}, {enabled: [ 0 ]}, 0);
                expect(vm.state).toBeObservable(true);
            });

            it("updates the parent file object when set", function () {
                var parentFile = { enabled: [] };
                vm = new CovariatesListRowViewModel(parentVM, parentFile, 0);
                vm.state(true);
                expect(parentFile.enabled).toEqual([ 0 ]);

                parentFile = { enabled: [ 10 ] };
                vm = new CovariatesListRowViewModel(parentVM, parentFile, 0);
                vm.state(true);
                expect(parentFile.enabled).toEqual([ 10, 0 ]);
                vm.state(false);
                expect(parentFile.enabled).toEqual([ 10 ]);

                parentFile = { enabled: [ 0 ] };
                vm = new CovariatesListRowViewModel(parentVM, parentFile, 0);
                vm.state(false);
                expect(parentFile.enabled).toEqual([ ]);
            });

            it("marks the parent view model as modified when set", function () {
                vm = new CovariatesListRowViewModel(parentVM, { enabled: [] }, 0);
                vm.state(true);
                expect(parentVM.hasUnsavedChanges).toHaveBeenCalledWith(true);
            });
        });
    });
});