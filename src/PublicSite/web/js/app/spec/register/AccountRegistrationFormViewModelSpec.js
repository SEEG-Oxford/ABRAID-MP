/* A suite of tests for the AccountRegistrationFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/register/AccountRegistrationFormViewModel"], function (AccountRegistrationFormViewModel) {
    "use strict";
    var noop = function () {};
    var wrap = function (result) {
        // basic currying
        return function () {
            return result;
        };
    };
    var captcha = { get_response: wrap("value"), get_challenge: wrap("value"), reload: noop }; // jshint ignore:line
    
    describe("The account registration form view model", function () {
        describe("has an email field which", function () {
            it("is an observable", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.email).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", { email: "email" }, [], {}, noop);
                    expect(vm.email()).toBe("email");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                    expect(vm.email()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);

                it("be a mandatory requirement", function () {
                    expect(vm.email).toHaveValidationRule({name: "required", params: true});
                });

                it("be a valid email format", function () {
                    expect(vm.email).toHaveValidationRule({name: "email", params: true});
                });

                it("not be of an excessive length", function () {
                    expect(vm.email).toHaveValidationRule({name: "maxLength", params: 320});
                });
            });
        });

        describe("has a password field which", function () {
            it("is an observable", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.password).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the expert object passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", { password: "password" }, [], {}, noop);
                    expect(vm.password()).toBe("password");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                    expect(vm.password()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);

                it("be a mandatory requirement", function () {
                    expect(vm.password).toHaveValidationRule({name: "required", params: true});
                });

                it("be of reasonable complexity", function () {
                    expect(vm.password).toHaveValidationRule({name: "passwordComplexity", params: true});
                });
            });
        });

        describe("has a password confirmation field which", function () {
            it("is an observable", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.passwordConfirmation).toBeObservable();
            });

            describe("starts with", function () {
                it("the value of the password field on the expert object passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", { password: "password" }, [], {}, noop);
                    expect(vm.passwordConfirmation()).toBe("password");
                });

                it("empty string if no value is passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                    expect(vm.passwordConfirmation()).toBe("");
                });
            });

            describe("is validated to", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);

                it("be a mandatory requirement", function () {
                    expect(vm.passwordConfirmation).toHaveValidationRule({name: "required", params: true});
                });

                it("be of reasonable complexity", function () {
                    expect(vm.passwordConfirmation).toHaveValidationRule({name: "passwordComplexity", params: true});
                });

                it("be the same as the password field", function () {
                    expect(vm.passwordConfirmation).toHaveValidationRule({name: "areSame", params: vm.password});
                });
            });
        });

        describe("has a notices field which", function () {
            it("is an observable", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.notices).toBeObservable();
            });

            describe("starts with", function () {
                it("the value on the alerts passed to the constructor, expressed as warnings", function () {
                    var vm = new AccountRegistrationFormViewModel("", { }, [ "warning1" ], {}, noop);
                    expect(vm.notices()).toContain({ message: "warning1", priority: "warning" });
                });

                it("empty array if no value is passed to the constructor", function () {
                    var vm = new AccountRegistrationFormViewModel("", {}, undefined, {}, noop);
                    expect(vm.notices()).toEqual([]);
                });
            });
        });

        describe("has a isSubmitting field which", function () {
            it("is an observable", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.notices).toBeObservable();
            });

            it("starts with false", function () {
                var vm = new AccountRegistrationFormViewModel("", {}, [], {}, noop);
                expect(vm.isSubmitting()).toEqual(false);
            });
        });

        describe("exposes a submit function which", function () {
            it("clears any existing notices", function () {
                // Arrange
                var vm = new AccountRegistrationFormViewModel("", {}, [], { get_response: noop }, noop); // jshint ignore:line
                spyOn(vm.notices, "removeAll");

                // Act
                vm.submit();

                // Assert
                expect(vm.notices.removeAll).toHaveBeenCalled();
            });

            describe("ensures that", function () {
                it("a captcha response has been provided", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], { get_response: wrap("") }, noop); // jshint ignore:line

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
                    expect(vm.notices()).toContain({ message: "Captcha is required.", priority: "warning" });
                });

                it("the form fields are valid", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(false);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
                    expect(vm.notices())
                        .toContain({ message: "Fields must be valid before saving.", priority: "warning" });
                });
            });

            describe("updates the isSubmitting field", function () {
                it("to true when sending the data", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(vm.isSubmitting()).toBe(true);
                });

                it("to false after the data is sent", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
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
                    var vm = new AccountRegistrationFormViewModel("baseUrl/", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toEqual("baseUrl/" + "register/account");
                });

                it("as a POST request", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                });

                it("as JSON", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().requestHeaders["Content-Type"])
                        .toEqual("application/json");
                });

                it("with the correct content", function () {
                    // Arrange
                    var captcha = { reload: noop };
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    captcha.get_challenge = wrap("expected_challenge"); // jshint ignore:line
                    captcha.get_response = wrap("expected_response"); // jshint ignore:line
                    vm.email("expected_email");
                    vm.password("expected_password");
                    vm.passwordConfirmation("expected_confirmation");

                    // Act
                    vm.submit();

                    // Assert
                    var body = JSON.parse(jasmine.Ajax.requests.mostRecent().params);
                    expect(body.email).toEqual("expected_email");
                    expect(body.password).toEqual("expected_password");
                    expect(body.passwordConfirmation).toEqual("expected_confirmation");
                    expect(body.captchaChallenge).toEqual("expected_challenge");
                    expect(body.captchaResponse).toEqual("expected_response");
                });
            });

            describe("handles successful form submission by", function () {
                it("showing a success message", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 204 });

                    // Assert
                    expect(vm.notices()[0].priority).toEqual("success");
                    expect(vm.notices().length).toEqual(1);
                });

                it("resetting the captcha", function () {
                    // Arrange
                    var captcha =
                        { get_response: wrap("value"), get_challenge: wrap("value"), reload: jasmine.createSpy() };  // jshint ignore:line
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    expect(captcha.reload).not.toHaveBeenCalled();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 204 });

                    // Assert
                    expect(captcha.reload).toHaveBeenCalled();
                });

                it("redirecting to the details page", function () {
                    // Arrange
                    var redirect = jasmine.createSpy();
                    var vm = new AccountRegistrationFormViewModel("baseUrl/", {}, [], captcha, redirect);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    expect(redirect).not.toHaveBeenCalled();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 204 });

                    // Assert
                    expect(redirect).toHaveBeenCalled();
                    expect(redirect).toHaveBeenCalledWith("baseUrl/" + "register/details");
                });
            });

            describe("handles failed form submission by", function () {
                it("showing all response messages as warnings", function () {
                    // Arrange
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
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

                it("resetting the captcha", function () {
                    // Arrange
                    var captcha =
                    { get_response: wrap("value"), get_challenge: wrap("value"), reload: jasmine.createSpy() };  // jshint ignore:line
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, noop);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    expect(captcha.reload).not.toHaveBeenCalled();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 400, "responseText": "[]" });

                    // Assert
                    expect(captcha.reload).toHaveBeenCalled();
                });

                it("not redirecting to the details page", function () {
                    // Arrange
                    var redirect = jasmine.createSpy();
                    var vm = new AccountRegistrationFormViewModel("", {}, [], captcha, redirect);
                    vm.isValid = wrap(true);

                    // Act
                    vm.submit();
                    expect(redirect).not.toHaveBeenCalled();
                    jasmine.Ajax.requests.mostRecent().response({ "status": 400, "responseText": "[]" });

                    // Assert
                    expect(redirect).not.toHaveBeenCalled();
                });
            });
        });
    });
});
