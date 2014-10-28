<!-- Style for templates -->
<style>
    .justify {
        text-align: justify;
    }
    .center {
        text-align: center;
    }
</style>

<!-- Templates -->
<script type="text/html" id="modelwrapper-alert-template">
    <div class="justify">Please configure ModelWrapper for this disease group if necessary.</div>
    <br />
    <div class="center">
        <button class="btn btn-default" data-bind="click: submit" data-dismiss="popover">Proceed with Model Run</button>
    </div>
</script>

<script type="text/html" id="automatic-model-runs-alert-template">
    <div class="justify">
        Enabling model runs to be triggered automatically is a one-time action that <i><strong>cannot be undone</strong></i>.
        <br /><br />
        Please ensure that ModelWrapper has been configured fully for this disease group before proceeding.
    </div>
    <br />
    <div class="center">
        <button class="btn btn-default" data-bind="click: enableAutomaticModelRuns" data-dismiss="popover">Proceed</button>
    </div>
</script>

<!-- Content -->
<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#disease-group-setup">
                Model Runs
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="disease-group-setup">
        <div class="panel-body">
            <div data-bind="ifnot: isAutomaticModelRunsEnabled">
                <!-- Information text -->
                <div>
                    <label for="last-model-run-text">Last Model Run:</label>
                    <span id="last-model-run-text" data-bind="text: lastModelRunText"></span>
                </div>
                <div>
                    <label for="disease-occurrences-text">Disease Occurrences:</label>
                    <span id="disease-occurrences-text" data-bind="text: diseaseOccurrencesText"></span>
                </div>

                <br>
                <div class="form-group" data-bind="if: selectedDiseaseGroupId">
                    <!-- Use gold standard occurrences (yes/no) -->
                    <div data-bind="if: hasGoldStandardOccurrences">
                        <div class="btn-group" id="use-gold-standard-occurrences" data-bind="foreach: [ { value: true, label: 'Yes' }, { value: false, label: 'No' } ]">
                            <label class="btn btn-default" data-bind="css: {active: $parent.useGoldStandardOccurrences() === value, disabled: find('isSubmitting')}">
                                <input type="radio" name="use-gold-standard-occurrences" data-bind="formRadio: { selected: $parent.useGoldStandardOccurrences, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                        <label for="use-gold-standard-occurrences" style="font-weight: normal; padding-left: 10px">
                            "Gold standard" disease occurrences are available for this disease group. Would you like to use them
                            (and only them) for set-up?
                        </label>
                        <br /><br />
                    </div>
                    <!-- Generate Disease Extent -->
                    <div class="form-group">
                        <button class="btn btn-primary" data-bind="click: generateDiseaseExtent,
                                                                   text: isGeneratingDiseaseExtent() ? 'Working...' : 'Generate Disease Extent',
                                                                   bootstrapDisable: disableButtonThatGeneratesDiseaseExtent()">
                        </button>
                    </div>
                    <!-- Run Model, Batch Start Date and Batch End Date -->
                    <div style="overflow: auto; width: 100%" class="form-group">
                        <span data-bind="if: hasModelBeenSuccessfullyRun" class="side-by-side">
                            <button class="btn btn-primary" data-bind="click: submit,
                                                                       text: isSubmitting() ? 'Working...' : (useGoldStandardOccurrences() ? 'Run Model' : 'Run Model and Batch Occurrences For Validation'),
                                                                       bootstrapDisable: disableButtonThatRunsModel()">
                            </button>
                        </span>
                        <span data-bind="ifnot: hasModelBeenSuccessfullyRun" class="side-by-side">
                            <button class="btn btn-primary" data-bind="popover: { title: 'Is ModelWrapper set up?', trigger: 'focus', placement: 'top', template: 'modelwrapper-alert-template'},
                                                                       text: isSubmitting() ? 'Working...' : (useGoldStandardOccurrences() ? 'Run Model' : 'Run Model and Batch Occurrences For Validation'),
                                                                       bootstrapDisable: disableButtonThatRunsModel()">
                            </button>
                        </span>
                        <span data-bind="ifnot: useGoldStandardOccurrences" class="side-by-side">
                            <label for="batch-start-date" style="margin-left: 20px" class="side-by-side">Batch Start Date:</label>
                            <span class="input-group date side-by-side">
                                <span class="input-group-addon"><i class="fa fa-calendar"></i></span>
                                <input id="batch-start-date" type="text" class="form-control" data-bind="formDate: { date: batchStartDate, startDate: batchDateMinimum, endDate: batchDateMaximum }">
                            </span>
                            <label for="batch-end-date" style="margin-left: 20px" class="side-by-side">Batch End Date:</label>
                            <span class="input-group date side-by-side">
                                <span class="input-group-addon"><i class="fa fa-calendar"></i></span>
                                <input id="batch-end-date" type="text" class="form-control" data-bind="formDate: { date: batchEndDate, startDate: batchDateMinimum, endDate: batchDateMaximum }">
                            </span>
                        </span>
                    </div>
                    <!-- Enable Automatic Model Runs -->
                    <div class="form-group">
                        <button class="btn btn-primary" data-bind="popover: { title: 'Are you sure?', trigger: 'focus', placement: 'top', template: 'automatic-model-runs-alert-template'},
                                                                   text: isEnablingAutomaticModelRuns() ? 'Enabling...' : 'Enable Automatic Model Runs',
                                                                   bootstrapDisable: disableButtonThatEnablesAutomaticModelRuns()">
                        </button>
                    </div>
                    <!-- Notices -->
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </div>
            </div>
            <div data-bind="if: isAutomaticModelRunsEnabled">
                <div class="alert alert-success" role="alert">Automatic model runs have been enabled for this disease group. No further setup is required.</div>
            </div>
        </div>
    </div>
</div>
