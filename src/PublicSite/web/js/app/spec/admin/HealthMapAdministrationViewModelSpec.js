/* A suite of tests for the HealthMapAdministrationViewModel AMD.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "app/admin/HealthMapAdministrationViewModel"
], function (HealthMapAdministrationViewModel) {
    "use strict";

    describe("The 'HealthMapAdministrationViewModel' view model", function () {
        var hmDiseases = [
            { id: 1, name: "1", abraidDisease: { id: 11, name: "11" } },
            { id: 2, name: "2", abraidDisease: { id: 12, name: "12" } },
            { id: 3, name: "3", abraidDisease: { id: 13, name: "13" } },
            { id: 4, name: "4", abraidDisease: undefined }
        ];
        var hmSubdiseases = [
            { id: 101, name: "101", abraidDisease: { id: 11, name: "11" }, parent: { id: 1, name: "1" } },
            { id: 102, name: "102", abraidDisease: { id: 12, name: "12" }, parent: { id: 2, name: "2" }  },
            { id: 103, name: "103", abraidDisease: { id: 13, name: "13" }, parent: { id: 3, name: "3" }  },
            { id: 104, name: "104", abraidDisease: undefined, parent: { id: 3, name: "3" } },
            { id: 105, name: "105", abraidDisease: { id: 13, name: "13" }, parent: undefined }
        ];
        var abraidDisease = [
            { id: 11, name: "11" },
            { id: 12, name: "12" },
            { id: 13, name: "13" }
        ];

        var vm = new HealthMapAdministrationViewModel("base/", hmDiseases, hmSubdiseases, abraidDisease);

        describe("exposes the abraid disease list", function () {
            it("as a field", function () {
                expect(vm.abraidDiseases.length).toEqual(3);
            });
        });

        describe("exposes the healthmap disease list", function () {
            it("as a field", function () {
                expect(vm.healthMapDiseases.length).toEqual(4);
            });

            it("with the inner disease objects replaced", function () {
                expect(vm.healthMapDiseases[0].abraidDisease).toBe(abraidDisease[0]);
                expect(vm.healthMapDiseases[1].abraidDisease).toBe(abraidDisease[1]);
                expect(vm.healthMapDiseases[2].abraidDisease).toBe(abraidDisease[2]);
                expect(vm.healthMapDiseases[3].abraidDisease).toBe(undefined);
            });
        });

        describe("exposes the healthmap subdisease list", function () {
            it("as a field", function () {
                expect(vm.healthMapSubDiseases.length).toEqual(5);
            });

            it("with the inner disease objects replaced", function () {
                expect(vm.healthMapSubDiseases[0].abraidDisease).toBe(abraidDisease[0]);
                expect(vm.healthMapSubDiseases[0].parent).toBe(hmDiseases[0]);
                expect(vm.healthMapSubDiseases[1].abraidDisease).toBe(abraidDisease[1]);
                expect(vm.healthMapSubDiseases[1].parent).toBe(hmDiseases[1]);
                expect(vm.healthMapSubDiseases[2].abraidDisease).toBe(abraidDisease[2]);
                expect(vm.healthMapSubDiseases[2].parent).toBe(hmDiseases[2]);
                expect(vm.healthMapSubDiseases[3].abraidDisease).toBe(undefined);
                expect(vm.healthMapSubDiseases[3].parent).toBe(hmDiseases[2]);
                expect(vm.healthMapSubDiseases[4].abraidDisease).toBe(abraidDisease[2]);
                expect(vm.healthMapSubDiseases[4].parent).toBe(undefined);
            });
        });

        describe("exposes a subview model for the healthMapDiseasesTable", function () {
            it("has the behavior of BaseTableViewModel", function () {
                expect(vm.healthMapDiseasesTable.entries).toBeObservable();
                expect(vm.healthMapDiseasesTable.filter).toBeObservable();
                expect(vm.healthMapDiseasesTable.sortField).toBeObservable();
                expect(vm.healthMapDiseasesTable.reverseSort).toBeObservable();
                expect(vm.healthMapDiseasesTable.updateSort).toBeDefined();
                expect(vm.healthMapDiseasesTable.visibleEntries).toBeObservable();
            });

            it("it's has an entry for each healthmap disease", function () {
                expect(vm.healthMapDiseasesTable.entries().length).toBe(hmDiseases.length);
            });

            it("it's entries are HealthMapDiseaseRowViewModels", function () {
                expect(vm.healthMapDiseasesTable.entries()[0].abraidDiseaseNew()).toEqual({ id : 11, name : "11" });
                expect(vm.healthMapDiseasesTable.entries()[0].parentDiseaseNew()).toEqual(undefined);
            });

            it("it's entries submit to the correct endpoint", function () {
                expect(vm.healthMapDiseasesTable.entries()[0].buildSubmissionUrl())
                    .toEqual("base/admin/healthmap/updateDisease");
            });
        });
        describe("exposes a subview model for the healthMapSubdiseasesTable", function () {
            it("has the behavior of BaseTableViewModel", function () {
                expect(vm.healthMapSubdiseasesTable.entries).toBeObservable();
                expect(vm.healthMapSubdiseasesTable.filter).toBeObservable();
                expect(vm.healthMapSubdiseasesTable.sortField).toBeObservable();
                expect(vm.healthMapSubdiseasesTable.reverseSort).toBeObservable();
                expect(vm.healthMapSubdiseasesTable.updateSort).toBeDefined();
                expect(vm.healthMapSubdiseasesTable.visibleEntries).toBeObservable();
            });

            it("it's has an entry for each healthmap subdisease", function () {
                expect(vm.healthMapSubdiseasesTable.entries().length).toBe(hmSubdiseases.length);
            });

            it("it's entries are HealthMapDiseaseRowViewModels", function () {
                expect(vm.healthMapSubdiseasesTable.entries()[0].abraidDiseaseNew()).toEqual({ id : 11, name : "11" });
                expect(vm.healthMapSubdiseasesTable.entries()[0].parentDiseaseNew())
                    .toEqual({ id : 1, name : "1", abraidDisease : { id : 11, name : "11" } });
            });

            it("it's entries submit to the correct endpoint", function () {
                expect(vm.healthMapSubdiseasesTable.entries()[0].buildSubmissionUrl())
                    .toEqual("base/admin/healthmap/updateSubDisease");
            });
        });

        it("exposes a 'isSubmitting' field", function () {
            expect(vm.isSubmitting).toBe(false);
        });
    });
});
