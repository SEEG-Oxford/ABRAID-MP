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
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <label for="min-new-occurrences" class="col-sm-4 control-label">Minimum Number of New Occurrences</label>
                    <div class="input-group col-sm-2">
                        <input class="form-control" id="min-new-occurrences" data-bind="value: minNewOccurrences, valueUpdate: 'input'">
                    </div>
                </div>
                <div class="form-group">
                    <label for="min-data-volume" class="col-sm-4 control-label">Minimum Data Volume</label>
                    <div class="input-group col-sm-2">
                        <input class="form-control" id="min-data-volume" data-bind="value: minDataVolume, valueUpdate: 'input'">
                    </div>
                </div>
                <div class="form-group">
                    <label for="min-distinct-countries" class="col-sm-4 control-label">Minimum Number of Distinct Countries</label>
                    <div class="input-group col-sm-2">
                        <input class="form-control" id="min-distinct-countries" data-bind="value: minDistinctCountries, valueUpdate: 'input'">
                    </div>
                </div>
                <div class="form-group">
                    <label for="min-high-frequency-countries" class="col-sm-4 control-label">Minimum Number of High Frequency Countries</label>
                    <div class="input-group col-sm-2">
                        <input class="form-control" id="min-high-frequency-countries" data-bind="value: minHighFrequencyCountries, valueUpdate: 'input'">
                    </div>
                </div>
                <div class="form-group">
                    <label for="high-frequency-threshold" class="col-sm-4 control-label">Minumum Number of Occurrences to be deemed a High Frequency Country</label>
                    <div class="input-group col-sm-2">
                        <input class="form-control" id="high-frequency-threshold" data-bind="value: highFrequencyThreshold, valueUpdate: 'input'">
                    </div>
                </div>
                <div class="form-group">
                    <label for="occurs-in-africa" class="col-sm-4 control-label">Occurs in Africa</label>
                    <div class="col-sm-2">
                        <input type="checkbox" id="occurs-in-africa" data-bind="checked: occursInAfrica">
                    </div>
                </div>
                <div class="col-sm-12">
                    <button type="button" class="btn btn-primary" data-bind="click: save">Save</button>
                </div>
                <div class="form-group col-sm-12" data-bind="if: notice">
                    <div data-bind="alert: notice"></div>
                </div>
            </form>
        </div>
    </div>
</div>