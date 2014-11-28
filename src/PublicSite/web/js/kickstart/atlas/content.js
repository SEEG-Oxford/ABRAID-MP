/* Apply KO bindings for the atlas page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false */
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/atlas/AtlasView",
        "app/atlas/CovariateInfluencesViewModel",
        "app/atlas/DownloadLinksViewModel",
        "app/atlas/LayerSelectorViewModel",
        "app/atlas/ModelRunDetailsViewModel",
        "app/atlas/StatisticsViewModel",
        "app/atlas/WmsLayerParameterFactory",
        "app/atlas/LegendViewModel",
        "domReady!"
    ], function (ko, AtlasView, CovariateInfluencesViewModel, DownloadLinksViewModel, LayerSelectorViewModel,
                 ModelRunDetailsViewModel, StatisticsViewModel, WmsLayerParameterFactory, LegendViewModel, doc) {
        var wmsParamFactory = new WmsLayerParameterFactory();
        ko.applyBindings(
            new ModelRunDetailsViewModel(
                new CovariateInfluencesViewModel(baseUrl),
                new DownloadLinksViewModel(baseUrl, data.wmsUrl, wmsParamFactory),
                new StatisticsViewModel(baseUrl)
            ),
            doc.getElementById("modelRunDetails")
        );

        ko.applyBindings(
            new LegendViewModel(),
            doc.getElementById("legend")
        );

        var map = new AtlasView(data.wmsUrl, wmsParamFactory); // jshint ignore:line

        // NB. ViewModels subscribing to events published by LayerSelector must be defined first.
        ko.applyBindings(
            new LayerSelectorViewModel(data.layers),
            doc.getElementById("layerSelector")
        );
    });
});
