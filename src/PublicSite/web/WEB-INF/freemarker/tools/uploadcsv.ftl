<#--
    A page to allow users to upload a CSV file for data acquisition.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/panel.ftl" as p/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var diseaseGroups = ${diseaseGroups};
</script>
</#assign>

<@c.page title="ABRAID-MP: Upload CSV" mainjs="/js/kickstart/tools/uploadcsv" bootstrapData=bootstrapData>
<div class="container">
    <div class="container">
        <@p.panel "upload-csv-body" "Upload CSV File" true>
            <p>Upload a disease occurrences CSV file below.</p>
            <@f.form "upload-csv-form" "Upload" "Uploading...">
                <@f.formGroupFile "file-picker" "File" "file" ".csv" />
                <p class="form-group">
                    <label for="is-bias">Is this a normal set of disease occurrences, or should it be used as a sample bias dataset only.</label>
                    <span id="is-bias" class="input-group btn-group" data-bind="foreach: [ { value: false, label: 'Normal' }, { value: true, label: 'Bias' } ]">
                        <label class="btn btn-default" data-bind="css: { active: $parent.isBias() === value, disabled: find('isSubmitting') }">
                            <input type="radio" name="is-bias" data-bind="formRadio: { selected: $parent.isBias, value: value }">
                            <span data-bind="text: label"></span>
                        </label>
                    </span>
                </p>
                <p class="form-group" data-bind="visible: isBias">
                    <label for="bias-disease">What disease should this be used as a sampling bias dataset for?</label>
                    <span id="bias-disease" class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-medkit"></i>
                        </span>
                        <select id="disease-group-picker" name="bias-disease" class="form-control" data-bind="options: diseaseGroups, value: selectedDiseaseGroup, optionsText: 'name', valueAllowUnset: true" ></select>
                    </span>
                    <span>This file will replace any existing bias set for the chosen disease. To remove a bias dataset (and resume using filter based bias datasets) upload an empty csv (header row only).</span>
                </p>
                <p class="form-group" data-bind="visible: !isBias()">
                    <label for="is-gold-standard">Is this a "gold standard" data set? If so, final weightings will be set to 1 and the occurrences will not appear on the Data Validator.</label>
                    <span id="is-gold-standard" class="input-group btn-group" data-bind="foreach: [ { value: true, label: 'Yes' }, { value: false, label: 'No' } ]">
                        <label class="btn btn-default" data-bind="css: { active: $parent.isGoldStandard() === value, disabled: find('isSubmitting') }">
                            <input type="radio" name="is-gold-standard" data-bind="formRadio: { selected: $parent.isGoldStandard, value: value }">
                            <span data-bind="text: label"></span>
                        </label>
                    </span>
                </p>
            </@f.form>

            <br>
            <br>
            <p> The CSV file must have a header row, and have columns in the following order (column names are
                ignored). It must also use the UTF-8 character set, although this is only an issue if special characters
                appear in the text.
            </p>
            <br>
            <br>
            <table class="table table-condensed">
                <thead>
                <tr>
                    <th>Column Name</th>
                    <th>Required?</th>
                    <th>Notes</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Site</td>
                    <td>Yes</td>
                    <td>The name of the occurrence location</td>
                </tr>
                <tr>
                    <td>Longitude</td>
                    <td>Yes</td>
                    <td>Must be between -180 and 180, using up to 5 decimal places</td>
                </tr>
                <tr>
                    <td>Latitude</td>
                    <td>Yes</td>
                    <td>Must be between -90 and 90, using up to 5 decimal places</td>
                </tr>
                <tr>
                    <td>Precision</td>
                    <td>Yes</td>
                    <td>Must be country, admin1, admin2 or precise</td>
                </tr>
                <tr>
                    <td>Country</td>
                    <td>Yes</td>
                    <td>Must be an ABRAID country name</td>
                </tr>
                <tr>
                    <td>Disease</td>
                    <td>Yes</td>
                    <td>Must be an ABRAID disease name</td>
                </tr>
                <tr>
                    <td>Occurrence Date</td>
                    <td>Yes</td>
                    <td>Must be in the format DD/MM/YYYY, MM/YYYY, YYYY, MMM-YY or MMM-YYYY</td>
                </tr>
                <tr>
                    <td>Feed</td>
                    <td>Yes</td>
                    <td>The source of the data</td>
                </tr>
                <tr>
                    <td>Summary</td>
                    <td>No</td>
                    <td>Text to display in the Data Validator</td>
                </tr>
                <tr>
                    <td>URL</td>
                    <td>No</td>
                    <td>URL to display in the Data Validator</td>
                </tr>
                <tr>
                    <td>Alert Title</td>
                    <td>No</td>
                    <td>Text to display in the Data Validator</td>
                </tr>
                </tbody>
            </table>
        </@p.panel>
</div>
</@c.page>
