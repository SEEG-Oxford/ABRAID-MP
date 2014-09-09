/* A suite of tests for the DiseaseInterestListViewModel
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/user/DiseaseInterestListViewModel",
    "shared/app/BaseTableViewModel",
    "underscore",
    "squire"
], function (DiseaseInterestListViewModel, BaseTableViewModel, _, Squire) {
    "use strict";

    describe("The disease interest list view model", function () {
        describe("has the BaseTableViewModel behavior which", function () {
            var vm;
            var tableSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    tableSpy = jasmine.createSpy("tableSpy").and.callFake(BaseTableViewModel);
                    injector.mock("shared/app/BaseTableViewModel", tableSpy);

                    injector.require(["app/user/DiseaseInterestListViewModel"],
                        function (DiseaseInterestListViewModel) {
                            vm = new DiseaseInterestListViewModel(
                                { diseaseInterests: [1, 3] }, [ {id: 1}, {id: 2}, {id: 3} ]);
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("it gets at construction", function () {
                expect(tableSpy).toHaveBeenCalled();
            });

            it("defaults to name based sorting", function () {
                expect(tableSpy.calls.argsFor(0)[1]).toEqual("name");
            });

            it("defaults to ascending sort order", function () {
                expect(tableSpy.calls.argsFor(0)[2]).toEqual(false);
            });

            it("defaults to only being filtered based on name", function () {
                expect(tableSpy.calls.argsFor(0)[3].length).toEqual(1);
                expect(tableSpy.calls.argsFor(0)[3][0]).toEqual("name");
            });

            it("doesn't have a row view model mapping function", function () {
                expect(tableSpy.calls.argsFor(0)[4]).toBeUndefined();
            });

            it("defaults to the diseases passed to the constructor, with added 'interested' observable", function () {
                expect(tableSpy.calls.argsFor(0)[0].length).toEqual(3);
                //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                //expect(_(vm.entries()).findWhere({id: 1}).interested).toBeObservable();
                expect(_(vm.entries()).findWhere({id: 1}).interested()).toBe(true);
                //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                //expect(_(vm.entries()).findWhere({id: 2}).interested).toBeObservable();
                expect(_(vm.entries()).findWhere({id: 2}).interested()).toBe(false);
            });
        });

        describe("has a buildSubmissionData function which", function () {
            it("returns the IDs of any diseases set to interested", function () {
                var vm = new DiseaseInterestListViewModel({ diseaseInterests: [1, 3] }, [ {id: 1}, {id: 2}, {id: 3} ]);
                var initial = vm.buildSubmissionData();

                _(vm.entries()).findWhere({id: 2}).interested(true);
                _(vm.entries()).findWhere({id: 1}).interested(false);
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