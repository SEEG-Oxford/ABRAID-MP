<#--
    A page to allow users to upload a CSV file for data acquisition.
    Copyright (c) 2014 University of Oxford
-->
<#import "../shared/layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/panel.ftl" as p/>

<@c.page title="ABRAID-MP: Upload CSV" mainjs="/js/kickstart/tools/uploadcsv">
<div class="container">
    <div class="container">
        <@p.panel "upload-csv-body" "Upload CSV File" true>
            <p>Upload a disease occurrences CSV file below.</p>
            <@f.form "upload-csv-form" "Upload" "Uploading...">
                <@f.formGroupFile "file-picker" "File" "file" ".csv" />
            </@f.form>
            <br>
            <br>
            <p> The CSV file must have a header row, and have columns in the following order (column names are
                ignored). It must also use the UTF-8 character set, though this is only an issue if special characters
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
                    <td>Must be in the format DD/MM/YYYY, MM/YYYY or YYYY</td>
                </tr>
                <tr>
                    <td>Title</td>
                    <td>Yes</td>
                    <td>Text to display in the Data Validator</td>
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
                </tbody>
            </table>
        </@p.panel>
</div>
</@c.page>