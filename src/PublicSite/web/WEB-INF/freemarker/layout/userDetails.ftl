<#--
    User details macro, for use on user sign up and user editing pages
    Copyright (c) 2014 University of Oxford
-->
<#macro userDetails title mainjs buttonStandardText="Save" buttonSubmittingText="Saving ...">
    <#import "../layout/common.ftl" as c/>
    <#import "../layout/form.ftl" as f/>
    <#import "../layout/table.ftl" as t/>
    <#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var diseases = ${diseases};
        var initialExpert = ${jsonExpert};
    </script>
    </#assign>
    <#assign templates>
        <@t.tableTemplates numberOfColumns=2 plural="diseases" subpath="diseaseInterestListViewModel.">
            <tr>
                <td><span data-bind="text: name"></td>
                <td><input type="checkbox" data-bind="formChecked: interested"></td>
            </tr>
        </@t.tableTemplates>
    </#assign>
    <@c.page title=title mainjs=mainjs templates=templates bootstrapData=bootstrapData>
    <div class="container">
        <@f.form formId="details-form" buttonStandardText=buttonStandardText buttonSubmittingText=buttonSubmittingText>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a href="#user-body">
                            User details
                        </a>
                    </h2>
                </div>
                <div class="panel-body" id="user-body">
                    <p class="form-group">
                        <label for="user-name">Name: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-user"></i>
                            </span>
                            <input id="user-name" type="text" class="form-control" placeholder="Name" data-bind="formValue: name" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="user-job">Job: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-briefcase"></i>
                            </span>
                            <input id="user-job" type="text" class="form-control" placeholder="Job" data-bind="formValue: jobTitle" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="user-institution">Institution: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-home"></i>
                            </span>
                            <input id="user-institution" type="text" class="form-control" placeholder="Institution" data-bind="formValue: institution" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="user-show">Show on site?</label>
                        <span id="user-show" class="input-group btn-group" data-bind="foreach: [ { value: true, label: 'Show me' }, { value: false, label: 'Don\'t show me' } ]">
                            <label class="btn btn-default" data-bind="css: { active: $parent.visibilityRequested() === value, disabled: find('isSubmitting') }">
                                <input type="radio" name="user-show" data-bind="formRadio: { selected: $parent.visibilityRequested, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </span>
                    </p>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a href="#interests-body">
                            Disease interests
                        </a>
                    </h2>
                </div>
                <div class="panel-body" id="interests-body">
                    <@t.tableBody singular="disease" title="Diseases" subpath="diseaseInterestListViewModel.">
                        [
                            { name: 'name', display: 'Disease' },
                            { name: 'interested', display: 'Interested?' }
                        ]
                    </@t.tableBody>
                </div>
            </div>
        </@f.form>
    </div>
    </@c.page>
</#macro>
