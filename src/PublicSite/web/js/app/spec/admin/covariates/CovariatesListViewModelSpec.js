/* Tests for CovariatesListViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/covariates/CovariatesListViewModel",
    "underscore"
], function (CovariatesListViewModel, _) {
    "use strict";

    describe("The covariates list view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new CovariatesListViewModel("raboof", { diseases: [ "foo" ], files: [] });
        });

        describe("holds a list of diseases which", function () {
            it("is observable", function () {
                expect(vm.diseases).toBeObservable();
            });

            it("start with the same content as passed to the constructor", function () {
                var input = { diseases: [ "1", "2", "3"], files: [] };
                vm =  new CovariatesListViewModel("foo", input);
                expect(vm.diseases()).toBe(input.diseases);
            });
        });

        describe("holds a selected disease which", function () {
            it("is observable", function () {
                expect(vm.selectedDisease).toBeObservable();
            });

            it("start with the first disease passed to the constructor", function () {
                var input = { diseases: [ "1", "2", "3"], files: [] };
                vm =  new CovariatesListViewModel("foo", input);
                expect(vm.selectedDisease()).toBe("1");
            });
        });

        describe("holds a filter string", function () {
            it("is observable", function () {
                expect(vm.filter).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.filter()).toBe("");
            });
        });

        describe("holds a sort field which", function () {
            it("is observable", function () {
                expect(vm.sortField).toBeObservable();
            });

            it("defaults to 'name'", function () {
                expect(vm.sortField()).toBe("name");
            });
        });

        describe("holds a 'reserve sort' field which", function () {
            it("is observable", function () {
                expect(vm.reverseSort).toBeObservable();
            });

            it("defaults to 'false'", function () {
                expect(vm.reverseSort()).toBe(false);
            });
        });

        describe("holds a list of files which", function () {
            it("is observable", function () {
                expect(vm.entries).toBeObservable();
            });

            it("start with the same content as passed to the constructor", function () {
                var input = { diseases: [ "foo" ], files: [
                    {name: "bar", info: "", enabled: [1], hide: false,
                        subFiles: [ { qualifier: "foo", path: "FOO" } ] },
                    { name: "containsfoo", info: "", enabled: [1], hide: false,
                        subFiles: [ { qualifier: "B", path: "bar" } ] },
                    { name: "oof", info: "", enabled: [1], hide: false,
                        subFiles: [ { qualifier: "OOF", path: "rab" } ] }
                ]};
                vm =  new CovariatesListViewModel("foo", input);
                expect(vm.entries()).toBe(input.files);
            });
        });

        describe("holds a field indicating the form isSubmitting state which", function () {
            it("is an observable", function () {
                expect(vm.isSubmitting).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isSubmitting()).toBe(false);
            });
        });

        describe("holds a field indicating the form modification state which", function () {
            it("is an observable", function () {
                expect(vm.hasUnsavedChanges).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.hasUnsavedChanges()).toBe(false);
            });
        });

        describe("holds a field for user notices which", function () {
            it("is an observable", function () {
                expect(vm.notices).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.notices()).toEqual([]);
            });
        });

        describe("has an 'update sort' method which", function () {
            it("sets the sort field if argument different to current value and sets order to descending", function () {
                spyOn(vm, "reverseSort"); // Yay for observables also being functions
                vm.updateSort("foo");
                expect(vm.sortField()).toBe("foo");
                expect(vm.reverseSort).toHaveBeenCalledWith(false);
            });

            it("sets the reverses the sort order if argument different same as current sort field", function () {
                var order = vm.reverseSort();
                spyOn(vm, "reverseSort"); // Yay for observables also being functions
                vm.updateSort("name");
                expect(vm.reverseSort).toHaveBeenCalledWith(!order);
            });
        });

        describe("has a submit method which", function () {
            it("updates the isSubmitting field correctly", function () {
                expect(vm.isSubmitting()).toBe(false);
                vm.submit();
                expect(vm.isSubmitting()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.isSubmitting()).toBe(false);
            });

            it("updates the modification state field correctly for successful submission", function () {
                vm.hasUnsavedChanges(true);
                vm.submit();
                expect(vm.hasUnsavedChanges()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                expect(vm.hasUnsavedChanges()).toBe(false);
            });

            it("updates the modification state field correctly for unsuccessful submission", function () {
                vm.hasUnsavedChanges(true);
                vm.submit();
                expect(vm.hasUnsavedChanges()).toBe(true);
                jasmine.Ajax.requests.mostRecent().response({ "status": 500 });
                expect(vm.hasUnsavedChanges()).toBe(true);
            });

            it("POSTs to the right url, with the correct data", function () {
                vm.diseases = function () { return "foo"; };
                vm.entries = function () { return "bar"; };
                vm.submit();

                var request = jasmine.Ajax.requests.mostRecent();
                expect(request.url).toBe("raboof" + "admin/covariates/config");
                expect(request.params).toEqual(JSON.stringify({ "files": "bar" }));
                expect(request.method).toBe("POST");
                expect(request.requestHeaders["Content-Type"]).toBe("application/json");
                request.response({ status: 204 });
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
        });

        describe("holds list of 'visible files' which", function () {
            it("is an observable", function () {
                expect(vm.visibleEntries).toBeObservable();
            });

            describe("is as filtered view of the 'files' list which", function () {
                beforeEach(function () {
                    vm = new CovariatesListViewModel("foo", {
                        diseases: [ { id: 1, name: "foo" } ],
                        files: [
                            {name: "bar", info: "", enabled: [1], hide: false,
                                subFiles: [ { qualifier: "foo", path: "FOO" } ] },
                            { name: "containsfoo", info: "", enabled: [1], hide: false,
                                subFiles: [ { qualifier: "B", path: "bar" } ] },
                            { name: "oof", info: "", enabled: [1], hide: false,
                                subFiles: [ { qualifier: "OOF", path: "rab" } ] }
                        ]
                    });
                });

                it("is filtered using the 'filter string' field", function () {
                    expect(vm.visibleEntries().length).toBe(3);
                    vm.filter("oo");
                    expect(vm.visibleEntries().length).toBe(2);
                });

                it("excludes hidden files", function () {
                    expect(vm.visibleEntries().length).toBe(3);
                    vm.visibleEntries()[0].hide(true);
                    expect(vm.visibleEntries().length).toBe(2);
                });

                it("converts file objects to row view models", function () {
                    // Note could rewrite this as a squire test that uses a fake version of the row vm constructor
                    expect(vm.visibleEntries()[0].usageCount).toBeDefined();
                    expect(vm.visibleEntries()[0].state).toBeDefined();
                    expect(vm.visibleEntries()[0].mouseOver).toBeDefined();
                });

                it("is sorted using the 'sort field'", function () {
                    expect(_(vm.visibleEntries()).pluck("name").map(function (f) {return f(); }))
                        .toEqual([ "bar", "containsfoo", "oof" ]);
                });

                it("is sorted using the 'sort order'", function () {
                    expect(_(vm.visibleEntries()).pluck("name").map(function (f) {return f(); }))
                        .toEqual([ "bar", "containsfoo", "oof" ]);
                    vm.reverseSort(true);
                    expect(_(vm.visibleEntries()).pluck("name").map(function (f) {return f(); }))
                        .toEqual([ "oof", "containsfoo", "bar" ]);
                });
            });
        });
    });
});
