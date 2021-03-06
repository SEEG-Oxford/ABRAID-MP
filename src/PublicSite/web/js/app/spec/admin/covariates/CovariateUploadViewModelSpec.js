/* Tests for CovariateUploadViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/covariates/CovariateUploadViewModel",
    "ko",
    "squire"
], function (CovariateUploadViewModel, ko, Squire) {
    "use strict";

    describe("The covariate upload view model", function () {
        var vm, covariateListVM, refreshSpy;
        var covariates = [ { id: 1, path: "1" }, { id: 2, path: "2" }, { id: 3, path: "3" } ];
        beforeEach(function () {
            refreshSpy = jasmine.createSpy("refresh");
            covariateListVM = {
                hasUnsavedChanges: ko.observable(false),
                visibleEntries: ko.observableArray([ { name: "1" }, { name: "2" }, { name: "3" } ]),
                entries: ko.observableArray(covariates)
            };
            vm = new CovariateUploadViewModel("baseUrl", covariateListVM, refreshSpy);
        });

        describe("holds a 'name' field which", function () {
            it("is observable", function () {
                expect(vm.name).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.name()).toBe("");
            });

            it("is a required field", function () {
                expect(vm.name).toHaveValidationRule({ name: "required", params: true });
            });

            it("is required to be unique within the list of covariate names", function () {
                expect(vm.name).toHaveValidationRule({ name: "isUniqueProperty", params: {
                    array: covariateListVM.visibleEntries,
                    property: "name"
                }});
            });
        });

        describe("holds a 'qualifier' field which", function () {
            it("is observable", function () {
                expect(vm.name).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.name()).toBe("");
            });

            it("is a required field", function () {
                expect(vm.name).toHaveValidationRule({ name: "required", params: true });
            });

            it("is required to be unique within the list of covariate names", function () {
                expect(vm.name).toHaveValidationRule({ name: "isUniqueProperty", params: {
                    array: covariateListVM.visibleEntries,
                    property: "name"
                }});
            });
        });

        describe("holds a 'parent' field which", function () {
            it("is observable", function () {
                expect(vm.parent).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.parent()).toBeUndefined();
            });
        });

        describe("holds a 'parentList' field which", function () {
            it("is the list of known covariates", function () {
                expect(vm.parentList()).toBe(covariates);
            });
        });

        describe("holds a 'discrete' field which", function () {
            it("is observable", function () {
                expect(vm.discrete).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.discrete()).toBe(false);
            });
        });

        describe("holds a 'subdirectory' field which", function () {
            it("is observable", function () {
                expect(vm.subdirectory).toBeObservable();
            });

            it("starts with './'", function () {
                expect(vm.subdirectory()).toBe("./");
            });

            it("is a required field", function () {
                expect(vm.subdirectory).toHaveValidationRule({ name: "required", params: true });
            });

            it("is required to start with './'", function () {
                expect(vm.subdirectory).toHaveValidationRule({ name: "startWith", params: "./" });
            });

            it("is required to end with '/'", function () {
                expect(vm.subdirectory).toHaveValidationRule({ name: "endWith", params: "/" });
            });

            it("is required to not contain any questionable substrings", function () {
                expect(vm.subdirectory)
                    .toHaveValidationRule({ name: "notContain", params: ["/../", "/./", "//", "\\"]});
            });
        });

        describe("holds an 'unsavedWarning' field, which", function () {
            it("is observable", function () {
                expect(vm.unsavedWarning).toBeObservable(true);

            });

            it("mirrors the value from the CovariateListViewModel", function () {
                expect(vm.unsavedWarning()).toBe(false);
                covariateListVM.hasUnsavedChanges(true);
                expect(vm.unsavedWarning).toBeObservable(true);
            });

            it("is a required to be false", function () {
                expect(vm.unsavedWarning).toHaveValidationRule({ name: "equal", params: false });
            });
        });

        describe("holds an 'uploadPath' field, which", function () {
            it("is observable", function () {
                expect(vm.uploadPath).toBeObservable(true);
            });

            it("combines the values from the subdirectory and file fields", function () {
                vm.subdirectory("./folder/");
                vm.file({ name: "file.png" });
                expect(vm.uploadPath()).toBe("folder/file.png");

                vm.subdirectory("abc");
                vm.file({ name: "def" });
                expect(vm.uploadPath()).toBe("abcdef");
            });

            it("is a required to be unique within the list of covariate paths", function () {
                expect(vm.uploadPath).toHaveValidationRule({ name: "isUniqueProperty", params: {
                    array: covariateListVM.entries,
                    property: "path"
                }});
            });
        });

        it("sets the postSuccessAction to 'refresh'", function () {
            expect(vm.postSuccessAction).toBe(refreshSpy);
        });

        describe("is an extension of BaseFileFormViewModel, with", function () {
            it("the correct constructor arguments", function (done) {
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                var baseSpy = jasmine.createSpy("baseSpy").and.callFake(function () {
                    this.file = function () { return undefined; };
                });
                injector.mock("shared/app/BaseFileFormViewModel", baseSpy);

                injector.require(["app/admin/covariates/CovariateUploadViewModel"],
                    function (CovariateUploadViewModel) {
                        vm = new CovariateUploadViewModel("baseUrl", covariateListVM, refreshSpy);
                        expect(baseSpy.calls.argsFor(0)[0]).toBe("baseUrl");
                        expect(baseSpy.calls.argsFor(0)[1]).toBe("admin/covariates/add");
                        done();
                    }
                );
            });

            it("a custom buildSubmissionData function, which builds the correct data, for new covariate", function () {
                vm.parent(undefined);
                vm.name("abc");
                vm.discrete(true);
                vm.qualifier("123");
                vm.subdirectory("def");
                expect(vm.buildSubmissionData()).toEqual({
                    name: "abc",
                    qualifier: "123",
                    discrete: true,
                    subdirectory: "def",
                    parentId: -1
                });
            });

            it("a custom buildSubmissionData function, which builds the correct data, for sub covariate", function () {
                vm.parent(covariates[0]);
                vm.name("abc");
                vm.discrete(false);
                vm.qualifier("123");
                vm.subdirectory("def");
                expect(vm.buildSubmissionData()).toEqual({
                    name: "abc",
                    qualifier: "123",
                    discrete: false,
                    subdirectory: "def",
                    parentId: 1
                });
            });

            it("a custom postSuccessAction function which is set to 'refresh'", function () {
                expect(vm.postSuccessAction).toBe(refreshSpy);
            });
        });
    });
});
