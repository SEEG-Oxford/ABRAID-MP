<#macro formGroup id title bind disable="false">
<div class="form-group">
    <label for="${id}" class="col-sm-8 control-label">${title}</label>
    <div class="input-group col-sm-4">
        <input type="text" class="form-control" id="${id}" data-bind="formValue: ${bind}, bootstrapDisable: ${disable}">
    </div>
</div>
</#macro>

<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#model-run-parameters">
                Model Run Parameters
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse" id="model-run-parameters">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <@formGroup id="min-new-occurrences" title="Min. Number of New Occurrences" bind="minNewOccurrences"></@formGroup>
                    <@formGroup id="min-data-volume" title="Min. Data Volume" bind="minDataVolume"></@formGroup>
                    <@formGroup id="min-distinct-countries" title="Min. Number of Distinct Countries" bind="minDistinctCountries"></@formGroup>
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
                    <@formGroup id="min-high-frequency-countries" title="Min. Number of High Frequency Countries" bind="minHighFrequencyCountries" disable="!occursInAfrica()"></@formGroup>
                    <@formGroup id="high-frequency-threshold" title="Min. Number of Occurrences to be deemed a High Frequency Country" bind="highFrequencyThreshold" disable="!occursInAfrica()"></@formGroup>
                </div>
            </div>
        </div>
    </div>
</div>