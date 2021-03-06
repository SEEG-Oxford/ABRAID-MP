<div class="leaflet-top leaflet-right" id="modelRunDetails" style="display: none; z-index: 1039" data-bind="visible: hasActiveLayer">
    <!-- ko with:downloadLinksViewModel -->
    <div class="leaflet-bar leaflet-control" data-bind="visible: showPng, preventBubble: true">
        <a target="_blank" title="PNG" data-bind="attr: { href: png }">
            <i class="fa fa-lg fa-picture-o"></i><span>Download PNG image</span>
        </a>
    </div>
    <div class="leaflet-bar leaflet-control" data-bind="visible: showTif, preventBubble: true">
        <a target="_blank" title="GeoTIFF" data-bind="attr: { href: tif }">
            <i class="fa fa-lg fa-download"></i><span>Download predicted data</span>
        </a>
    </div>
    <div class="leaflet-bar leaflet-control" style="display: none" data-bind="visible: showOccurrences, preventBubble: true">
        <a target="_blank" title="Input CSV" data-bind="attr: { href: occurrences }">
            <i class="fa fa-lg fa-file-excel-o"></i><span>Download input data</span>
        </a>
    </div>
    <!-- /ko -->
    <div id="covariatesAndStatsButtonBox" class="leaflet-bar leaflet-control" data-bind="preventBubble: false">
        <a id="covariatesAndStatsButton" data-toggle="collapse" class="collapsed" href="#covariatesAndStatistics">
            <i class="fa fa-lg fa-bar-chart-o"></i><span>View covariates and statistics</span>
        </a>
        <div class="panel-collapse collapse container-sm-height" id="covariatesAndStatistics" style="width:650px;overflow-x:hidden;">
            <div class="panel-body" data-bind="slideLeft: showCovariateTable" style="width:1300px;">
                <div style="float: left; width:650px; min-height: 328px; position: relative">
                    <div class="row-xs-height">
                        <div class="col-xs-5 col-xs-height">
                            <!-- ko with:covariateInfluencesViewModel -->
                            <h5>Covariate Influence</h5>
                            <div data-bind="if: covariateInfluences">
                                <script type="text/html" id="covariateHelp"><span data-bind="text: $data.info || ' '"></span></script>
                                <table class="table table-condensed" id="covariates">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>Rank</th>
                                            <th>Name</th>
                                            <th>Relative<br/>Influence</th>
                                        </tr>
                                    </thead>
                                    <tbody data-bind="foreach: covariateInfluences">
                                        <tr>
                                            <td><span data-bind="popover: {template: 'covariateHelp', title: name, trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td data-bind="text: $index() + 1"></td>
                                            <td data-bind="text: name"></td>
                                            <td data-bind="numericText: meanInfluence"></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /ko -->
                        </div>
                        <div class="col-xs-2 col-xs-height" style="border-left: 1px solid #CCCCCC">
                            <!-- ko with:statisticsViewModel -->
                            <h5>Submodel Statistics</h5>
                            <div data-bind="if: statistics">
                                <table class="table table-condensed" id="statistics">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>Name</th>
                                            <th>Mean</th>
                                            <th>Standard<br/>Deviation</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td><span data-bind="popover: {template: 'kappaHelp', title: 'Kappa', trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td>Kappa</td>
                                            <td data-bind="numericText: statistics().kappa"></td>
                                            <td data-bind="numericText: statistics().kappaSd"></td>
                                            <script type="text/html" id="kappaHelp">The degree of agreement between the prediction and the truth taking account of the fact that some of those correct classifications may have happened simply by chance. A kappa statistic of 0 means the model was no better than random and a kappa statistic of 1 means it made a perfect prediction.</script>
                                        </tr>
                                        <tr>
                                            <td><span data-bind="popover: {template: 'aucHelp', title: 'Area Under Curve', trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td>AUC</td>
                                            <td data-bind="numericText: statistics().auc"></td>
                                            <td data-bind="numericText: statistics().aucSd"></td>
                                            <script type="text/html" id="aucHelp">Area under the receiver operating characteristic curve; a measure of how well the model does at ranking sites by probability of disease occurrence. An AUC of 0 means the model ranked all sites the wrong way round, 0.5 means the model was no better than random and an AUC of 1 means it made a perfect prediction.</script>
                                        </tr>
                                        <tr>
                                            <td><span data-bind="popover: {template: 'sensHelp', title: 'Sensitivity', trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td>Sensitivity</td>
                                            <td data-bind="numericText: statistics().sens"></td>
                                            <td data-bind="numericText: statistics().sensSd"></td>
                                            <script type="text/html" id="sensHelp">The proportion of sites in which the disease occurs that the model correctly predicted the disease to occur in. A sensitivity of 0 means the model didn't correctly predict any of the occurrences and a sensitivity of 1 means the model predicted all occurrences perfectly.</script>
                                        </tr>
                                        <tr>
                                            <td><span data-bind="popover: {template: 'specHelp', title: 'Specificity', trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td>Specificity</td>
                                            <td data-bind="numericText: statistics().spec"></td>
                                            <td data-bind="numericText: statistics().specSd"></td>
                                            <script type="text/html" id="specHelp">The proportion of sites in which the disease does not occur that the model correctly predicted the disease to not occur in. A specificity of 0 means the model didn't correctly predict any of the absences and a sensitivity of 1 means the model predicted all absences perfectly.</script>
                                        </tr>
                                        <tr>
                                            <td><span data-bind="popover: {template: 'pccHelp', title: 'Proportion Correctly Classified', trigger: 'hover', placement: 'bottom', container: 'body'}"><i class="fa fa-info-circle"></i></span></td>
                                            <td>PCC</td>
                                            <td data-bind="numericText: statistics().pcc"></td>
                                            <td data-bind="numericText: statistics().pccSd"></td>
                                            <script type="text/html" id="pccHelp">Proportion correctly classified; the proportion of sites in which the model correctly predicted whether the disease either occurred or was absent. A PCC of 0 means the model predicted all absences as occurrences and vice versa, and a PCC of 1 means the model correctly classified all occurrences and absences perfectly.</script>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /ko -->
                        </div>
                    </div>
                    <div style="clear: both; padding-bottom: 22px;"></div>
                    <div style="text-align: center; position: absolute; bottom: 0px; width: 100%">
                        <a class="leaflet-panel-button" data-bind="attr: { href: covariateInfluencesViewModel.effectCurvesLink }" target="_blank" title="Curves">Download effect curve data</a>
                        <a class="leaflet-panel-button" href="#" data-bind="toggleClick: showCovariateTable, preventBubble: true" title="Plots">Show effect curves</a>
                    </div>
                </div>
                <div class="panel-body" style="width:650px; margin-left:650px; position: relative">
                    <!-- ko with:covariateInfluencesViewModel -->
                    <div id="plotPanel"  data-bind="foreach: covariateInfluencesToPlot">
                        <span data-bind="click: function() {$parent.activeCurve($data.covariate)}" data-toggle="modal" data-target="#plotModal">
                            <div data-bind="smallEffectPlot: $data"></div>
                        </span>
                    </div>
                    <!-- /ko -->
                    <div style="text-align: center; position: absolute; bottom: 5px; width: 100%">
                        <a class="leaflet-panel-button" href="#" data-bind="toggleClick: showCovariateTable" title="Back">Back</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

