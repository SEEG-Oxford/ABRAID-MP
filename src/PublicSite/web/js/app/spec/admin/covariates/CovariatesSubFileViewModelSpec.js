/* Tests for CovariatesSubFileViewModel.
 * Copyright (c) 2015 University of Oxford
 */
define([ "app/admin/covariates/CovariatesSubFileViewModel" ], function (CovariatesSubFileViewModel) {
    "use strict";

    describe("The covariates sub file view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new CovariatesSubFileViewModel({}, { enabled: [] }, 0);
        });

        describe("holds a qualifier which", function () {
            var parentSubFile = {};
            var parentVM = {};
            beforeEach(function () {
                parentSubFile = { qualifier: "foo", path: "asdf" };
                parentVM = { hasUnsavedChanges: jasmine.createSpy() };
                vm = new CovariatesSubFileViewModel(parentVM, parentSubFile, 0);
            });

            it("is an observable", function () {
                expect(vm.qualifier).toBeObservable();
            });

            it("has the same initial value as the parent file object", function () {
                expect(vm.qualifier()).toBe(parentSubFile.qualifier);
            });

            it("updates the parent file object when set", function () {
                vm.qualifier("raboof");
                expect(parentSubFile.qualifier).toBe("raboof");
            });

            it("marks the parent view model as modified when set", function () {
                vm.qualifier("raboof");
                expect(parentVM.hasUnsavedChanges).toHaveBeenCalledWith(true);
            });
        });

        describe("holds a path field which", function () {
            it("has the same value as the parent file (prefixed with './')", function () {
                vm = new CovariatesSubFileViewModel({}, { qualifier: "foo", path: "asdf" }, 0);
                expect(vm.path).toBe("./asdf");
            });
        });

    });
});
