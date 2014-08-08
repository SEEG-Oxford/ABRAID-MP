/* A suite of tests for the DiseaseInterestListViewModel
 * Copyright (c) 2014 University of Oxford
 */
define(["app/user/DiseaseInterestListViewModel", "underscore"], function (DiseaseInterestListViewModel, _) {
    "use strict";
    var vm = {};
    beforeEach(function () {
        vm = new DiseaseInterestListViewModel({}, []);
    });

    describe("The disease interest list view model", function () {
        describe("holds a list of diseases which", function () {
            it("is observable", function () {
                expect(vm.diseases).toBeObservable();
            });

            it("start with the same content as passed to the constructor", function () {
                var vm = new DiseaseInterestListViewModel({diseaseInterests: [1]}, [ {id: 1}, {id: 2} ]);
                expect(vm.diseases().length).toBe(2);
                expect(_(vm.diseases()).pluck("id")).toContain(1);
                expect(_(vm.diseases()).pluck("id")).toContain(2);
            });

            it("adds an 'interested' observable to its initial entries", function () {
                var vm = new DiseaseInterestListViewModel({diseaseInterests: [1]}, [ {id: 1}, {id: 2} ]);
                expect(_(vm.diseases()).findWhere({id: 1}).interested).toBeObservable();
                expect(_(vm.diseases()).findWhere({id: 1}).interested()).toBe(true);
                expect(_(vm.diseases()).findWhere({id: 2}).interested).toBeObservable();
                expect(_(vm.diseases()).findWhere({id: 2}).interested()).toBe(false);
            });
        });

        describe("holds a filter string, which", function () {
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

        describe("holds list of 'visible diseases' which", function () {
            it("is an observable", function () {
                expect(vm.visibleDiseases).toBeObservable();
            });

            describe("is a filtered view of the 'diseases' list which", function () {
                beforeEach(function () {
                    vm = new DiseaseInterestListViewModel(
                        { diseaseInterests: [1, 3] },
                        [ {id: 1, name: "foo"}, {id: 2, name: "boo"}, {id: 3, name: "boofoo"} ]
                    );
                });

                it("is filtered using the 'filter string' field", function () {
                    expect(vm.visibleDiseases().length).toBe(3);
                    vm.filter("foo");
                    expect(vm.visibleDiseases().length).toBe(2);
                });

                it("is sorted using the 'sort field'", function () {
                    expect(_(vm.visibleDiseases()).pluck("name")).toEqual([ "boo", "boofoo", "foo" ]);
                    vm.sortField("interested");
                    expect(_(vm.visibleDiseases()).pluck("name")).toEqual([ "foo", "boofoo", "boo" ]);
                });

                it("is sorted using the 'sort order'", function () {
                    expect(_(vm.visibleDiseases()).pluck("name")).toEqual([ "boo", "boofoo", "foo" ]);
                    vm.reverseSort(true);
                    expect(_(vm.visibleDiseases()).pluck("name")).toEqual([ "foo", "boofoo", "boo"  ]);
                });
            });
        });

        describe("has a buildSubmissionData function which", function () {
            it("returns the IDs of any diseases set to interested", function () {
                vm = new DiseaseInterestListViewModel({ diseaseInterests: [1, 3] }, [ {id: 1}, {id: 2}, {id: 3} ]);
                var initial = vm.buildSubmissionData();

                _(vm.diseases()).findWhere({id: 2}).interested(true);
                _(vm.diseases()).findWhere({id: 1}).interested(false);
                var other = vm.buildSubmissionData();

                expect(initial.length).toBe(2);
                expect(initial).toContain(1);
                expect(initial).toContain(3);

                expect(other.length).toBe(2);
                expect(other).toContain(2);
                expect(other).toContain(3);
            });
        });
    });
});