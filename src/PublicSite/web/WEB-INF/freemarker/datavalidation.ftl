<#--
    The map page for an expert to submit reviews.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<#import "/spring.ftl" as spring />

<#assign endOfHeadContent>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
</#assign>

<#assign endOfBodyScriptContent>
    var wmsUrl = "http://localhost:8081/geoserver/abraid/wms";
    var diseaseInterests = [
        <#list diseaseInterests as diseaseInterest>
            { name: "${diseaseInterest.name}", id: "${diseaseInterest.id}" },
        </#list>
    ];
</#assign>

<#assign endOfBodyContent>
    <script src="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.js"></script>
    <script src="<@spring.url "/js/L.Control.Zoomslider.js"/>"></script>
    <script src="<@spring.url "/js/ViewModels.js"/>"></script>
    <script src="<@spring.url "/js/leafletMap.js"/>"></script>
</#assign>

<@c.page title="ABRAID MP" endOfHead=endOfHeadContent endOfBody=endOfBodyContent endOfBodyScript=endOfBodyScriptContent>
    <#include "datavalidationsidepanel.ftl"/>

    <div id="layerSelector">
            <h4>You are validating
            <select data-bind="options: validationTypes, value: selectedType"></select>
        of
            <select data-bind="options: diseaseInterests, optionsText: 'name', value: selectedDisease"></select>
        </h4>
    </div>

    <div id="map"></div>
</@c.page>

