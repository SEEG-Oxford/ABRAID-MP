<#macro formGroup id title bind>
<div class="form-group">
    <label for="${id}" class="col-sm-8 control-label">${title}</label>
    <div class="input-group col-sm-4">
        <input type="text" class="form-control" id="${id}" data-bind="formValue: ${bind}">
    </div>
</div>
</#macro>

<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#disease-extent-parameters">
                Disease Extent Parameters
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse" id="disease-extent-parameters">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <@formGroup id="max-months-ago-for-higher-score" title="Max. Months Ago for Higher Occurrence Score" bind="maxMonthsAgoForHigherOccurrenceScore"></@formGroup>
                    <@formGroup id="min-validation-weighting" title="Min. Validation Weighting" bind="minValidationWeighting"></@formGroup>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <@formGroup id="higher-occurrence-score" title="Higher Occurrence Score" bind="higherOccurrenceScore"></@formGroup>
                    <@formGroup id="lower-occurrence-score" title="Lower Occurrence Score" bind="lowerOccurrenceScore"></@formGroup>
                </div>
            </div>
        </div>
    </div>
</div>
