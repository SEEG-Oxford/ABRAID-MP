/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupSetupViewModel",
    "app/BaseFormViewModel",
    "ko",
    "underscore",
    "app/spec/lib/squire"
], function (DiseaseGroupSetupViewModel, BaseFormViewModel, ko, _, Squire) {
    "use strict";

    describe("The 'disease group setup' view model", function () {
        var vm = {};
        var baseUrl = "";
        var selectedEventName = "selected";
        var savedEventName = "saved";

        describe("has the BaseFormViewModel behavior which", function () {
            var vm;
            var formSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    formSpy = jasmine.createSpy("formSpy").and.callFake(BaseFormViewModel);
                    injector.mock("app/BaseFormViewModel", formSpy);

                    injector.require(["app/admin/diseasegroups/DiseaseGroupSetupViewModel"],
                        function (DiseaseGroupSetupViewModel) {
                            vm = new DiseaseGroupSetupViewModel(baseUrl, selectedEventName, savedEventName);
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

            it("only receives JSON", function () {
                expect(formSpy.calls.argsFor(0)[0]).toEqual(false);
                expect(formSpy.calls.argsFor(0)[1]).toEqual(true);
            });

            it("builds the correct submission url", function () {
                var id = 1;
                vm.selectedDiseaseGroupId(id);
                expect(vm.buildSubmissionUrl()).toEqual(baseUrl + "admin/diseasegroups/" + id + "/requestmodelrun");

                // A custom buildSubmissionUrl was constructed in DiseaseGroupSetupViewModel,
                // so baseUrl and targetUrl arguments to BaseFormViewModel are undefined
                expect(formSpy.calls.argsFor(0)[2]).toEqual(undefined);
                expect(formSpy.calls.argsFor(0)[3]).toEqual(undefined);
            });

            it("builds the correct submission data", function () {
                var date = "19 Aug 2014";
                vm.batchEndDate(date);
                expect(vm.buildSubmissionData().batchEndDate).toBeDefined();
                expect(vm.buildSubmissionData().batchEndDate).toContain("2014-08-19T00:00:00");
            });

            it("uses the provided messages", function () {
                expect(formSpy.calls.argsFor(0)[4]).toEqual({ success: "Model run requested." });
            });

            it("excludes the generic failure message", function () {
                expect(formSpy.calls.argsFor(0)[5]).toEqual(true);
            });
        });

        describe("has the expected extended behaviour. It", function () {
            beforeEach(function () {
                vm = new DiseaseGroupSetupViewModel(baseUrl, selectedEventName, savedEventName);
                vm.isValid = ko.observable(true);
            });

            describe("holds the expected state fields", function () {
                it("as observables", function () {
                    expect(vm.selectedDiseaseGroupId).toBeObservable();
                    expect(vm.isAutomaticModelRunsEnabled).toBeObservable();
                    expect(vm.hasModelBeenSuccessfullyRun).toBeObservable();
                    expect(vm.lastModelRunText).toBeObservable();
                    expect(vm.diseaseOccurrencesText).toBeObservable();
                    expect(vm.canRunModelServerResponse).toBeObservable();
                    expect(vm.batchEndDate).toBeObservable();
                    expect(vm.batchEndDateMinimum).toBeObservable();
                    expect(vm.batchEndDateMaximum).toBeObservable();
                });

                it("with the expected validation rules specified", function () {
                    expect(vm.canRunModelServerResponse).toHaveValidationRule({ name: "equal", params: true });
                    expect(vm.batchEndDate).toHaveValidationRule({name: "required", params: true});
                    expect(vm.batchEndDate).toHaveValidationRule({name: "date", params: true});
                });
            });

            describe("has a method to enable automatic model runs which", function () {
                it("POSTs to the expected URL", function () {
                    // Arrange
                    var id = 1;
                    vm.selectedDiseaseGroupId(id);
                    var expectedUrl = baseUrl + "admin/diseasegroups/" + id + "/automaticmodelruns";

                    // Act
                    vm.enableAutomaticModelRuns();

                    // Arrange
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                });

                it("when successful, updates the isAutomaticModelRunsEnabled flag", function () {
                    // Arrange
                    vm.selectedDiseaseGroupId(1);
                    vm.isAutomaticModelRunsEnabled(false);
                    // Act
                    vm.enableAutomaticModelRuns();
                    jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                    // Assert
                    expect(vm.isAutomaticModelRunsEnabled()).toBe(true);
                });

                it("when unsuccessful, updates the 'notices' with an error", function () {
                    // Arrange
                    var expectedNotice = { message: "Server error.", priority: "warning" };
                    // Act
                    vm.enableAutomaticModelRuns();
                    jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                    // Assert
                    expect(vm.notices()).toContain(expectedNotice);
                });

                describe("responds to the 'disease group selected' event by", function () {
                    // Arrange
                    var diseaseGroupId = 1;
                    var diseaseGroup = { id: diseaseGroupId };

                    it("updating the expected parameters", function () {
                        // Act
                        ko.postbox.publish(selectedEventName, { id: diseaseGroupId, automaticModelRuns: false });

                        // Assert
                        expect(vm.selectedDiseaseGroupId()).toBe(diseaseGroupId);
                        expect(vm.isAutomaticModelRunsEnabled()).toBe(false);
                        expect(vm.isSubmitting()).toBe(true);
                        expect(vm.notices().length).toBe(0);
                    });

                    it("GETing from the expected URL", function () {
                        // Arrange
                        var expectedUrl = baseUrl + "admin/diseasegroups/" + diseaseGroupId + "/modelruninformation";

                        // Act
                        ko.postbox.publish(selectedEventName, diseaseGroup);

                        // Assert
                        expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                        expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                    });

                    describe("updating the fields with the values from the response data when successful", function () {
                        // Arrange
                        var baseData = {
                            lastModelRunText: "lastModelRunText",
                            diseaseOccurrencesText: "diseaseOccurrencesText",
                            batchEndDateDefault: "batchEndDateDefault",
                            batchEndDateMinimum: "batchEndDateMinimum",
                            batchEndDateMaximum: "batchEndDateMaximum",
                            hasModelBeenSuccessfullyRun: "hasModelBeenSuccessfullyRun"
                        };

                        it("if the model cannot run", function () {
                            // Arrange
                            var reason = "something is wrong";
                            var responseJson = _(baseData).extend({ canRunModel: false, cannotRunModelReason: reason });
                            var responseText = JSON.stringify(responseJson);

                            // Act
                            ko.postbox.publish(selectedEventName, diseaseGroup);
                            jasmine.Ajax.requests.mostRecent().response({ status : 200, responseText : responseText });

                            // Assert
                            expectBaseData();
                            expect(vm.canRunModelServerResponse()).toBe(false);
                            expect(vm.notices().length).toBe(1);
                            expect(vm.notices()[0]).toEqual(
                                { message: "Cannot run model because " + reason, priority: "warning" }
                            );
                        });

                        it("if the model can run", function () {
                            // Arrange
                            var responseJson = _(baseData).extend({ canRunModel: true });
                            var responseText = JSON.stringify(responseJson);

                            // Act
                            ko.postbox.publish(selectedEventName, diseaseGroup);
                            jasmine.Ajax.requests.mostRecent().response({ status: 200, responseText : responseText });

                            // Assert
                            expectBaseData();
                            expect(vm.canRunModelServerResponse()).toBe(true);
                            expect(vm.notices().length).toBe(0);
                        });
                    });

                    it("updating the notice with the error message when unsuccessful", function () {
                        // Act
                        ko.postbox.publish(selectedEventName, diseaseGroup);
                        jasmine.Ajax.requests.mostRecent().response({ status: 500 });

                        // Assert
                        expect(vm.notices().length).toBe(1);
                        expect(vm.notices()[0]).toEqual(
                            { message: "Could not retrieve model run details.", priority: "warning" }
                        );
                    });
                });

                var expectBaseData = function () {
                    expect(vm.lastModelRunText()).toBe("lastModelRunText");
                    expect(vm.diseaseOccurrencesText()).toBe("diseaseOccurrencesText");
                    expect(vm.batchEndDate()).toBe("batchEndDateDefault");
                    expect(vm.batchEndDateMinimum()).toBe("batchEndDateMinimum");
                    expect(vm.batchEndDateMaximum()).toBe("batchEndDateMaximum");
                };

                describe("responds to the 'disease group saved' event by", function () {
                    it("updating the expected parameters", function () {
                        // Arrange
                        var diseaseGroupId = 1;

                        // Act
                        ko.postbox.publish(savedEventName, diseaseGroupId);

                        // Assert
                        expect(vm.selectedDiseaseGroupId()).toBe(diseaseGroupId);
                        expect(vm.isSubmitting()).toBe(true);
                        expect(vm.notices().length).toBe(0);
                    });

                    it("GETing from the expected URL", function () {
                        // Arrange
                        var diseaseGroupId = 1;
                        var expectedUrl = baseUrl + "admin/diseasegroups/" + diseaseGroupId + "/modelruninformation";

                        // Act
                        ko.postbox.publish(savedEventName, diseaseGroupId);

                        // Arrange
                        expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                        expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                    });
                });
            });
        });
    });
});
