<#--
    The atlas page for display of model results.
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

    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap-theme.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet.markercluster/0.2/MarkerCluster.css">
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Source+Sans+Pro:600" type="text/css">
    <link rel="stylesheet" href="<@spring.url "/css/markers.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/map.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/atlas.css" />">
</head>
<body>
    <div id="map">
        <#include "layerselector.ftl"/>
        <#include "modelrundetails.ftl"/>
        <div class="leaflet-bottom leaflet-left" id="legend" style="display: none" data-bind="visible: true">
            <div class="legend leaflet-bar leaflet-control continuous" style="display: none" data-bind="visible: type() == 'continuous'">
                <div style="text-align: center">Probability of<br>infections occurring<br></div>
                <i style="background-color:#769766;"></i>
                <i style="background-color:#769766; background-image: -webkit-gradient(linear, left, right, from(#769766), to(#b5caaa)); background-image: -webkit-linear-gradient(left, #769766, #b5caaa); background-image: -moz-linear-gradient(left, #769766, #b5caaa); background-image: -o-linear-gradient(left, #769766, #b5caaa); background-image: linear-gradient(to right, #769766, #b5caaa);"></i>
                <i style="background-color:#b5caaa; background-image: -webkit-gradient(linear, left, right, from(#b5caaa), to(#ffffbf)); background-image: -webkit-linear-gradient(left, #b5caaa, #ffffbf); background-image: -moz-linear-gradient(left, #b5caaa, #ffffbf); background-image: -o-linear-gradient(left, #b5caaa, #ffffbf); background-image: linear-gradient(to right, #b5caaa, #ffffbf);"></i>
                <i style="background-color:#ffffbf; background-image: -webkit-gradient(linear, left, right, from(#ffffbf), to(#c478a9)); background-image: -webkit-linear-gradient(left, #ffffbf, #c478a9); background-image: -moz-linear-gradient(left, #ffffbf, #c478a9); background-image: -o-linear-gradient(left, #ffffbf, #c478a9); background-image: linear-gradient(to right, #ffffbf, #c478a9);"></i>
                <i style="background-color:#c478a9; background-image: -webkit-gradient(linear, left, right, from(#c478a9), to(#8e1b65)); background-image: -webkit-linear-gradient(left, #c478a9, #8e1b65); background-image: -moz-linear-gradient(left, #c478a9, #8e1b65); background-image: -o-linear-gradient(left, #c478a9, #8e1b65); background-image: linear-gradient(to right, #c478a9, #8e1b65);"></i>
                <i style="background-color:#8e1b65;"></i><br>
                <span style="width: 50%; float: left">0</span><span style="width: 50%; float: right; text-align: right">1</span>
            </div>
            <div class="legend leaflet-bar leaflet-control" style="display: none" data-bind="visible: type() == 'discrete'">
                <i style="background:#8e1b65"></i><span>Presence</span><br>
                <i style="background:#c478a9"></i><span>Possible presence</span><br>
                <i style="background:#ffffbf"></i><span>Uncertain</span><br>
                <i style="background:#b5caaa"></i><span>Possible absence</span><br>
                <i style="background:#769766"></i><span>Absence</span><br>
            </div>
        </div>
    </div>
    <#--<#include "atlasdisclaimer.ftl"/>-->

    <!-- Base url -->
    <script>
        var baseUrl = "<@spring.url '/'/>";
    </script>

    <!-- Bootstrapped JS data for KO view models -->
    <script>
        var data = {
            wmsUrl: "${baseWmsUrl}",
            layers: ${layers}
        };
    </script>

    <!-- Require -->
    <script type="text/javascript" data-main="<@spring.url '/js/kickstart/atlas/content' />" src="//cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
</body>
</html>
