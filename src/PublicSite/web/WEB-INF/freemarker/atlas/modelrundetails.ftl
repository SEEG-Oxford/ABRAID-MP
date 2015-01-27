<div class="leaflet-top leaflet-right" id="modelRunDetails" style="display: none" data-bind="visible: hasActiveLayer, event: { 'dblclick' : function () { return false; } }, dblclickBubble: false">
    <!-- ko with:downloadLinksViewModel -->
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="PNG" data-bind="attr: { href: png }">
            <i class="fa fa-lg fa-picture-o"></i> Download as styled PNG
        </a>
    </div>
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="GeoTIFF" data-bind="attr: { href: tif }">
            <i class="fa fa-lg fa-download"></i>Download as raw GeoTIFF
        </a>
    </div>
    <div class="leaflet-bar leaflet-control" style="display: none" data-bind="visible: showOccurrences">
        <a target="_blank" title="Input CSV" data-bind="attr: { href: occurrences }">
            <i class="fa fa-lg fa-file-excel-o"></i>Download input occurrences
        </a>
    </div>
    <!-- /ko -->
    <div class="leaflet-bar leaflet-control">
        <a data-toggle="collapse" href="#covariatesAndStatistics" style="font-size: 12px; width: auto; height: auto;">
            <i class="fa fa-lg fa-bar-chart-o"></i> View Covariates and Statistics
        </a>
        <div></div>
        <div class="panel-collapse collapse container-sm-height" id="covariatesAndStatistics">
            <div class="panel-body">
                <div class="row-sm-height">
                    <div class="col-sm-6 col-sm-height">
                        <!-- ko with:covariateInfluencesViewModel -->
                        <h5>Covariate Influences</h5>
                        <div data-bind="if: covariateInfluences">
                            <table class="table table-condensed" id="covariates">
                            <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Name</th>
                                <th>Mean Influence</th>
                            </tr>
                            </thead>
                            <tbody data-bind="foreach: covariateInfluences">
                            <tr>
                                <td data-bind="text: $index() + 1"></td>
                                <td data-bind="text: name"></td>
                                <td data-bind="numericText: meanInfluence"></td>
                            </tr>
                            </tbody>
                            </table>
                        </div>
                        <!-- /ko -->
                    </div>
                    <div class="col-sm-6 col-sm-height" style="border-left: 1px solid #CCCCCC">
                        <!-- ko with:statisticsViewModel -->
                        <h5>Submodel Statistics</h5>
                        <div data-bind="if: statistics">
                            <table class="table table-condensed" id="statistics">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Mean</th>
                                        <th>Standard Deviation</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Kappa</td>
                                        <td data-bind="numericText: statistics().kappa"></td>
                                        <td data-bind="numericText: statistics().kappaSd"></td>
                                    </tr>
                                    <tr>
                                        <td><abbr title="Area Under Curve">AUC</abbr></td>
                                        <td data-bind="numericText: statistics().auc"></td>
                                        <td data-bind="numericText: statistics().aucSd"></td>
                                    </tr>
                                    <tr>
                                        <td>Sensitivity</td>
                                        <td data-bind="numericText: statistics().sens"></td>
                                        <td data-bind="numericText: statistics().sensSd"></td>
                                    </tr>
                                    <tr>
                                        <td>Specificity</td>
                                        <td data-bind="numericText: statistics().spec"></td>
                                        <td data-bind="numericText: statistics().specSd"></td>
                                    </tr>
                                    <tr>
                                        <td><abbr title="Proportion Correctly Classified">PCC</abbr></td>
                                        <td data-bind="numericText: statistics().pcc"></td>
                                        <td data-bind="numericText: statistics().pccSd"></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <!-- /ko -->
                    </div>
                </div>
                <!-- ko with:covariateInfluencesViewModel -->
                <div style="text-align: center;">
                    <a class="leaflet-panel-button" data-bind="attr: { href: effectCurvesLink }" target="_blank" title="Curves">Download effect curve data</a>
                </div>
                <!-- /ko -->
            </div>
        </div>
    </div>
</div>
