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
            <div data-bind="if: selectedDiseaseGroupId">
                <p class="form-group">
                    <span data-bind="if: hasModelBeenSuccessfullyRun">
                        <button class="btn btn-primary" data-bind="click: runModel, css: { 'disabled': !canRunModel() || working }, text: working() ? 'Working...' : 'Run Model'"></button>
                    </span>
                    <span data-bind="ifnot: hasModelBeenSuccessfullyRun">
                        <button class="btn btn-primary" data-bind="popover: { title: 'Is ModelWrapper set up?', trigger: 'focus', placement: 'top', template: 'modelwrapper-alert-template'}, css: { 'disabled': !canRunModel() || working }, text: working() ? 'Working...' : 'Run Model'"></button>
                    </span>
                </p>
                <div class="form-group" data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
            </div>
        </div>
    </div>
</div>
