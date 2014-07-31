/* A suite of tests for the AccountDetailsFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/register/AccountDetailsFormViewModel"], function (AccountDetailsFormViewModel) {
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
        describe("has a name field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.name).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", { name: "name"}, noop, {});
                    expect(vm.name()).toBe("name");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                    expect(vm.name()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});

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
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.jobTitle).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", { jobTitle: "jobTitle"}, noop, {});
                    expect(vm.jobTitle()).toBe("jobTitle");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                    expect(vm.jobTitle()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});

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
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.institution).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", { institution: "institution"}, noop, {});
                    expect(vm.institution()).toBe("institution");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                    expect(vm.institution()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});

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
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.visibilityRequested).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", { visibilityRequested: true }, noop, {});
                    expect(vm.visibilityRequested()).toBe(true);
                });

                it("false if no value is passed to the constructor", function () {
                    var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                    expect(vm.visibilityRequested()).toBe(false);
                });
            });
        });

        describe("holds a sub view model for the disease interest list which", function () {
            it("takes the value passed to the constructor", function () {
                var subVM = { "fakeKey": "fakeValue" };
                var vm = new AccountDetailsFormViewModel("", {}, noop, subVM);
                expect(vm.diseaseInterestListViewModel).toBe(subVM);
            });
        });

        describe("has a notices field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.notices).toBeObservable();
            });

            it("starts with empty array", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.notices()).toEqual([]);
            });
        });

        describe("has a isSubmitting field which", function () {
            it("is an observable", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.notices).toBeObservable();
            });

            it("starts with false", function () {
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                expect(vm.isSubmitting()).toEqual(false);
            });
        });

        describe("exposes a submit function which", function () {
            it("clears any existing notices", function () {
                // Arrange
                var vm = new AccountDetailsFormViewModel("", {}, noop, {});
                vm.isValid = wrap(false);
                spyOn(vm.notices, "removeAll");

                // Act
                vm.submit();

                // Assert
                expect(vm.notices.removeAll).toHaveBeenCalled();
            });

            it("ensures that the form fields are valid", function () {
                // Arrange
                var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                vm.isValid = wrap(false);

                // Act
                vm.submit();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
                expect(vm.notices())
                    .toContain({ message: "Fields must be valid before saving.", priority: "warning" });
            });

            describe("updates the isSubmitting field", function () {
                it("to true when sending the data", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(vm.isSubmitting()).toBe(true);
                });

                it("to false after the data is sent", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    jasmine.Ajax.requests.mostRecent().response({ "status": 204 });
                    expect(vm.isSubmitting()).toBe(false);
                });
            });

            describe("sends the form data", function () {
                it("to the correct url", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("baseUrl/", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toEqual("baseUrl/" + "register/details");
                });

                it("as a POST request", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                });

                it("as JSON", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().requestHeaders["Content-Type"])
                        .toEqual("application/json");
                });

                it("with the correct content", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
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
            });

            describe("handles successful form submission by", function () {
                it("showing a success message", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 204 });

                    // Assert
                    expect(vm.notices()[0].priority).toEqual("success");
                    expect(vm.notices().length).toEqual(1);
                });

                it("redirecting to the home page", function () {
                    // Arrange
                    var redirect = jasmine.createSpy();
                    var vm = new AccountDetailsFormViewModel("baseUrl/", {}, redirect, diseasesVM);
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

            describe("handles failed form submission by", function () {
                it("showing all response messages as warnings", function () {
                    // Arrange
                    var vm = new AccountDetailsFormViewModel("", {}, noop, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response(
                        { "status": 400, "responseText": "[ \"abc\", \"def\" ]" });

                    // Assert
                    expect(vm.notices()).toContain({ message: "abc", priority: "warning" });
                    expect(vm.notices()).toContain({ message: "def", priority: "warning" });
                    expect(vm.notices().length).toEqual(2);
                });

                it("not redirecting to the home page", function () {
                    // Arrange
                    var redirect = jasmine.createSpy();
                    var vm = new AccountDetailsFormViewModel("", {}, redirect, diseasesVM);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    expect(redirect).not.toHaveBeenCalled();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 400, "responseText": "[]" });

                    // Assert
                    expect(redirect).not.toHaveBeenCalled();
                });

                it("redirecting to the account page if email address clashed", function () {
                    // Arrange
                    var redirect = jasmine.createSpy();
                    var vm = new AccountDetailsFormViewModel("baseUrl/", {}, redirect, diseasesVM);
                    vm.isValid = wrap(true);

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
});
