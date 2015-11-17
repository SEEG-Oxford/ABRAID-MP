/* A suite of tests for the HealthMapAdministrationViewModel AMD.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "app/admin/HealthMapDiseaseRowViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (HealthMapDiseaseRowViewModel, BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'HealthMapDiseaseRowViewModel' view model", function () {
        var baseUrl = "base";
        var healthMapDisease = {
            id: 1,
            name: "2",
            parent: {
                id: 3,
                name: "4"
            },
            abraidDisease: {
                id: 5,
                name: "6"
            }
        };

        describe("has state fields for it's", function () {
            var vm = new HealthMapDiseaseRowViewModel(baseUrl, "foo", healthMapDisease);

            it("ID", function () {
                expect(vm.id).toBeDefined();
                expect(vm.id).toEqual(healthMapDisease.id);
            });
            it("name", function () {
                expect(vm.name).toBeDefined();
                expect(vm.name).toEqual(healthMapDisease.name);
            });
            it("parent disease", function () {
                expect(vm.parentDisease).toBeDefined();
                expect(vm.parentDisease).toBeObservable();
                expect(vm.parentDisease()).toEqual(healthMapDisease.parent);
            });
            it("abraid disease", function () {
                expect(vm.abraidDisease).toBeDefined();
                expect(vm.abraidDisease).toBeObservable();
                expect(vm.abraidDisease()).toEqual(healthMapDisease.abraidDisease);
            });
            it("updated parent disease", function () {
                expect(vm.parentDiseaseNew).toBeDefined();
                expect(vm.parentDiseaseNew).toBeObservable();
                expect(vm.parentDiseaseNew()).toEqual(healthMapDisease.parent);
            });
            it("updated abraid disease", function () {
                expect(vm.abraidDiseaseNew).toBeDefined();
                expect(vm.abraidDiseaseNew).toBeObservable();
                expect(vm.abraidDiseaseNew()).toEqual(healthMapDisease.abraidDisease);
            });
            it("editing state", function () {
                expect(vm.editing).toBeDefined();
                expect(vm.editing).toBeObservable();
                expect(vm.editing()).toEqual(false);
            });
        });

        describe("has the behaviour of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseFormViewModel);
                    injector.mock("shared/app/BaseFormViewModel", baseSpy);

                    injector.require(["app/admin/HealthMapDiseaseRowViewModel"],
                        function (HealthMapDiseaseRowViewModel) {
                            vm = new HealthMapDiseaseRowViewModel(baseUrl, "foo", healthMapDisease);
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("when created", function () {
                expect(baseSpy).toHaveBeenCalled();
            });

            it("it specifies the correct 'baseUrl'", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[2]).toBe(baseUrl);
            });

            it("it specifies the correct form url", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[3]).toBe("foo");
            });

            it("it specifies to send JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0]).toBe(true);
            });

            it("it specifies to receive JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[1]).toBe(true);
            });

            it("it uses the standard UI messages", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[4]).toBeUndefined();
            });

            describe("overrides the 'buildSubmissionData' function, which", function () {
                it("generates the correct data object", function () {
                    vm.abraidDiseaseNew({ id: 7, name: "8", not: "used" });
                    vm.parentDiseaseNew({ id: 9, name: "10", not: "used" });
                    var data = vm.buildSubmissionData();
                    expect(data).toEqual({
                        id: 1,
                        name: "2",
                        parent: {
                            id: 9,
                            name: "10"
                        },
                        abraidDisease: {
                            id: 7,
                            name: "8"
                        }
                    });
                });

                it("generates the correct data object, with empty fields", function () {
                    vm.abraidDiseaseNew(undefined);
                    vm.parentDiseaseNew(undefined);
                    var data = vm.buildSubmissionData();
                    expect(data).toEqual({
                        id: 1,
                        name: "2",
                        abraidDisease: undefined,
                        parent: undefined
                    });
                });
            });

            describe("overrides the 'successHandler' function, which", function () {
                it("synchronises the linked disease fields", function () {
                    vm.abraidDisease({ id: 7, name: "8" });
                    vm.parentDisease({ id: 9, name: "10" });
                    vm.abraidDiseaseNew({ id: 11, name: "12"});
                    vm.parentDiseaseNew({ id: 13, name: "14"});
                    vm.successHandler();
                    expect(vm.abraidDisease()).toEqual({ id: 11, name: "12"});
                    expect(vm.parentDisease()).toEqual({ id: 13, name: "14"});
                });

                it("sets editing to false", function () {
                    vm.editing(true);
                    vm.successHandler();
                    vm.editing(false);
                });
            });
        });
    });
});
