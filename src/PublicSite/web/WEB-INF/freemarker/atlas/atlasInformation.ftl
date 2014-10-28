<div class="leaflet-top leaflet-right" id="atlasInformation" style="display: none" data-bind="visible: activeLayer">
    <!-- ko with:downloadLinksViewModel -->
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="PNG" data-bind="attr: { href: png }">
            <i class="fa fa-lg fa-picture-o"></i> Download as styled PNG
        </a>
    </div>
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="GeoTIFF" data-bind="attr: { href: tif }">
            <i class="fa fa-lg fa-download"></i> Download as raw GeoTIFF
        </a>
    </div>
    <!-- /ko -->
    <div class="leaflet-bar leaflet-control">
        <a data-toggle="collapse" href="#covariatesAndStatistics" style="font-size: 12px; padding: 0 5px; width: auto; height: auto;">
            <i class="fa fa-lg fa-bar-chart"></i> Model Run Covariates and Statistics
        </a>
        <div></div>
        <div class="panel-collapse collapse" style="background-color: white" id="covariatesAndStatistics">
            <div class="panel-body">
                <div class="col-sm-6" style="border-right: 1px solid #CCCCCC">
                    <!-- ko with:covariateInfluencesViewModel -->
                    <h5>Covariate Influences</h5>
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
                    <!-- /ko -->
                </div>
                <div class="col-sm-6" style="border-left: 1px solid #CCCCCC">
                    <!-- ko with:submodelStatisticsViewModel -->
                    <h5>Submodel Statistics</h5>
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
                            <td>Deviance</td>
                            <td data-bind="numericText: statistics().deviance"></td>
                            <td data-bind="numericText: statistics().devianceSd"></td>
                        </tr>
                        <tr>
                            <td><abbr title="Root Mean Squared Error">RMSE</abbr></td>
                            <td data-bind="numericText: statistics().rmse"></td>
                            <td data-bind="numericText: statistics().rmseSd"></td>
                        </tr>
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
                        <tr>
                            <td>Threshold</td>
                            <td data-bind="numericText: statistics().threshold"></td>
                            <td data-bind="numericText: statistics().thresholdSd"></td>
                        </tr>
                        </tbody>
                    </table>
                    <!-- /ko -->
                </div>
            </div>
        </div>
    </div>
</div>