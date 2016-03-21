<#--
    Covariate list page, to add and edit covariate files.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/table.ftl" as t/>
<#import "../shared/layout/panel.ftl" as p/>
<#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var initialData = ${initialData};
    </script>
</#assign>

<#assign templates>
    <@t.tableTemplates numberOfColumns=4 plural="files">
        <tr>
            <td data-bind="event: { mouseover: function() { mouseOver(true) }, mouseout: function() { mouseOver(false) } }">
                <span class="input-group">
                    <input type="text" data-bind="formValue: name, attr: { title: name }, css: { 'transparent-input': !mouseOver() }" placeholder="A name must be provided">
                </span>
            </td>
            <td>
                <button class="btn btn-default" style="padding-top: 3px; padding-bottom: 3px" data-bind="text: count, popover: { title: 'Files', trigger: 'click', placement: 'bottom', template: 'sub-files-template'}, click: function(data, event) { event.preventDefault(); }, bootstrapDisable: $parent.isSubmitting()"></button>
            </td>
            <td><input type="checkbox" data-bind="checked: discrete, bootstrapDisable: true"></td>
            <td><input type="checkbox" data-bind="formChecked: state"></td>
            <td>
                <button class="btn btn-default fa fa-lg fa-info-circle" style="float: right" data-bind="style: { color: info() ? '#3c763d' : '#31708f' }, popover: { title: 'Info text', trigger: 'click', placement: 'bottom', template: 'info-text-template'}, click: function(data, event) { event.preventDefault(); }, bootstrapDisable: $parent.isSubmitting()"></button>
            </td>
            <td>
                <button class="btn btn-default fa fa-lg fa-trash-o" style="float: right" data-bind="popover: { title: 'Delete file?', trigger: 'focus', placement: 'bottom', template: 'file-list-delete-template'}, click: function(data, event) { event.preventDefault(); }, bootstrapDisable: $parent.isSubmitting()"></button>
            </td>
        </tr>
    </@t.tableTemplates>
    <script type="text/html" id="sub-files-template">
        <table>
            <tr>
                <th>Qualifier</th>
                <th>Path</th>
            </tr>
            <tbody data-bind="foreach: files">
                <tr>
                    <td><span class="input-group"><input type="text" data-bind="formValue: qualifier, attr: { title: qualifier }" placeholder="A qualifier must be provided"></span></td>
                    <td><input type="text" data-bind="formValue: path, attr: { title: path }" readonly="true" class="transparent-input" ></td>
                </tr>
            </tbody>
        </table>
    </script>
    <script type="text/html" id="info-text-template">
        <textarea rows="8" cols="30" maxlength="500" placeholder="Not specified" autofocus="true" style="resize: none;" data-bind="value: info">
        </textarea>
    </script>
    <script type="text/html" id="file-list-delete-template">
        <p>This file is currently used in <span data-bind="text: usageCount"></span> <span data-bind="text: usageCount() === 1 ? 'disease' : 'diseases'"></span>. Are you sure you want to delete it?</p><br>
        <p style="text-align:center;">
            <span class="btn btn-default" data-bind="click: function () { hide(true) }" data-dismiss="popover">Confirm<span>
        </p>
    </script>
    <script type="text/html" id="covariate-validation-template">
        <!-- ko if: field.rules().length != 0 -->
        <span class="input-group-addon" style="background-color: transparent; border: none" data-container="body" data-bind="tooltip: { title: field.error, placement: 'right' } ">
            <i class="fa fa-lg" data-bind="css: field.isValid() ? '' : 'text-danger fa-exclamation-circle'"></i>
        </span>
        <!-- /ko -->
    </script>
</#assign>

