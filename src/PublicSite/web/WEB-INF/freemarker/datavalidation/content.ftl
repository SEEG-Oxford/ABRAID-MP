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

    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Source+Sans+Pro:600" type="text/css">
    <link rel="stylesheet" href="<@spring.url "/ext/leaflet/leaflet/leaflet.css" />">
    <link rel="stylesheet" href="<@spring.url "/ext/leaflet/zoomslider/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/ext/jquery/flipclock/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/map.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/dataValidation.css" />">
    <#include "../analytics.ftl"/>
</head>
<body>
    <div id="dataValidation">
        <#include "sidepanel.ftl"/>

        <div id="map">
            <#include "layerselector.ftl"/>
            <#include "helpmodal.ftl"/>
            <#include "latestoccurrencespanel.ftl"/>
            <div id="spinner" data-bind="fadeVisible: { visible: visible() }, preventBubble: true">
                <i class="fa fa-3x fa-spinner fa-spin"></i>
                <div class="background"></div>
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
            wmsUrl: "${baseWmsUrl?js_string}",
            loggedIn: ${userLoggedIn?c},
            showHelpText: ${showHelpText?c},
            diseasesRequiringExtentInput: [<#list diseasesRequiringExtentInput as disease>${disease?c},</#list>],
            diseasesRequiringOccurrenceInput: [<#list diseasesRequiringOccurrenceInput as disease>${disease?c},</#list>],
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
                                    }<#if diseaseGroup_has_next>,</#if>
                                </#list>
                            ]
                        },
                    </#list>
                <#else>
                    {
                        name: "${defaultValidatorDiseaseGroupName?js_string}",
                        diseaseGroups: [
                            {
                                id: 87,
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
                                    }<#if diseaseGroup_has_next>,</#if>
                                </#list>
                            ]
                        },
                    </#list>
                </#if>
            ]
        };
    </script>

    <!-- Require -->
    <script type="text/javascript" data-main="<@spring.url '/js/kickstart/datavalidation/content' />" src="//cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.min.js"></script>
</body>
</html>
