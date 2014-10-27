<div class="leaflet-bar leaflet-control">
    <div>
        <div>
            <a data-toggle="collapse" href="#covariatesAndStatistics" style="font-size: 12px; padding: 0 5px; width: auto; height: auto;">
                Model Run Covariates and Statistics
            </a>
            <div></div>
        </div>
    </div>
    <div class="panel-collapse collapse" style="background-color: white" id="covariatesAndStatistics">
        <div class="panel-body">
            <div class="col-sm-6">
                <table class="table table-condensed" id="covariates">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Covariate Name</th>
                            <th>Mean Influence</th>
                        </tr>
                    </thead>
                    <tbody data-bind="foreach: covariateInfluences">
                        <tr>
                            <td data-bind="text: $index() + 1"></td>
                            <td data-bind="text: name"></td>
                            <td data-bind="numericText: meanInfluence, precision: 3"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="col-sm-6">
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
                            <td></td>
                        </tr>
                        <tr>
                            <td><abbr title="Root Mean Squared Error">RMSE</abbr></td>
                            <td data-bind="numericText: statistics().rmse"></td>
                            <td></td>
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
                            <td></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>