<@c.page title="ABRAID-MP Administration: Covariates" mainjs="/js/kickstart/admin/covariates" bootstrapData=bootstrapData templates=templates>
<div class="container">
    <@p.panel "add-covariate-body" "Add Covariate File" true>
        <p>Use the fields below to add new covariate files to the system.</p>
        <ul>
            <li>Creating new covariates</li>
            <ul>
                <li>Select &quot;Create new covariate&quot; to define a new covariate.</li>
                <li>The first file uploaded must be the &#39;reference&#39; layer.</li>
                <li>This is the layer to be predicted to and will be shown in the values histogram.</li>
                <li>It should have the largest qualifier value (i.e. maximum year/date).</li>
                <li>Use of a consistant &quot;subdirectory&quot; for all layers within a single covariate is recommended.</li>
            </ul>
            <li>Qualifiers</li>
            <ul>
                <li>Qualifiers must be provided for all layers.</li>
                <li>It is used to identify layers within multi-layer covariates.</li>
                <li>For temporal data they should be formatted as &quot;YYYY-MM-DD&quot; to the appropriate precision.</li>
                <li>For single layer covariates any value can be used (but &quot;Single&quot; is recommended).</li>
            </ul>
        </ul>
        <@f.form "add-covariate-form" "Upload" "Uploading...">
            <@f.formGroupGeneric "parent-picker" "Parent Covariate" "glyphicon glyphicon-sort">
                <select id="parent-picker" class="form-control" data-bind="options: parentList, value: parent, optionsText: 'name', optionsCaption: '*** Create new covariate ***', bootstrapDisable: isSubmitting()" ></select>
            </@f.formGroupGeneric>
            <div data-bind="visible: !parent()">
                <@f.formGroupBasic "file-name" "Name" "name" "glyphicon glyphicon-pencil" />
            </div>
            <@f.formGroupBasic "file-qualifier" "Qualifier" "qualifier" "glyphicon glyphicon-calendar" />
            <@f.formGroupBasic "file-dir" "Subdirectory" "subdirectory" "glyphicon glyphicon-folder-open" />
            <@f.formGroupFile "file-picker" "File" "file" />
            <p data-bind="visible: !parent()">
                <label for="file-discrete">Is Discrete?: </label>
                <input id="file-discrete" type="checkbox" style="line-height: 20px; margin: 0 0 5px 0; vertical-align: middle " data-bind="formChecked: discrete" autocomplete="off">
            </p>
            <div class="hidden" data-bind="css: { hidden: false }" style="min-height: 32px; margin: 10px 0">
                <div class="alert alert-danger" data-bind="visible: unsavedWarning"><p>The lower section of this page has unsaved changes. Please save these first (or refresh).</p></div>
                <div class="alert alert-warning" data-bind="visible: subdirectory.isValid() && file.isValid() && !uploadPath.isValid() && uploadPath()"><p data-bind="text: 'A file already exists at the target path (./' + uploadPath() + '). Either change the subdirectory or rename the file and try again.'"></p></div>
                <div class="alert alert-info" data-bind="visible: subdirectory.isValid() && file.isValid() && uploadPath.isValid() && uploadPath()"><p data-bind="text: 'File will be uploaded to ./' + uploadPath()"></p></div>
            </div>
        </@f.form>
    </@p.panel>
    <@p.panel "covariate-body" "Covariate Settings" true>
        <p>Use the fields below to update the existing covariate settings.</p>
        <@f.form "covariate-form">
            <@f.formGroupGeneric "disease-picker" "Disease" "fa fa-medkit">
                <select id="disease-picker" class="form-control" data-bind="options: diseases, value: selectedDisease, optionsText: 'name', bootstrapDisable: isSubmitting()" ></select>
            </@f.formGroupGeneric>
            <@t.tableBody singular="covariate" title="Covariates">
                [
                    { name: 'name', display: 'Name' },
                    { name: 'count', display: 'Files' },
                    { name: 'discrete', display: 'Is discrete*' },
                    { name: 'state', display: 'Use for current disease' },
                    { name: 'info', display: '+' },
                    { name: '', display: '' }
                ]
            </@t.tableBody>
            <p>* Currently only 2-value binary discrete covariates are supported.</p>
        </@f.form>
    </@p.panel>
</div>
</@c.page>
