<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#disease-extent-parameters">
                Disease Extent Parameters
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="disease-extent-parameters">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="max-months-ago" class="col-sm-8 control-label">Max. Months Ago</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="max-months-ago" data-bind="formValue: maxMonthsAgo">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="max-months-ago-for-higher-score" class="col-sm-8 control-label">Max. Months Ago for Higher Occurrence Score</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="max-months-ago-for-higher-score" data-bind="formValue: maxMonthsAgoForHigherOccurrenceScore">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="higher-occurrence-score" class="col-sm-8 control-label">Higher Occurrence Score</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="higher-occurrence-score" data-bind="formValue: higherOccurrenceScore">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lower-occurrence-score" class="col-sm-8 control-label">Lower Occurrence Score</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="lower-occurrence-score" data-bind="formValue: lowerOccurrenceScore">
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="min-validation-weighting" class="col-sm-8 control-label">Min. Validation Weighting</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-validation-weighting" data-bind="formValue: minValidationWeighting">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-occurrences-for-presence" class="col-sm-8 control-label">Min. Occurrences for Presence</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-occurrences-for-presence" data-bind="formValue: minOccurrencesForPresence">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-occurrences-for-possible-presence" class="col-sm-8 control-label">Min. Occurrences for Possible Presence</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-occurrences-for-possible-presence" data-bind="formValue: minOccurrencesForPossiblePresence">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>