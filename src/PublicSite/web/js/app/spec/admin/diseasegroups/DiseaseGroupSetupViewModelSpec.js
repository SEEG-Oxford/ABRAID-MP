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

        describe("holds the selected disease group ID which", function () {
            it("is an observable", function () {
                expect(vm.selectedDiseaseGroupId).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.selectedDiseaseGroupId()).toBeUndefined();
            });
        });

        describe("holds whether or not automatic model runs have been enabled for the disease group", function () {
            it("is an observable", function () {
                expect(vm.isAutomaticModelRunsEnabled).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isAutomaticModelRunsEnabled()).toBe(false);
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
        });

        describe("holds the batch start date which", function () {
            it("is an observable", function () {
                expect(vm.batchStartDate).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchStartDate()).toBe("");
            });

            it("has appropriate validation rules", function () {
                expect(vm.batchStartDate).toHaveValidationRule({name: "required", params: true});
                expect(vm.batchStartDate).toHaveValidationRule({name: "date", params: true});
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

        describe("holds the batch date minimum which", function () {
            it("is an observable", function () {
                expect(vm.batchDateMinimum).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchDateMinimum()).toBe("");
            });
        });

        describe("holds the batch date maximum which", function () {
            it("is an observable", function () {
                expect(vm.batchDateMaximum).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.batchDateMaximum()).toBe("");
            });
        });

        describe("holds whether or not the disease group has 'gold standard' occurrences which", function () {
            it("is an observable", function () {
                expect(vm.hasGoldStandardOccurrences).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.hasGoldStandardOccurrences()).toBe(false);
            });
        });

        describe("holds whether or not to use a disease group's 'gold standard' occurrences which", function () {
            it("is an observable", function () {
                expect(vm.useGoldStandardOccurrences).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.useGoldStandardOccurrences()).toBe(false);
            });
        });

        describe("holds whether to disable the button that generates disease extents, which", function () {
            it("disables if the model cannot be run", function () {
                vm.canRunModel(false);
                vm.hasModelBeenSuccessfullyRun(false);
                vm.isSubmitting(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatGeneratesDiseaseExtent()).toBe(true);
            });

            it("disables if the model has been successfully run at least once", function () {
                vm.canRunModel(true);
                vm.hasModelBeenSuccessfullyRun(true);
                vm.isSubmitting(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatGeneratesDiseaseExtent()).toBe(true);
            });

            it("disables if we are submitting", function () {
                vm.canRunModel(true);
                vm.hasModelBeenSuccessfullyRun(false);
                vm.isSubmitting(true);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatGeneratesDiseaseExtent()).toBe(true);
            });

            it("disables if we are generating the disease extent", function () {
                vm.canRunModel(true);
                vm.hasModelBeenSuccessfullyRun(false);
                vm.isSubmitting(false);
                vm.isGeneratingDiseaseExtent(true);
                expect(vm.disableButtonThatGeneratesDiseaseExtent()).toBe(true);
            });

            it("enables if the disease extent should be able to be generated", function () {
                vm.canRunModel(true);
                vm.hasModelBeenSuccessfullyRun(false);
                vm.isSubmitting(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatGeneratesDiseaseExtent()).toBe(false);
            });
        });

        describe("holds whether to disable the button that runs the model, which", function () {
            // NB. Dates must be numbers, not date string, to allow for comparison in min/max rule and be valid.
            it("enables if not using gold standard occurrences and we should be able to run the model", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchStartDate(9);
                vm.batchEndDate(10);
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(false);
            });

            it("enables if using gold standard occurrences and we should be able to run the model", function () {
                vm.useGoldStandardOccurrences(true);
                vm.batchStartDate(9);
                vm.batchEndDate(10);
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(false);
            });

            it("disables if not using gold standard occurrences and the start date is invalid", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchStartDate(10);
                vm.batchEndDate(9);
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });

            it("disables if not using gold standard occurrences and the end date is invalid", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchStartDate(9);
                vm.batchEndDate("");
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });

            it("disables if the model cannot be run", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchEndDate("10 Jul 2014");
                vm.canRunModel(false);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });

            it("disables if we are submitting", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchEndDate("10 Jul 2014");
                vm.canRunModel(true);
                vm.isSubmitting(true);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });

            it("disables if we are enabling automatic model runs", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchEndDate("10 Jul 2014");
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(true);
                vm.isGeneratingDiseaseExtent(false);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });

            it("disables if we are generating the disease extent", function () {
                vm.useGoldStandardOccurrences(false);
                vm.batchEndDate("10 Jul 2014");
                vm.canRunModel(true);
                vm.isSubmitting(false);
                vm.isEnablingAutomaticModelRuns(false);
                vm.isGeneratingDiseaseExtent(true);
                expect(vm.disableButtonThatRunsModel()).toBe(true);
            });
        });

        describe("has the behaviour of BaseFormView model, but overrides to", function () {
            it("build a submission URL which is correct", function () {
                // Arrange
                vm.selectedDiseaseGroupId(10);
                var expectedUrl = "http://abraid.zoo.ox.ac.uk/publicsite/admin/diseases/10/requestmodelrun";

                // Act
                var actualUrl = vm.buildSubmissionUrl();

                // Assert
                expect(actualUrl).toBe(expectedUrl);
            });

            it("build submission data which is correct", function () {
                // Arrange
                vm.batchEndDate("10 Jul 2014");
                vm.useGoldStandardOccurrences(true);
                var expectedBatchEndDate = "2014-07-10T00:00:00";

                // Act
                var actualData = vm.buildSubmissionData();

                // Assert
                expect(actualData.batchEndDate).toContain(expectedBatchEndDate);
                expect(actualData.useGoldStandardOccurrences).toBe(true);
            });
        });

        it("has a 'resetState' method, which clears the state of the view model fields", function () {
            // Arrange
            vm.lastModelRunText("a");
            vm.diseaseOccurrencesText("b");
            vm.batchEndDate("c");
            vm.batchDateMinimum("d");
            vm.batchDateMaximum("e");
            vm.hasModelBeenSuccessfullyRun(true);
            vm.canRunModel(true);
            vm.hasGoldStandardOccurrences(false);
            vm.useGoldStandardOccurrences(false);

            // Act
            vm.resetState(undefined);

            // Assert
            expect(vm.lastModelRunText()).toBe("");
            expect(vm.diseaseOccurrencesText()).toBe("");
            expect(vm.batchEndDate()).toBe("");
            expect(vm.batchDateMinimum()).toBe("");
            expect(vm.batchDateMaximum()).toBe("");
            expect(vm.hasModelBeenSuccessfullyRun()).toBe(false);
            expect(vm.canRunModel()).toBe(false);
            expect(vm.hasGoldStandardOccurrences()).toBe(false);
            expect(vm.useGoldStandardOccurrences()).toBe(false);
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
                    var expectedUrl = baseUrl + "admin/diseases/" + diseaseGroupId + "/modelruninformation";

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
                        batchDateMinimum: "d",
                        batchDateMaximum: "e",
                        hasModelBeenSuccessfullyRun: "f",
                        canRunModel: "g",
                        hasGoldStandardOccurrences: "h"
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
                    expect(vm.batchDateMinimum()).toBe(response.batchDateMinimum);
                    expect(vm.batchDateMaximum()).toBe(response.batchDateMaximum);
                    expect(vm.hasModelBeenSuccessfullyRun()).toBe(response.hasModelBeenSuccessfullyRun);
                    expect(vm.canRunModel()).toBe(response.canRunModel);
                    expect(vm.hasGoldStandardOccurrences()).toBe(response.hasGoldStandardOccurrences);

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
                        message: "Cannot run model or generate disease extent because " + response.cannotRunModelReason,
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

        describe("has a method to generate a disease extent which", function () {
            it("POSTs to the expected URL", function () {
                // Arrange
                var id = 1;
                vm.selectedDiseaseGroupId(id);
                vm.useGoldStandardOccurrences(true);
                var expectedUrl = baseUrl + "admin/diseases/" + id + "/generatediseaseextent";
                var expectedParams = "useGoldStandardOccurrences=true";

                // Act
                vm.generateDiseaseExtent();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
                expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
            });

            it("when successful, updates the 'notices' with a success message", function () {
                // Arrange
                var id = 1;
                vm.selectedDiseaseGroupId(id);
                var expectedNotice = { message: "Disease extent generated.", priority: "success" };

                // Act
                vm.generateDiseaseExtent();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });

                // Assert
                expect(vm.notices()).toContain(expectedNotice);
            });

            it("when unsuccessful, updates the 'notices' with an error", function () {
                // Arrange
                var expectedNotice = { message: "Server error.", priority: "warning" };
                // Act
                vm.generateDiseaseExtent();
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(vm.notices()).toContain(expectedNotice);
            });
        });

        describe("has a method to enable automatic model runs which", function () {
            it("POSTs to the expected URL", function () {
                // Arrange
                var id = 1;
                vm.selectedDiseaseGroupId(id);
                var expectedUrl = baseUrl + "admin/diseases/" + id + "/automaticmodelruns";

                // Act
                vm.enableAutomaticModelRuns();

                // Assert
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
        });
    });
});