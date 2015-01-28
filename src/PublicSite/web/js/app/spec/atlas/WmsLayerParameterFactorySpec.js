/* A suite of tests for the atlas WmsLayerParameterFactory AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/WmsLayerParameterFactory",
    "underscore"
], function (WmsLayerParameterFactory, _) {
    "use strict";

    describe("The atlas 'WMS layer parameter factory'", function () {
        var vm = new WmsLayerParameterFactory();

        describe("has a 'createLayerParametersForDisplay' method, which", function () {
            it("returns the standard abraid WMS configuration", function () {
                var params = vm.createLayerParametersForDisplay("the_name_of_a_model_run_uncertainty");
                expect(params.format).toEqual("image/png");
                expect(params.reuseTiles).toEqual(true);
                expect(params.tiled).toEqual(true);
            });

            it("returns the correct extent specific configuration", function () {
                var params = vm.createLayerParametersForDisplay("the_name_of_a_model_run_extent");
                expect(params.layers).toEqual("abraid:atlas_extent_layer");
                expect(params.styles).toEqual("abraid_extent");
                expect(params.cql_filter).toEqual("model_run_name='the_name_of_a_model_run'"); // jshint ignore:line
            });

            it("returns the correct mean specific configuration", function () {
                var params = vm.createLayerParametersForDisplay("the_name_of_a_model_run_mean");
                expect(params.layers).toEqual("abraid:the_name_of_a_model_run_mean");
                expect(params.styles).toEqual("abraid_mean");
                expect(params.cql_filter).toBeUndefined(); // jshint ignore:line
            });

            it("returns the correct uncertainty specific configuration", function () {
                var params = vm.createLayerParametersForDisplay("the_name_of_a_layer_uncertainty");
                expect(params.layers).toEqual("abraid:the_name_of_a_layer_uncertainty");
                expect(params.styles).toEqual("abraid_uncertainty");
                expect(params.cql_filter).toBeUndefined(); // jshint ignore:line
            });
        });

        describe("has a 'createLayerParametersForDownload' method, which", function () {
            it("returns the same as the display function but adds standard download parameters", function () {
                var paramsDownload, paramsDisplay;
                var extraDownloadParameters = {
                    service: "WMS",
                    version: "1.1.0",
                    request: "GetMap",
                    bbox: "-180.1,-60.0,180.0,85.0",// BBox min set to -180.1 due to bug in Geoserver (should be -180.0)
                    width: 1656,
                    height: 667,
                    srs: "EPSG:4326"
                };

                paramsDisplay = vm.createLayerParametersForDisplay("the_name_of_a_model_run_uncertainty");
                paramsDownload = vm.createLayerParametersForDownload("the_name_of_a_model_run_uncertainty");
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));

                paramsDisplay = vm.createLayerParametersForDisplay("the_name_of_a_model_run_mean");
                paramsDownload = vm.createLayerParametersForDownload("the_name_of_a_model_run_mean");
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));

                paramsDisplay = vm.createLayerParametersForDisplay("the_name_of_a_model_run_extent");
                paramsDownload = vm.createLayerParametersForDownload("the_name_of_a_model_run_extent");
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));
            });
        });
    });
});
