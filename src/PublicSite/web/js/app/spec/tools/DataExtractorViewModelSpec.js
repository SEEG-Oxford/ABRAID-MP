/* Tests for UploadCsvViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/tools/DataExtractorViewModel",
    "ko",
    "squire"
], function (DataExtractorViewModel, ko, Squire) {
    "use strict";

    describe("The 'data extractor' view model", function () {
        var vm;

        var run1 = { name: "run1", disease: "diseaseB1", date: "2015-01-02" };
        var run2 = { name: "run2", disease: "diseaseA2", date: "2015-01-03" };
        var country1 = { name: "country1", gaulCode: 11, maxX: 1, minX: -1, maxY: 2, minY: -2 };
        var country2 = { name: "country2", gaulCode: 12, maxX: 3, minX: -3, maxY: 2, minY: -2 };
        var wmsFactory = {};
        beforeEach(function () {
            wmsFactory.createLayerParametersForCroppedDownload =
                jasmine.createSpy("wmsFactory").and.returnValue({downloadParams: true});
            vm = new DataExtractorViewModel("baseUrl/", "wmsUrl", [run1, run2], [country1, country2], wmsFactory);
        });

        describe("is an extension of BaseFormViewModel, with", function () {
            it("the correct constructor arguments", function (done) {
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                var baseSpy = jasmine.createSpy("baseSpy");
                injector.mock("shared/app/BaseFormViewModel", baseSpy);

                injector.require(["app/tools/DataExtractorViewModel"],
                    function (DataExtractorViewModel) {
                        vm = new DataExtractorViewModel(
                            "baseUrl/", "wmsUrl", [run1, run2], [country1, country2], wmsFactory);
                        expect(baseSpy.calls.argsFor(0)[0]).toBe(false); // Dont send json
                        expect(baseSpy.calls.argsFor(0)[1]).toBe(false); // Dont receive json
                        expect(baseSpy.calls.argsFor(0)[2]).toBe("baseUrl/");
                        expect(baseSpy.calls.argsFor(0)[3]).toBe("tools/location/precise");
                        expect(baseSpy.calls.argsFor(0)[4]).toEqual({}); // No custom messages
                        expect(baseSpy.calls.argsFor(0)[5]).toEqual(false); // Default failure messages
                        expect(baseSpy.calls.argsFor(0)[6]).toEqual(false); // Allow repeat submission
                        expect(baseSpy.calls.argsFor(0)[7]).toEqual("GET"); // HTTP Method

                        done();
                    }
                );
            });

            it("a custom buildSubmissionData function, which builds the correct data", function () {
                vm.lat(123);
                vm.lng(321);
                vm.run({name: "foo"});
                expect(vm.buildSubmissionData()).toEqual({
                    lat: 123,
                    lng: 321,
                    run: "foo"
                });
            });

            it("a custom successHandler function, which sets the score value", function () {
                vm.score("123456");
                vm.successHandler("654321");
                expect(vm.score()).toEqual("654321");
                vm.successHandler("-9999.0");
                expect(vm.score()).toEqual("No data");
            });
        });

        describe("holds an 'score' field which", function () {
            it("is observable", function () {
                expect(vm.score).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.score()).toBeUndefined();
            });
        });

        describe("holds a 'diseases' field which", function () {
            it("contains the disease for the specified model runs, sorted by name", function () {
                expect(vm.diseases).toEqual(["diseaseA2", "diseaseB1"]);
            });
        });

        describe("holds a 'disease' field which", function () {
            it("is observable", function () {
                expect(vm.disease).toBeObservable();
            });
            it("defaults to the first disease", function () {
                expect(vm.disease()).toEqual("diseaseA2");
            });
            it("defaults to dengue if present", function () {
                var run3 = { name: "run3", disease: "dengue", date: "2015-01-03" };
                var run4 = { name: "run4", disease: "aaaa", date: "2015-01-03" };
                vm = new DataExtractorViewModel(
                    "baseUrl/", "wmsUrl", [run1, run2, run3, run4], [country1, country2], wmsFactory);
                expect(vm.disease()).toEqual("dengue");
            });
        });

        describe("holds a 'countries' field which", function () {
            it("contains the countries specified, sorted by name ", function () {
                var country3 = { name: "aaaa", gaulCode: 12, maxX: 3, minX: -3, maxY: 2, minY: -2 };
                vm = new DataExtractorViewModel(
                    "baseUrl/", "wmsUrl", [run1, run2], [country1, country2, country3], wmsFactory);
                expect(vm.countries).toEqual([country3, country1, country2]);
            });
        });

        describe("holds a 'country' field which", function () {
            it("is observable", function () {
                expect(vm.country).toBeObservable();
            });

            it("defaults to the first country", function () {
                expect(vm.country()).toEqual(country1);
            });

            it("defaults to Afghanistan if present", function () {
                var country3 = { name: "Afghanistan", gaulCode: 12, maxX: 3, minX: -3, maxY: 2, minY: -2 };
                var country4 = { name: "aaaa", gaulCode: 12, maxX: 3, minX: -3, maxY: 2, minY: -2 };
                vm = new DataExtractorViewModel(
                    "baseUrl/", "wmsUrl", [run1, run2], [country1, country2, country3, country4], wmsFactory);
                expect(vm.country()).toEqual(country3);
            });
        });

        describe("holds a 'runs' field which", function () {
            it("is observable", function () {
                expect(vm.runs).toBeObservable();
            });

            it("contains the specified runs, filtered to match the current diseases, ordered by date", function () {
                var run3 = { name: "run3", disease: "dengue", date: "2015-01-03" };
                var run4 = { name: "run4", disease: "dengue", date: "2015-01-04" };
                var run5 = { name: "run5", disease: "dengue", date: "2015-01-02" };
                vm = new DataExtractorViewModel(
                    "baseUrl/", "wmsUrl", [run1, run2, run3, run4, run5], [country1, country2], wmsFactory);
                expect(vm.runs()).toEqual([run4, run3, run5]);
                vm.disease("diseaseA2");
                expect(vm.runs()).toEqual([run2]);
            });
        });

        describe("holds a 'run' field which", function () {
            it("is observable", function () {
                expect(vm.run).toBeObservable();
            });

            it("defaults to the first run in runs", function () {
                var run3 = { name: "run3", disease: "dengue", date: "2015-01-03" };
                var run4 = { name: "run4", disease: "dengue", date: "2015-01-04" };
                var run5 = { name: "run5", disease: "dengue", date: "2015-01-02" };
                vm = new DataExtractorViewModel(
                    "baseUrl", "wmsUrl", [run1, run2, run3, run4, run5], [country1, country2], wmsFactory);
                expect(vm.run()).toEqual(run4);
            });
        });

        describe("holds a 'run' field which", function () {
            it("is observable", function () {
                expect(vm.run).toBeObservable();
            });

            it("defaults to the first run in runs", function () {
                var run3 = { name: "run3", disease: "dengue", date: "2015-01-03" };
                var run4 = { name: "run4", disease: "dengue", date: "2015-01-04" };
                var run5 = { name: "run5", disease: "dengue", date: "2015-01-02" };
                vm = new DataExtractorViewModel(
                    "baseUrl", "wmsUrl", [run1, run2, run3, run4, run5], [country1, country2], wmsFactory);
                expect(vm.run()).toEqual(run4);
            });
        });

        describe("holds a 'lat' field which", function () {
            it("is observable", function () {
                expect(vm.lat).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.lat()).toBeUndefined();
            });

            it("is correctly validated", function () {
                expect(vm.lat).toHaveValidationRule({name: "min", params: -60});
                expect(vm.lat).toHaveValidationRule({name: "max", params: 85});
                expect(vm.lat).toHaveValidationRule({name: "required", params: true});
                expect(vm.lat).toHaveValidationRule({name: "number", params: true});
            });
        });

        describe("holds a 'lng' field which", function () {
            it("is observable", function () {
                expect(vm.lng).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.lng()).toBeUndefined();
            });

            it("is correctly validated", function () {
                expect(vm.lng).toHaveValidationRule({name: "min", params: -180});
                expect(vm.lng).toHaveValidationRule({name: "max", params: 180});
                expect(vm.lng).toHaveValidationRule({name: "required", params: true});
                expect(vm.lng).toHaveValidationRule({name: "number", params: true});
            });
        });

        describe("holds a 'mode' field which", function () {
            it("is observable", function () {
                expect(vm.lng).toBeObservable();
            });

            it("starts as 'precise'", function () {
                expect(vm.mode()).toEqual("precise");
            });
        });

        describe("holds a 'tifUrl' field which", function () {
            it("is observable", function () {
                expect(vm.tifUrl).toBeObservable();
            });

            it("has the correct url", function () {
                vm.country(country1);
                vm.run(run1);
                expect(vm.tifUrl()).toEqual("baseUrl/tools/location/adminUnit?gaul=11&run=run1");
                vm.country(country2);
                vm.run(run2);
                expect(vm.tifUrl()).toEqual("baseUrl/tools/location/adminUnit?gaul=12&run=run2");
            });
        });

        describe("holds a 'pngUrl' field which", function () {
            it("is observable", function () {
                expect(vm.pngUrl).toBeObservable();
            });

            it("has the correct url", function () {
                vm.country(country1);
                vm.run(run1);
                expect(vm.pngUrl()).toEqual("wmsUrl?downloadParams=true");
                vm.country(country2);
                vm.run(run2);
                expect(vm.pngUrl()).toEqual("wmsUrl?downloadParams=true");
            });

            it("calls wmsFactory correctly", function () {
                vm.country(country1);
                vm.run(run1);
                expect(vm.pngUrl()).toEqual("wmsUrl?downloadParams=true");
                var args1 = wmsFactory.createLayerParametersForCroppedDownload.calls.mostRecent().args;
                expect(args1[0]).toEqual("run1");
                expect(args1[1]).toEqual(11);
                // padded version of country1 extent
                expect(args1[2].bbox).toEqual("-1.4000000000000001,-2.4,1.4000000000000001,2.4");
                expect(args1[2].height).toEqual(600);
                expect(args1[2].width).toEqual(350);

                vm.country(country2);
                vm.run(run2);
                expect(vm.pngUrl()).toEqual("wmsUrl?downloadParams=true");
                var args2 = wmsFactory.createLayerParametersForCroppedDownload.calls.mostRecent().args;
                expect(args2[0]).toEqual("run2");
                expect(args2[1]).toEqual(12);
                // padded version of country2 extent
                expect(args2[2].bbox).toEqual("-3.6,-2.604,3.6,2.604");
                expect(args2[2].height).toEqual(434);
                expect(args2[2].width).toEqual(600);
            });
        });
    });
});
