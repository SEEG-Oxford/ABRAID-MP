<#--
    The system administration page for disease groups.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var initialData = ${initialData};
</script>
</#assign>

<@c.page title="ABRAID-MP Administration: Disease Group" mainjs="/js/admindiseasegroup" bootstrapData=bootstrapData>
<div class="container">
    <div id="disease-groups-list">
        <label for="disease-group-picker" class="side-by-side">Selected Disease Group:</label>
        <span class="input-group">
            <span class="input-group-addon">
                <i class="fa fa-medkit"></i>
            </span>
            <select id="disease-group-picker" class="form-control" data-bind="options: diseaseGroups, value: selectedDiseaseGroup, optionsText: 'name'" ></select>
        </span>
    </div>
    <br />
    <br />
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#main-settings">
                    Main Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="main-settings">
            <div class="panel-body">
            </div>
        </div>
    </div>
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
                <p class="form-group">
                    <a class="btn btn-primary" data-bind="click: runModel, css: { 'disabled': !canRunModel() || runningModel }">Run Model</a>
                </p>
                <div class="form-group" data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</@c.page>