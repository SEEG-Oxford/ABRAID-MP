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
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:600'  type='text/css'>
    <link rel="stylesheet" href="<@spring.url "/css/login.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/markers.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/dataValidation.css" />">
</head>
<body>
    <div id="dataValidation">
        <#include "sidepanel.ftl"/>
        <#include "layerselector.ftl"/>

        <div id="map">
            <div id="spinner" data-bind="fadeVisible: { visible: visible() }">
                <i class="fa fa-3x fa-spinner fa-spin"></i>
                <div></div>
            </div>
        </div>
    </div>

    <!-- Base url -->
    <script>
        var baseUrl = "<@spring.url '/'/>";
    </script>

    <!-- Bootstrapped JS data for KO view models -->
    <script>
        var data = {
            wmsUrl: "http://localhost:8081/geoserver/abraid/wms",
            loggedIn: ${userLoggedIn?c},
            diseaseOccurrenceReviewCount: ${diseaseOccurrenceReviewCount?c},
            adminUnitReviewCount: ${adminUnitReviewCount?c},
            diseaseInterests: [
                <#if diseaseInterests??>
                    <#list diseaseInterests as validatorDiseaseGroup>
                        {
                            name: "${validatorDiseaseGroup.getName()?js_string}",
                            id: ${validatorDiseaseGroup.id?c},
                            diseaseGroups: [
                                <#list validatorDiseaseGroupMap[validatorDiseaseGroup.getName()] as diseaseGroup>
                                    {
                                        name: "${diseaseGroup.getShortNameForDisplay()?js_string}",
                                        id: ${diseaseGroup.id?c}
                                    },
                                </#list>
                            ]
                        },
                    </#list>
                <#else>
                    {
                        name: "${defaultValidatorDiseaseGroupName?js_string}",
                        diseaseGroups: [
                            {
                                name: "${defaultDiseaseGroupShortName?js_string}"
                            }
                        ]
                    }
                </#if>
            ],
            allOtherDiseases: [
                <#if allOtherDiseases??>
                    <#list allOtherDiseases as validatorDiseaseGroup>
                        {
                            name: "${validatorDiseaseGroup.getName()?js_string}",
                            id: ${validatorDiseaseGroup.id?c},
                            diseaseGroups: [
                                <#list validatorDiseaseGroupMap[validatorDiseaseGroup.getName()] as diseaseGroup>
                                    {
                                        name: "${diseaseGroup.getShortNameForDisplay()?js_string}",
                                        id: ${diseaseGroup.id?c}
                                    },
                                </#list>
                            ]
                        },
                    </#list>
                </#if>
            ]
        };
    </script>

    <!-- Require -->
    <script type="text/javascript" data-main="<@spring.url '/js/kickstart/datavalidation/content' />" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
</body>
</html>
