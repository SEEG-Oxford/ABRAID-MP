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
    <div id="disease-group-list">
        <label for="disease-picker" class="side-by-side">Selected Disease Group:</label>
        <span class="input-group">
            <span class="input-group-addon">
                <i class="fa fa-medkit"></i>
            </span>
            <select id="disease-picker" class="form-control" data-bind="options: diseases, value: selectedDisease, optionsText: 'name'" ></select>
        </span>
    </div>
    <br />
    <br />
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#setup-body">
                    Main Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="setup-body">
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
            </div>
        </div>
    </div>
</div>
</@c.page>