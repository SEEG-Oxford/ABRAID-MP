/* Apply KO bindings for the atlas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, alert:false, window:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/atlas/AtlasView",
        "app/atlas/CovariateInfluencesViewModel",
        "app/atlas/DownloadLinksViewModel",
        "app/atlas/LayerSelectorViewModel",
        "app/atlas/ModelRunDetailsViewModel",
        "app/atlas/StatisticsViewModel",
        "app/LeafletMapFactory",
        "app/atlas/WmsLayerParameterFactory",
        "app/atlas/GeoJsonLayerFactory",
        "app/atlas/LegendViewModel",
        "domReady!",
        "analytics"
    ], function (ko, $, AtlasView, CovariateInfluencesViewModel, DownloadLinksViewModel, LayerSelectorViewModel,
                 ModelRunDetailsViewModel, StatisticsViewModel, LeafletMapFactory, WmsLayerParameterFactory,
                 GeoJsonLayerFactory, LegendViewModel, doc) {
        if ($(window).width() <= 780) {
            $("#legendText").removeClass("in");
            $("#legendExpander a").addClass("collapsed");
        }

        var wmsParamFactory = new WmsLayerParameterFactory();
        var geoJsonLayerFactory = new GeoJsonLayerFactory(baseUrl);
        var covariateInfluencesViewModel = new CovariateInfluencesViewModel(baseUrl);

        ko.applyBindings(
            new ModelRunDetailsViewModel(
                covariateInfluencesViewModel,
                new DownloadLinksViewModel(baseUrl, data.wmsUrl, wmsParamFactory),
                new StatisticsViewModel(baseUrl)
            ),
            doc.getElementById("modelRunDetails")
        );

        ko.applyBindings(covariateInfluencesViewModel, doc.getElementById("plotModal"));

        ko.applyBindings(
            new LegendViewModel(),
            doc.getElementById("legend")
        );

        var map = new AtlasView(data.wmsUrl, new LeafletMapFactory(), wmsParamFactory, geoJsonLayerFactory, alert); // jshint ignore:line

        // NB. ViewModels subscribing to events published by LayerSelector must be defined first.
        ko.applyBindings(
            new LayerSelectorViewModel(data.layers, data.seegMember),
            doc.getElementById("layerSelector")
        );
    });
});
