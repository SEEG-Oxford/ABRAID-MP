<#--
    The map page for an expert to view disease occurrences and extents, and to submit reviews.
    This content will reside in an IFrame, on the ABRAID-MP public site and TGHN website.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>MAP</title>

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap-theme.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet.markercluster/0.2/MarkerCluster.css">
    <link rel="stylesheet" href="<@spring.url "/css/markers.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/dataValidation.css" />">
</head>
<body>
    <div id="dataValidation">
        <#include "datavalidationsidepanel.ftl"/>
        <#include "layerselector.ftl"/>

        <div id="map"></div>
    </div>
    <script src="//cdnjs.cloudflare.com/ajax/libs/knockout/3.0.0/knockout-debug.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.0/jquery.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/modernizr/2.7.1/modernizr.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/js/bootstrap.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.5.1/moment.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.5.1/lang/en-gb.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js"></script>
    <script src="http://leaflet.github.io/Leaflet.markercluster/dist/leaflet.markercluster-src.js"></script>
    <script src="<@spring.url "/js/L.Control.Zoomslider.js"/>"></script>
    <script src="<@spring.url "/js/flipclock.min.js"/>"></script>
    <script src="<@spring.url "/js/navbar.js" />"></script>
    <script>
        var baseUrl = "<@spring.url "/" />";
        var wmsUrl = "http://localhost:8081/geoserver/abraid/wms";
        var loggedIn = ${userLoggedIn?c};
        var diseaseOccurrenceReviewCount = ${reviewCount?c};
        var diseaseInterests = [
            <#if diseaseInterests??>
                <#list diseaseInterests as validatorDiseaseGroup>
                    {
                        name: "${validatorDiseaseGroup.getName()?lower_case?js_string}",
                        id: ${validatorDiseaseGroup.id?c},
                        diseaseGroups: [
                            <#list validatorDiseaseGroupMap[validatorDiseaseGroup.getName()] as diseaseGroup>
                                {
                                    name: "${diseaseGroup.getName()?lower_case?js_string}",
                                    id: ${diseaseGroup.id?c}
                                },
                            </#list>
                        ]
                    },
                </#list>
            <#else>
                {
                    name: "${defaultValidatorDiseaseGroupName}",
                    id: 0
                }
            </#if>
        ];
        var allOtherDiseases = [
            <#if allOtherDiseases??>
                <#list allOtherDiseases as validatorDiseaseGroup>
                    {
                        name: "${validatorDiseaseGroup.getName()?lower_case?js_string}",
                        id: ${validatorDiseaseGroup.id?c},
                        diseaseGroups: [
                            <#list validatorDiseaseGroupMap[validatorDiseaseGroup.getName()] as diseaseGroup>
                                {
                                    name: "${diseaseGroup.getName()?lower_case?js_string}",
                                    id: ${diseaseGroup.id?c}
                                },
                            </#list>
                        ]
                    },
                </#list>
            </#if>
        ];
    </script>
    <script src="<@spring.url "/js/dataValidation.js"/>"></script>
    <script src="<@spring.url "/js/leafletMap.js"/>"></script>
    <script src="<@spring.url "/js/login.js" />"></script>
</body>
</html>
