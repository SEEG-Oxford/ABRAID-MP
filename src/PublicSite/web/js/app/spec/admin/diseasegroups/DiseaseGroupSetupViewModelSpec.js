/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupSetupViewModel",
    "ko"
], function (DiseaseGroupSetupViewModel, ko) {
    "use strict";

    describe("The 'disease group setup' view model", function () {
        var vm = {};
        var baseUrl = "http://abraid.zoo.ox.ac.uk/publicsite/";
        var selectedEventName = "selected";
        var savedEventName = "saved";
        beforeEach(function () {
            vm = new DiseaseGroupSetupViewModel(baseUrl, selectedEventName, savedEventName);
            vm.isValid = ko.observable(true);
        });

        describe("holds the select disease group ID which", function () {
            it("is an observable", function () {
                expect(vm.selectedDiseaseGroupId).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.selectedDiseaseGroupId()).toBeUndefined();
            });
        });

        describe("holds whether or not the model has been successfully run which", function () {
            it("is an observable", function () {
                expect(vm.hasModelBeenSuccessfullyRun).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.hasModelBeenSuccessfullyRun()).toBe(false);
            });
        });

        describe("holds the last model run text which", function () {
            it("is an observable", function () {
                expect(vm.lastModelRunText).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.lastModelRunText()).toBe("");
            });
        });

        describe("holds the disease occurrences text which", function () {
            it("is an observable", function () {
                expect(vm.diseaseOccurrencesText).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.diseaseOccurrencesText()).toBe("");
            });
        });

        describe("holds whether or not the model can be run (server response) which", function () {
            it("is an observable", function () {
                expect(vm.canRunModel).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.canRunModel()).toBe(false);
            });

            it("is required to be true for the view model to be valid", function () {
                expect(vm.canRunModel).toHaveValidationRule({name: "equal", params: true});
            });
        });

        describe("holds the batch end date which", function () {
            it("is an observable", function () {
                expect(vm.batchEndDate).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchEndDate()).toBe("");
            });

            it("has appropriate validation rules", function () {
                expect(vm.batchEndDate).toHaveValidationRule({name: "required", params: true});
                expect(vm.batchEndDate).toHaveValidationRule({name: "date", params: true});
            });
        });

        describe("holds the batch end date minimum which", function () {
            it("is an observable", function () {
                expect(vm.batchEndDateMinimum).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchEndDateMinimum()).toBe("");
            });
        });

        describe("holds the batch end date maximum which", function () {
            it("is an observable", function () {
                expect(vm.batchEndDateMaximum).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchEndDateMaximum()).toBe("");
            });
        });

        describe("has the behavior of BaseFormView model, but overrides to", function() {
            it("build a submission URL which is correct", function () {
                // Arrange
                vm.selectedDiseaseGroupId(10);
                var expectedUrl = "http://abraid.zoo.ox.ac.uk/publicsite/admin/diseasegroups/10/requestmodelrun";

                // Act
                var actualUrl = vm.buildSubmissionUrl();

                // Assert
                expect(actualUrl).toBe(expectedUrl);
            });

            it("build submission data which is correct", function () {
                // Arrange
                vm.batchEndDate("10 Jul 2014");
                var expectedBatchEndDate = "2014-07-10T00:00:00";

                // Act
                var actualData = vm.buildSubmissionData();

                // Assert
                expect(actualData.batchEndDate).toContain(expectedBatchEndDate);
            });
        });

        it("has a 'resetState' method, which clears the state of the view model fields", function () {
            // Arrange
            vm.lastModelRunText("a");
            vm.diseaseOccurrencesText("b");
            vm.batchEndDate("c");
            vm.batchEndDateMinimum("d");
            vm.batchEndDateMaximum("e");
            vm.hasModelBeenSuccessfullyRun(true);
            vm.canRunModel(true);

            // Act
            vm.resetState(undefined);

            // Assert
            expect(vm.lastModelRunText()).toBe("");
            expect(vm.diseaseOccurrencesText()).toBe("");
            expect(vm.batchEndDate()).toBe("");
            expect(vm.batchEndDateMinimum()).toBe("");
            expect(vm.batchEndDateMaximum()).toBe("");
            expect(vm.hasModelBeenSuccessfullyRun()).toBe(false);
            expect(vm.canRunModel()).toBe(false);
        });

        describe("has a 'updateModelRunInfo' method, which", function () {
            it("is called in response to the 'disease group selected' event", function () {
                // Arrange
                var diseaseGroupId = 1;
                spyOn(vm, "updateModelRunInfo");

                // Act
                ko.postbox.publish(selectedEventName, { id: diseaseGroupId });

                // Assert
                expect(vm.updateModelRunInfo).toHaveBeenCalledWith(diseaseGroupId);
            });

            it("is called in response to the 'disease group saved' event", function () {
                // Arrange
                var diseaseGroupId = 1;
                spyOn(vm, "updateModelRunInfo");

                // Act
                ko.postbox.publish(savedEventName, diseaseGroupId);

                // Assert
                expect(vm.updateModelRunInfo).toHaveBeenCalledWith(diseaseGroupId);
            });

            it("updates the 'selected disease group id'", function () {
                vm.updateModelRunInfo(1);
                expect(vm.selectedDiseaseGroupId()).toBe(1);
                vm.updateModelRunInfo(undefined);
                expect(vm.selectedDiseaseGroupId()).toBe(undefined);
                vm.updateModelRunInfo(999);
                expect(vm.selectedDiseaseGroupId()).toBe(999);
            });

            it("clears the state of view model", function () {
                // Arrange
                spyOn(vm, "resetState");

                // Act
                vm.updateModelRunInfo(1);

                // Assert
                expect(vm.resetState).toHaveBeenCalled();
            });

            describe("updates the view model 'busy' state to", function () {
                it("busy when the update starts", function () {
                    // Arrange
                    vm.updateModelRunInfo(1);

                    // Assert
                    expect(vm.isSubmitting()).toBe(true);
                    expect(vm.notices().length).toBe(0);
                });

                it("not busy when the update completes", function () {
                    // Act
                    vm.updateModelRunInfo(1);
                    jasmine.Ajax.requests.mostRecent().response({"status": 500});

                    // Assert
                    expect(vm.isSubmitting()).toBe(false);
                });
            });

            describe("fetches the last model run information, which", function () {
                it("is skipped if no disease group id is specified", function () {
                    // Act
                    vm.updateModelRunInfo(undefined);

                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent()).toBeUndefined();
                });

                it("sends a GET request to the expected URL", function () {
                    // Arrange
                    var diseaseGroupId = 1;
                    var expectedUrl = baseUrl + "admin/diseasegroups/" + diseaseGroupId + "/modelruninformation";

                    // Act
                    vm.updateModelRunInfo(diseaseGroupId);

                    // Arrange
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                });

                it("updates the view model fields with the obtained data", function () {
                    // Arrange
                    var diseaseGroupId = 1;
                    var response = {
                        lastModelRunText: "a",
                        diseaseOccurrencesText: "b",
                        batchEndDateDefault: "c",
                        batchEndDateMinimum: "d",
                        batchEndDateMaximum: "e",
                        hasModelBeenSuccessfullyRun: "f",
                        canRunModel: "g"
                    };

                    // Act
                    vm.updateModelRunInfo(diseaseGroupId);
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        responseText: JSON.stringify(response),
                        contentType: "application/json"
                    });

                    // Arrange
                    expect(vm.lastModelRunText()).toBe(response.lastModelRunText);
                    expect(vm.diseaseOccurrencesText()).toBe(response.diseaseOccurrencesText);
                    expect(vm.batchEndDate()).toBe(response.batchEndDateDefault);
                    expect(vm.batchEndDateMinimum()).toBe(response.batchEndDateMinimum);
                    expect(vm.batchEndDateMaximum()).toBe(response.batchEndDateMaximum);
                    expect(vm.hasModelBeenSuccessfullyRun()).toBe(response.hasModelBeenSuccessfullyRun);
                    expect(vm.canRunModel()).toBe(response.canRunModel);

                    expect(vm.notices().length).toBe(0);
                });

                it("shows a message if data indicates that the model cannot be run", function () {
                    // Arrange
                    var diseaseGroupId = 1;
                    var response = {
                        canRunModel: false,
                        cannotRunModelReason: "abc"
                    };

                    // Act
                    vm.updateModelRunInfo(diseaseGroupId);
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        responseText: JSON.stringify(response),
                        contentType: "application/json"
                    });

                    // Assert
                    expect(vm.notices()).toContain({
                        message: "Cannot run model because " + response.cannotRunModelReason,
                        priority: "warning"
                    });
                });

                it("shows a message if the data can not be obtained", function () {
                    // Arrange
                    var diseaseGroupId = 1;

                    // Act
                    vm.updateModelRunInfo(diseaseGroupId);
                    jasmine.Ajax.requests.mostRecent().response({"status": 400});

                    // Assert
                    expect(vm.notices()).toContain({
                        message: "Could not retrieve model run details.",
                        priority: "warning"
                    });
                });
            });
        });
    });
});
