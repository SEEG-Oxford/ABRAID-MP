/* A suite of tests for the ExpertAdministrationViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/ExpertAdministrationViewModel",
    "app/BaseTableViewModel",
    "app/BaseFormViewModel",
    "app/spec/lib/squire",
    "underscore"
], function (DiseaseGroupAdministrationViewModel, BaseTableViewModel, BaseFormViewModel, Squire,  _) {
    "use strict";

    describe("The 'experts administration' view model", function () {
        var baseUrl = "baseUrl/";
        var experts = [
            {
                "name": "Helena Patching",
                "visibilityRequested": true,
                "jobTitle": "Software",
                "institution": "Seeg",
                "id": 1,
                "email": "zool1250@zoo.ox.ac.uk",
                "weighting": 1,
                "visibilityApproved": true,
                "createdDate": "2014-07-30T12:44:35.184Z",
                "updatedDate": "2014-07-30T12:44:35.184Z",
                "seegmember": true,
                "administrator": true
            },
            {
                "name": "Ed Wiles",
                "visibilityRequested": false,
                "jobTitle": "Software",
                "institution": "Seeg",
                "id": 2,
                "email": "zool1251@zoo.ox.ac.uk",
                "weighting": 0,
                "visibilityApproved": false,
                "createdDate": "2014-07-30T12:44:35.184Z",
                "updatedDate": "2014-07-30T12:44:35.184Z",
                "seegmember": false,
                "administrator": false
            }
        ];

        describe("has the behavior of BaseTableViewModel", function () {
            var vm;
            var baseSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseTableViewModel);
                    injector.mock("app/BaseTableViewModel", baseSpy);

                    injector.require(["app/admin/ExpertAdministrationViewModel"],
                            function (ExpertAdministrationViewModel) {
                            vm = new ExpertAdministrationViewModel(baseUrl, experts); // jshint ignore:line
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

            it("it specifies one entry per expert", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0].length).toBe(experts.length);
            });

            it("it specifies to start with 'updated date' sorting", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[1]).toBe("updatedDate");
            });

            it("it specifies to start with descending sort order", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[2]).toBe(true);
            });

            it("it specifies to filter on email, name, job and institution", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[3]).toContain("name");
                expect(args[3]).toContain("email");
                expect(args[3]).toContain("jobTitle");
                expect(args[3]).toContain("institution");
            });

            describe("it contains entries where", function () {
                it("the id, name, email, jobTitle, institution and visibilityRequested fields are copied from the experts", function () {  // jshint ignore:line
                    expect(vm.entries()[0]).toEqual(
                        jasmine.objectContaining(
                            _(experts[0]).pick("id", "name", "email", "jobTitle", "institution", "visibilityRequested")
                        )
                    );
                });

                it("the seegmember, administrator, visibilityApproved and weighting fields are observables copied from the experts", function () {  // jshint ignore:line
                    var row = vm.entries()[0];
                    var expected = experts[0];

                    //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                    //expect(row.seegmember).toBeObservable();
                    expect(row.seegmember()).toBe(expected.seegmember);

                    //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                    //expect(row.administrator).toBeObservable();
                    expect(row.administrator()).toBe(expected.administrator);

                    //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                    //expect(row.visibilityApproved).toBeObservable();
                    expect(row.visibilityApproved()).toBe(expected.visibilityApproved);

                    //should return once KO gets fixed https://github.com/knockout/knockout/issues/1344
                    //expect(row.weighting).toBeObservable();
                    expect(row.weighting()).toBe(expected.weighting.toString());
                });

                it("the createdDate and updatedDate fields are parsed from the experts", function () {
                    var row = vm.entries()[0];
                    var expected = experts[0];

                    expect(row.createdDate).toEqual(new Date(expected.createdDate));
                    expect(row.updatedDate).toEqual(new Date(expected.updatedDate));
                });

                it("the weighting field has suitable validation", function () {
                    var row = vm.entries()[0];

                    expect(row.weighting).toHaveValidationRule({name: "required", params: true});
                    expect(row.weighting).toHaveValidationRule({name: "number", params: true});
                    expect(row.weighting).toHaveValidationRule({name: "min", params: 0});
                    expect(row.weighting).toHaveValidationRule({name: "max", params: 1});
                });

                it("changes are recorded", function () {
                    var row = vm.entries()[0];

                    expect(row.changed).toBe(false);
                    row.seegmember(false);
                    expect(row.changed).toBe(true);
                    row.changed = false;

                    expect(row.changed).toBe(false);
                    row.administrator(false);
                    expect(row.changed).toBe(true);
                    row.changed = false;

                    expect(row.changed).toBe(false);
                    row.visibilityApproved(false);
                    expect(row.changed).toBe(true);
                    row.changed = false;

                    expect(row.changed).toBe(false);
                    row.weighting("0.5");
                    expect(row.changed).toBe(true);
                });
            });
        });

        describe("has the behavior of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy").and.callFake(BaseFormViewModel);
                    injector.mock("app/BaseFormViewModel", baseSpy);

                    injector.require(["app/admin/ExpertAdministrationViewModel"],
                        function (ExpertAdministrationViewModel) {
                            vm = new ExpertAdministrationViewModel(baseUrl, experts); // jshint ignore:line
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    _(vm.entries()).each(function (row) { row.changed = false; });
                    done();
                }
            });

            it("when created", function () {
                expect(baseSpy).toHaveBeenCalled();
            });

            it("it specifies the correct 'baseUrl'", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0]).toBe(baseUrl);
            });

            it("it specifies the correct form url", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[1]).toBe("admin/experts");
            });

            it("it specifies to send JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[2]).toBe(true);
            });

            it("it specifies to receive JSON", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[3]).toBe(true);
            });

            it("it uses the standard UI messages", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[4].fail).toBeUndefined();
                expect(args[4].success).toBeUndefined();
            });

            describe("overrides the 'buildSubmissionData' function, which", function () {
                it("generates the correct data object", function () {
                    var rows = _.sortBy(vm.entries(), "id");
                    rows[0].seegmember(false);
                    rows[1].weighting("0.5");
                    var data = _.sortBy(vm.buildSubmissionData(), "id");
                    expect(data).toEqual([
                        {
                            id: 1,
                            seegmember: false,
                            administrator: true,
                            visibilityApproved: true,
                            weighting: 1
                        },
                        {
                            id: 2,
                            seegmember: false,
                            administrator: false,
                            visibilityApproved: false,
                            weighting: 0.5
                        }
                    ]);
                });

                it("only contains changed rows", function () {
                    vm.entries()[1].changed = true;
                    var data = vm.buildSubmissionData();
                    expect(data.length).toBe(1);
                });
            });
        });
    });
});
