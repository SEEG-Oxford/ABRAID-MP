<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#model-run-parameters">
                Model Run Parameters
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="model-run-parameters">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="min-new-occurrences" class="col-sm-8 control-label">Min. Number of New Occurrences</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-new-occurrences" data-bind="formValue: minNewOccurrences">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-data-volume" class="col-sm-8 control-label">Min. Data Volume</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-data-volume" data-bind="formValue: minDataVolume">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-distinct-countries" class="col-sm-8 control-label">Min. Number of Distinct Countries</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-distinct-countries" data-bind="formValue: minDistinctCountries">
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="occurs-in-africa" class="col-sm-8 control-label">Occurs in Africa</label>
                        <div class="col-sm-4">
                            <input type="checkbox" id="occurs-in-africa" data-bind="formChecked: occursInAfrica">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-high-frequency-countries" class="col-sm-8 control-label">Min. Number of High Frequency Countries</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="min-high-frequency-countries" data-bind="formValue: minHighFrequencyCountries, bootstrapDisable: !occursInAfrica()">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="high-frequency-threshold" class="col-sm-8 control-label">Min. Number of Occurrences to be deemed a High Frequency Country</label>
                        <div class="input-group col-sm-4">
                            <input type="text" class="form-control" id="high-frequency-threshold" data-bind="formValue: highFrequencyThreshold, bootstrapDisable: !occursInAfrica()">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>