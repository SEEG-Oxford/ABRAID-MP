<#--
    A page for the data extractor tool.
    Copyright (c) 2015 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/panel.ftl" as p/>


<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var data = {
        wmsUrl: "${baseWmsUrl}",
        runs: ${runs},
        countries: ${countries},
    };
</script>
</#assign>
<@c.page title="ABRAID-MP: Location Details" mainjs="/js/kickstart/tools/dataextractor" bootstrapData=bootstrapData>
<div class="container">
    <div class="container" id="page">
        <@p.panel "location-details-body" "Data Extractor" true>
            <form id="location-details-form" action="" data-bind="formSubmit: submit">
                <div class="row">
                    <div class="col-md-6">
                        <@f.formGroupGeneric "disease-picker" "Disease" "fa fa-medkit">
                            <select id="disease-picker" class="form-control" data-bind="options: diseases, value: disease, bootstrapDisable: isSubmitting()" ></select>
                        </@f.formGroupGeneric>
                    </div>
                    <div class="col-md-6">
                        <@f.formGroupGeneric "run-picker" "Model Run Date" "fa fa-calendar">
                            <select id="run-picker" class="form-control" data-bind="options: runs, value: run, optionsText: 'date', bootstrapDisable: isSubmitting()" ></select>
                        </@f.formGroupGeneric>
                    </div>
                </div>
                <p class="form-group">
                    <label for="mode">Mode:&nbsp;&nbsp;</label>
                    <span class="btn-group" id="mode" data-bind="foreach: [ {value: 'precise', label: 'Lat/Long'}, {value: 'country', label: 'Country'} ]">
                        <label class="btn btn-default" data-bind="css: {active : $parent.mode() === value, disabled: find('isSubmitting')}">
                            <input type="radio" name="mode" data-bind="formRadio: { selected: $parent.mode, value: value }">
                            <span data-bind="text: label"></span>
                        </label>
                    </span>
                </p>
                <div class="panel panel-default panel-body" data-bind="visible: mode() == 'precise'">
                    <div class="row">
                        <div class="col-md-6">
                            <@f.formGroupBasic "latitude" "Latitude" "lat" "fa fa-lg fa-bars" />
                        </div>
                        <div class="col-md-6">
                            <@f.formGroupBasic "longitude" "Longitude" "lng" "fa fa-lg fa-bars fa-rotate-90" />
                        </div>
                    </div>
                    <p class="form-group">
                        <button type="submit" class="btn btn-primary disabled" style="float: left; margin-right: 20px" data-bind="formButton: { submitting: 'Calculating Risk Score', standard: 'Extract Risk Score' }" disabled>&nbsp;</button>
                        <input style="width: 100px; float: left; background-color: #ffffff; cursor: text" class="form-control" placeholder="?" readonly data-bind="disabled: find('isSubmitting') || (!find('isValid')), value: score">
                    </p>
                </div>
                <div class="panel panel-default panel-body" data-bind="visible: mode() == 'country'">
                    <@f.formGroupGeneric "country-picker" "Country" "fa fa-flag">
                        <select id="country-picker" class="form-control" data-bind="options: countries, value: country, optionsText: 'name', bootstrapDisable: isSubmitting()" ></select>
                    </@f.formGroupGeneric>
                    <a class="btn btn-primary" data-bind="attr: { href: pngUrl }" target="_blank">Extract Styled Image PNG</a>
                    <a class="btn btn-primary" data-bind="attr: { href: tifUrl }" target="_blank">Extract Raw Data GeoTIFF</a>
                </div>
                <div class="form-group" data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
            </form>
        </@p.panel>
    </div>
</@c.page>
