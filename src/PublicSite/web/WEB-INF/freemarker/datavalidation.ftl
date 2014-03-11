<#--
    The map page for an expert to submit reviews.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<@c.page title="ABRAID MP">

    <#include "datavalidationsidepanel.ftl"/>

    <script>
        var diseaseInterests = [
            <#list diseaseInterests as diseaseInterest>
                { name: "${diseaseInterest.name}", id: "${diseaseInterest.id}" },
            </#list>
        ];
    </script>

    <div id="layerSelector">
        <h4>You are validating
            <select class="layerSelectorDropDown" data-bind="options: validationTypes, value: selectedType"></select>
        of
            <select class="layerSelectorDropDown" data-bind="options: diseaseInterests, optionsText: 'name', value: selectedDisease"></select>
        </h4>
    </div>

    <div id="map"></div>


    <script src="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.js"></script>
    <script src="js/L.Control.Zoomslider.js"></script>
    <script src="js/ViewModels.js"></script>
    <script src="js/leafletMap.js"></script>

</@c.page>