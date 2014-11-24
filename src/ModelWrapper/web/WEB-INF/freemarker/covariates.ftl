<#--
    ModelWrapper's covariate list page, to add and edit covariate files.
    Copyright (c) 2014 University of Oxford
-->
<#import "shared/layout/common.ftl" as c/>
<#import "shared/layout/form.ftl" as f/>
<#import "shared/layout/table.ftl" as t/>
<#import "shared/layout/panel.ftl" as p/>
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
                <input type="text" data-bind="formValue: name, attr: { title: name }, css: { 'transparent-input': !mouseOver() }" placeholder="A name must be provided" >
            </td>
            <td><input type="text" data-bind="formValue: path, attr: { title: path }" readonly="true" class="transparent-input" ></td>
            <td><input type="checkbox" data-bind="formChecked: state"></td>
            <td>
                <button class="btn btn-default fa fa-lg fa-trash-o" style="float: right" data-bind="popover: { title: 'Delete file?', trigger: 'focus', placement: 'bottom', template: 'file-list-delete-template'}, click: function(data, event) { event.preventDefault(); }, bootstrapDisable: $parent.isSubmitting()"></button>
                <span data-bind="if: info">
                    <i class="fa fa-lg fa-info-circle text-info" data-bind="tooltip: { title: info, placement: 'bottom' }"></i>&nbsp;
                </span>
            </td>
        </tr>
    </@t.tableTemplates>
    <script type="text/html" id="file-list-delete-template">
        <p>This file is currently used in <span data-bind="text: usageCount"></span> <span data-bind="text: usageCount() === 1 ? 'disease' : 'diseases'"></span>. Are you sure you want to delete it?</p><br>
        <p style="text-align:center;">
            <span class="btn btn-default" data-bind="click: function () { hide(true) }" data-dismiss="popover">Confirm<span>
        </p>
    </script>
</#assign>

<@c.page title="ABRAID-MP ModelWrapper" mainjs="/js/kickstart/covariates" bootstrapData=bootstrapData templates=templates>
<div class="container">
    <@p.panel "add-covariate-body" "Add Covariate File" true>
        <p>Use the fields below to add new covariate files to the system.</p>
        <@f.form "add-covariate-form" "Upload" "Uploading...">
            <@f.formGroupBasic "file-name" "Name" "name" "glyphicon glyphicon-pencil" />
            <@f.formGroupBasic "file-dir" "Subdirectory" "subdirectory" "glyphicon glyphicon-folder-open" />
            <@f.formGroupFile "file-picker" "File" "file" />
            <div class="hidden" data-bind="css: { hidden: false }">
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
                    { name: 'path', display: 'Path' },
                    { name: 'state', display: 'Use for current disease' },
                    { name: 'info', display: '+' }
                ]
            </@t.tableBody>
        </@f.form>
    </@p.panel>
</div>
</@c.page>
