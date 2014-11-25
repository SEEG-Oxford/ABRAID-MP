/* Tests for BaseFileFormViewModel.
 * Copyright (c) 2014 University of Oxford
 */
/* global window:false */
define([
    "shared/app/BaseFileFormViewModel",
    "squire"
], function (BaseFileFormViewModel, Squire) {
    "use strict";

    describe("BaseFileFormViewModel", function () {

        describe("has a 'file' field, which", function () {
            it("is an observable", function () {
                var vm = new BaseFileFormViewModel();
                expect(vm.file).toBeObservable();
            });

            it("starts empty", function () {
                var vm = new BaseFileFormViewModel();
                expect(vm.file()).toBeUndefined();
            });

            it("is a required field", function () {
                var vm = new BaseFileFormViewModel();
                expect(vm.file).toHaveValidationRule({ name: "required", params: true });
            });
        });

        describe("has a 'useFormData' field, which", function () {
            it("gets a browser appropriate value", function () {
                var vm = new BaseFileFormViewModel();
                expect(vm.useFormData).toBe(window.FormData !== undefined);
            });
        });

        describe("extends BaseFormViewModel", function () {
            it("with appropriate arguments", function (done) {
                // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                var formSpy = jasmine.createSpy("formSpy");
                injector.mock("shared/app/BaseFormViewModel", formSpy);

                injector.require(["shared/app/BaseFileFormViewModel"],
                    function (BaseFileFormViewModel) {
                        new BaseFileFormViewModel("baseUrl", "targetUrl", { messages: true }, true); // jshint ignore:line

                        expect(formSpy).toHaveBeenCalledWith(
                            false, true, "baseUrl", "targetUrl", { messages: true }, true);

                        jasmine.Ajax.install();
                        done();
                    }
                );
            });

            describe("and overrides buildAjaxArgs", function () {
                it("to prevent jquery from messing up the AJAX request", function () {
                    var vm = new BaseFileFormViewModel("a", "b");
                    var args = vm.buildAjaxArgs();
                    expect(args.method).toBe("POST");
                    expect(args.url).toBe("ab");
                    expect(args.processData).toBe(false);
                    expect(args.contentType).toBe(false);
                });

                if (window.FormData !== undefined && !window._phantom) {
                    // Only run this test in environments that support it

                    it("to use FormData if available", function () {
                        var vm = new BaseFileFormViewModel("a", "b");
                        vm.buildSubmissionData = function () {
                            return { a: "b" };
                        };
                        vm.useFormData = true;
                        var args = vm.buildAjaxArgs();
                        expect(Object.getPrototypeOf(args.data).constructor).toBe(window.FormData);
                        expect(args.iframe).toBeUndefined();
                    });
                }

                it("to use iframe-transport if FormData not available", function () {
                    var vm = new BaseFileFormViewModel("a", "b");
                    vm.buildSubmissionData = function () { return { a: "b" }; };
                    vm.useFormData = false;
                    var args = vm.buildAjaxArgs();
                    expect(args.iframe).toBe(true);
                    expect(args.files).toBeDefined();
                    expect(args.data).toEqual({ a: "b" });
                });
            });

            describe("and overrides successHandler", function () {
                it("to add a postSuccessAction which is only called after 'true' successes", function () {
                    var vm = new BaseFileFormViewModel();
                    vm.postSuccessAction = jasmine.createSpy();
                    vm.successHandler("<div>" + JSON.stringify({ status: "FAIL", messages: ["a", "b"]}) + "</div>");
                    expect(vm.postSuccessAction).not.toHaveBeenCalled();
                    vm.successHandler("<div>" + JSON.stringify({ status: "SUCCESS", messages: ["a", "b"]}) + "</div>");
                    expect(vm.postSuccessAction).toHaveBeenCalled();
                });

                it("to distinguish real successes from iframe-transport false positives & extract the messages section for the result data", function () { // jshint ignore:line
                    var vm = new BaseFileFormViewModel();
                    vm.successHandler("<div>" + JSON.stringify({ status: "FAIL", messages: ["a", "b"]}) + "</div>");
                    expect(vm.notices()[0]).toEqual({ message : "Failed to save form.", priority : "warning" });
                    expect(vm.notices()[1]).toEqual({ message : "a", priority : "warning" });
                    expect(vm.notices()[2]).toEqual({ message : "b", priority : "warning" });
                    vm.notices([]);
                    vm.successHandler("<div>" + JSON.stringify({ status: "SUCCESS", messages: ["a", "b"]}) + "</div>");
                    expect(vm.notices()[0]).toEqual({ message : "Form saved successfully.", priority : "success" });
                    expect(vm.notices()[1]).toEqual({ message : "a", priority : "success" });
                    expect(vm.notices()[2]).toEqual({ message : "b", priority : "success" });
                });

                it("to handle bad JSON", function () {
                    var vm = new BaseFileFormViewModel();
                    vm.successHandler("{S : 2 sdfaasdfaiv}");
                    expect(vm.notices()[0]).toEqual({ message : "Failed to save form.", priority : "warning" });
                });
            });

            describe("and overrides failureHandler", function () {
                it("to extract the messages section for the result data", function () {
                    var vm = new BaseFileFormViewModel();
                    vm.failureHandler({ responseText: JSON.stringify({ status: "FAIL", messages: ["a", "b"]}) });
                    expect(vm.notices()[0]).toEqual({ message : "Failed to save form.", priority : "warning" });
                    expect(vm.notices()[1]).toEqual({ message : "a", priority : "warning" });
                    expect(vm.notices()[2]).toEqual({ message : "b", priority : "warning" });
                });

                it("to handle bad JSON", function () {
                    var vm = new BaseFileFormViewModel();
                    vm.failureHandler({ responseText: "<div class='not json'>404</div>" });
                    expect(vm.notices()[0]).toEqual({ message : "Failed to save form.", priority : "warning" });
                });
            });
        });
    });
});
