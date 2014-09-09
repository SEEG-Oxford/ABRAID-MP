/* A suite of tests for the AccountDetailsFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/user/AccountDetailsFormViewModel",
    "shared/app/BaseFormViewModel",
    "squire"
], function (AccountDetailsFormViewModel, BaseFormViewModel, Squire) {
    "use strict";
    var noop = function () {};
    var wrap = function (result) {
        // basic currying
        return function () {
            return result;
        };
    };
    var diseasesVM = {
        buildSubmissionData: wrap([1])
    };

    describe("The account details form view model", function () {
        describe("has the BaseFormViewModel behavior which", function () {
            var vm;
            var formSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    formSpy = jasmine.createSpy("formSpy").and.callFake(BaseFormViewModel);
                    injector.mock("shared/app/BaseFormViewModel", formSpy);

                    injector.require(["app/user/AccountDetailsFormViewModel"],
                        function (AccountDetailsFormViewModel) {
                            vm = new AccountDetailsFormViewModel("base/", "target", { foo: "bar" }, {}, noop, {});
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("it gets at construction", function () {
                expect(formSpy).toHaveBeenCalled();
            });

            it("builds the correct submission url", function () {
                expect(vm.buildSubmissionUrl()).toEqual("base/target");
            });

            it("sends and receives JSON", function () {
                expect(formSpy.calls.argsFor(0)[0]).toEqual(true);
                expect(formSpy.calls.argsFor(0)[1]).toEqual(true);
            });

            it("uses the provided messages", function () {
                expect(formSpy.calls.argsFor(0)[4]).toEqual({ foo: "bar" });
            });
        });

        describe("has a name field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                expect(vm.name).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, { name: "name"}, noop, {});
                    expect(vm.name()).toBe("name");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                    expect(vm.name()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});

                it("be a mandatory requirement", function () {
                    expect(vm.name).toHaveValidationRule({name: "required", params: true});
                });

                it("not be of an excessive length", function () {
                    expect(vm.name).toHaveValidationRule({name: "maxLength", params: 1000});
                });
            });
        });

        describe("has a jobTitle field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                expect(vm.jobTitle).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, { jobTitle: "jobTitle"}, noop, {});
                    expect(vm.jobTitle()).toBe("jobTitle");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {},  {}, noop, {});
                    expect(vm.jobTitle()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});

                it("be a mandatory requirement", function () {
                    expect(vm.jobTitle).toHaveValidationRule({name: "required", params: true});
                });

                it("not be of an excessive length", function () {
                    expect(vm.jobTitle).toHaveValidationRule({name: "maxLength", params: 100});
                });
            });
        });

        describe("has an institution field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                expect(vm.institution).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, { institution: "institution"}, noop, {});
                    expect(vm.institution()).toBe("institution");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                    expect(vm.institution()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});

                it("be a mandatory requirement", function () {
                    expect(vm.institution).toHaveValidationRule({name: "required", params: true});
                });

                it("not be of an excessive length", function () {
                    expect(vm.institution).toHaveValidationRule({name: "maxLength", params: 100});
                });
            });
        });

        describe("has a visibilityRequested field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                expect(vm.visibilityRequested).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, { visibilityRequested: true }, noop, {});
                    expect(vm.visibilityRequested()).toBe(true);
                });

                it("false if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, {});
                    expect(vm.visibilityRequested()).toBe(false);
                });
            });
        });

        describe("holds a sub view model for the disease interest list which", function () {
            it("takes the value passed to the constructor", function () {
                var subVM = { "fakeKey": "fakeValue" };
                var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, subVM);
                expect(vm.diseaseInterestListViewModel).toBe(subVM);
            });
        });

        it("exposes a buildSubmissionData function which builds the correct content", function () {
            // Arrange
            var vm = new AccountDetailsFormViewModel("", "", {}, {}, noop, diseasesVM);
            vm.isValid = wrap(true);

            vm.name("expected_name");
            vm.jobTitle("expected_job");
            vm.institution("expected_institute");
            vm.visibilityRequested(true);
            vm.diseaseInterestListViewModel.buildSubmissionData = wrap([ 9, 7 ]);

            // Act
            vm.submit();

            // Assert
            var body = JSON.parse(jasmine.Ajax.requests.mostRecent().params);
            expect(body.name).toEqual("expected_name");
            expect(body.jobTitle).toEqual("expected_job");
            expect(body.institution).toEqual("expected_institute");
            expect(body.visibilityRequested).toEqual(true);
            expect(body.diseaseInterests.length).toEqual(2);
            expect(body.diseaseInterests).toContain(9);
            expect(body.diseaseInterests).toContain(7);
        });

        describe("handles successful form submission", function () {
            it("with the standard successful form submission behavior", function () {
                // Arrange
                var vm = new AccountDetailsFormViewModel("baseUrl/", "", {}, {}, noop, diseasesVM);
                spyOn(vm, "baseSuccessHandler");

                // Act
                vm.submit();
                expect(vm.baseSuccessHandler).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ "status": 200, "responseText": "[]" });

                // Assert
                expect(vm.baseSuccessHandler).toHaveBeenCalled();
            });

            it("by redirecting to the home page", function () {
                // Arrange
                var redirect = jasmine.createSpy();
                var vm = new AccountDetailsFormViewModel("baseUrl/", "", {}, {}, redirect, diseasesVM);
                vm.isValid = wrap(true);

                // Act
                vm.submit();
                expect(redirect).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ "status": 204 });

                // Assert
                expect(redirect).toHaveBeenCalled();
                expect(redirect).toHaveBeenCalledWith("baseUrl/");
            });
        });

        describe("handles failed form submission", function () {
            it("with the standard failed form submission behavior", function () {
                // Arrange
                var vm = new AccountDetailsFormViewModel("baseUrl/", "", {}, {}, noop, diseasesVM);
                spyOn(vm, "baseFailureHandler");

                // Act
                vm.submit();
                expect(vm.baseFailureHandler).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ "status": 409, "responseText": "[]" });

                // Assert
                expect(vm.baseFailureHandler).toHaveBeenCalled();
            });

            it("by redirecting to the account registration page if email address clashed", function () {
                // Arrange
                var redirect = jasmine.createSpy();
                var vm = new AccountDetailsFormViewModel("baseUrl/", "", {}, {}, redirect, diseasesVM);

                // Act
                vm.submit();
                expect(redirect).not.toHaveBeenCalled();
                jasmine.Ajax.requests.mostRecent().response({ "status": 409, "responseText": "[]" });

                // Assert
                expect(redirect).toHaveBeenCalledWith("baseUrl/" + "register/account");
            });
        });
    });
});
