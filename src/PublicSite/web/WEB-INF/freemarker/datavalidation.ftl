<#--
    The map page for an expert to submit reviews.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<#import "/spring.ftl" as spring />

<#assign endOfHeadContent>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet.markercluster/0.2/MarkerCluster.css">
    <link rel="stylesheet" href="<@spring.url "/css/markers.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/dataValidation.css" />">
</#assign>

<#assign endOfBodyScriptContent>
    var wmsUrl = "http://localhost:8081/geoserver/abraid/wms";
    var loggedIn = ${userLoggedIn?c};
    var diseaseOccurrenceReviewCount = ${reviewCount?c};
    <#if diseaseInterests??>
        var diseaseInterests = [
            <#list diseaseInterests as disease>
                {
                    name: "${disease.getName()?lower_case?js_string}",
                    id: ${disease.id?c}
                },
            </#list>
        ];
    <#else>
        var diseaseInterests = [
            {
                name: "${defaultValidatorDiseaseGroupName}",
                id: 0
            }
        ];
    </#if>
    var allOtherDiseases = [
        <#if allOtherDiseases??>
            <#list allOtherDiseases as disease>
                {
                    name: "${disease.getName()?lower_case?js_string}",
                    id: ${disease.id?c}
                },
            </#list>
        </#if>
    ];

</#assign>

<#assign endOfBodyContent>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.5.1/moment.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.5.1/lang/en-gb.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.js"></script>
    <script src="http://leaflet.github.io/Leaflet.markercluster/dist/leaflet.markercluster-src.js"></script>
    <script src="<@spring.url "/js/L.Control.Zoomslider.js"/>"></script>
    <script src="<@spring.url "/js/dataValidation.js"/>"></script>
    <script src="<@spring.url "/js/leafletMap.js"/>"></script>
    <script src="<@spring.url "/js/flipclock.min.js"/>"></script>
    <script src="<@spring.url "/js/login.js" />"></script>
</#assign>

<@c.page title="ABRAID MP" endOfHead=endOfHeadContent endOfBody=endOfBodyContent endOfBodyScript=endOfBodyScriptContent>
<div id="dataValidation">
    <#include "datavalidationsidepanel.ftl"/>

    <div id="layerSelector">
        <h4>You are validating
            <select data-bind="options: validationTypes, value: selectedType"></select>
        of
            <select data-bind="foreach: groups, value: selectedDisease">
                <optgroup data-bind="attr: {label: groupLabel}, foreach: children">
                    <option data-bind="html: name, option: $data"></option>
                </optgroup>
            </select>
        </h4>
        <div class="alert alert-info" data-bind="visible: noOccurrencesLeftToReview()" style="text-align: center">
            There are no occurrences in need of review for this disease.
        </div>
    </div>

    <div id="map"></div>
</div>
</@c.page>
