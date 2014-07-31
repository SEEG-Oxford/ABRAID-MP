<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#setup-body">
                Disease Group Setup
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="setup-body">
        <div class="panel-body">
            <div>
                <label for="last-model-run-text">Last Model Run:</label>
                <span data-bind="text: lastModelRunText"></span>
            </div>
            <div>
                <label for="disease-occurrences-text">Disease Occurrences:</label>
                <span data-bind="text: diseaseOccurrencesText"></span>
            </div>

            <br />
            <div class="form-group" data-bind="if: selectedDiseaseGroupId">
                <span data-bind="if: hasModelBeenSuccessfullyRun" class="side-by-side">
                    <button class="btn btn-primary" data-bind="click: runModel, bootstrapDisable: !canRunModel(), text: working() ? 'Working...' : 'Run Model and Batch Occurrences For Validation'"></button>
                </span>
                <span data-bind="ifnot: hasModelBeenSuccessfullyRun" class="side-by-side">
                    <button class="btn btn-primary" data-bind="popover: { title: 'Is ModelWrapper set up?', trigger: 'focus', placement: 'top', template: 'modelwrapper-alert-template'}, bootstrapDisable: !canRunModel(), text: working() ? 'Working...' : 'Run Model and Batch Occurrences For Validation'"></button>
                </span>
                <label for="batch-end-date" class="side-by-side" style="margin-left: 20px">Batch End Date:</label>
                <span class="input-group date">
                    <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
                    <input id="batch-end-date" type="text" class="form-control" data-bind="formDate: { date: batchEndDate, startDate: batchEndDateMinimum, endDate: batchEndDateMaximum }">
                </span>
            </div>
            <div class="form-group" data-bind="foreach: notices">
                <div data-bind="alert: $data"></div>
            </div>
        </div>
    </div>
</div>
