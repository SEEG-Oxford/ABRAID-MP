<div class="leaflet-top leaflet-right" id="modelRunDetails" style="display: none" data-bind="visible: hasActiveLayer, event: { 'dblclick' : function () { return false; } }, dblclickBubble: false">
    <!-- ko with:downloadLinksViewModel -->
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="PNG" data-bind="attr: { href: png }">
            <i class="fa fa-lg fa-picture-o"></i>Download PNG image
        </a>
    </div>
    <div class="leaflet-bar leaflet-control">
        <a target="_blank" title="GeoTIFF" data-bind="attr: { href: tif }">
            <i class="fa fa-lg fa-download"></i>Download predicted data
        </a>
    </div>
    <div class="leaflet-bar leaflet-control" style="display: none" data-bind="visible: showOccurrences">
        <a target="_blank" title="Input CSV" data-bind="attr: { href: occurrences }">
            <i class="fa fa-lg fa-file-excel-o"></i>Download input data
        </a>
    </div>
    <!-- /ko -->
    <div class="leaflet-bar leaflet-control">
        <a data-toggle="collapse" href="#covariatesAndStatistics" style="width: auto; height: auto;">
            <i class="fa fa-lg fa-bar-chart-o"></i>View covariates and statistics
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
                                        <th></th>
                                        <th>Name</th>
                                        <th>Mean</th>
                                        <th>Standard Deviation</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><span data-bind="popover: {template: 'kappaHelp', title: 'Kappa', trigger: 'hover', placement: 'bottom'}"><i class="fa fa-info-circle"></i></span></td>
                                        <td>Kappa</td>
                                        <td data-bind="numericText: statistics().kappa"></td>
                                        <td data-bind="numericText: statistics().kappaSd"></td>
                                        <script type="text/html" id="kappaHelp">The degree of agreement between the prediction and the truth taking account of the fact that some of those correct classifications may have happened simply by chance. A kappa statistic of 0 means the model was no better than random and a kappa statistic of 1 means it made a perfect prediction.</script>
                                    </tr>
                                    <tr>
                                        <td><span data-bind="popover: {template: 'aucHelp', title: 'Area Under Curve', trigger: 'hover', placement: 'bottom'}"><i class="fa fa-info-circle"></i></span></td>
                                        <td>AUC</td>
                                        <td data-bind="numericText: statistics().auc"></td>
                                        <td data-bind="numericText: statistics().aucSd"></td>
                                        <script type="text/html" id="aucHelp">Area under the receiver operating characteristic curve; a measure of how well the model does at ranking sites by probability of disease occurrence. An AUC of 0 means the model ranked all sites the wrong way round, 0.5 means the model was no better than random and an AUC of 1 means it made a perfect prediction.</script>
                                    </tr>
                                    <tr>
                                        <td><span data-bind="popover: {template: 'sensHelp', title: 'Sensitivity', trigger: 'hover', placement: 'bottom'}"><i class="fa fa-info-circle"></i></span></td>
                                        <td>Sensitivity</td>
                                        <td data-bind="numericText: statistics().sens"></td>
                                        <td data-bind="numericText: statistics().sensSd"></td>
                                        <script type="text/html" id="sensHelp">The proportion of sites in which the disease occurs that the model correctly predicted the disease to occur in. A sensitivity of 0 means the model didn't correctly predict any of the occurrences and a sensitivity of 1 means the model predicted all occurrences perfectly.</script>
                                    </tr>
                                    <tr>
                                        <td><span data-bind="popover: {template: 'specHelp', title: 'Specificity', trigger: 'hover', placement: 'bottom'}"><i class="fa fa-info-circle"></i></span></td>
                                        <td>Specificity</td>
                                        <td data-bind="numericText: statistics().spec"></td>
                                        <td data-bind="numericText: statistics().specSd"></td>
                                        <script type="text/html" id="specHelp">The proportion of sites in which the disease does not occur that the model correctly predicted the disease to not occur in. A specificity of 0 means the model didn't correctly predict any of the absences and a sensitivity of 1 means the model predicted all absences perfectly.</script>
                                    </tr>
                                    <tr>
                                        <td><span data-bind="popover: {template: 'pccHelp', title: 'Proportion Correctly Classified', trigger: 'hover', placement: 'bottom'}"><i class="fa fa-info-circle"></i></span></td>
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
                <div style="clear: both"></div>
                <!-- ko with:covariateInfluencesViewModel -->
                <div style="text-align: center;">
                    <a class="leaflet-panel-button" data-bind="attr: { href: effectCurvesLink }" target="_blank" title="Curves">Download effect curve data</a>
                </div>
                <!-- /ko -->
            </div>
        </div>
    </div>
</div>
