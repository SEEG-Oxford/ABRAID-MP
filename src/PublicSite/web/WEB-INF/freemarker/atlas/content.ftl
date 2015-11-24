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

    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet.markercluster/0.2/MarkerCluster.css">
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Source+Sans+Pro:600" type="text/css">
    <link rel="stylesheet" href="<@spring.url "/ext/c3/c3/c3.css" />">
    <link rel="stylesheet" href="<@spring.url "/ext/leaflet/zoomslider/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/ext/jquery/flipclock/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/map.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/atlas.css" />">
</head>
<body>
    <div id="map">
        <#include "layerselector.ftl"/>
        <#include "modelrundetails.ftl"/>
        <#include "legend.ftl"/>
    </div>
    <div class="modal fade" id="plotModal" tabindex="-1" role="dialog" aria-labelledby="plotModalLabel" data-bind="preventBubble: true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true" class="glyphicon glyphicon-remove"></span>
                        <span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title" id="helpModalLabel">ABRAID-MP Effect Curve</h4>
                </div>
                <div class="leaflet-bar" style="text-align: center; box-shadow: none; border-radius: 0; border: none">
                    <a class="leaflet-panel-button" data-bind="savePlot: { id: 'largePlot', name: activeCurve().name }" href="#">
                        <i class="fa fa-floppy-o"></i>&nbsp;&nbsp;Save PNG
                    </a>
                </div>
                <div class="modal-body" style="text-align: center; overflow: auto; padding-bottom: 5">
                    <div id="largePlot" data-bind="largeEffectPlot: activeCurve"></div>
                </div>
                <div class="modal-body" style="text-align: center; overflow: auto; padding-top: 0">
                    <div id="histogram" data-bind="covariateHistogramPlot: activeCurve"></div>
                </div>
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
            layers: ${layers},
            seegMember: ${seegMember?c}
        };
    </script>

    <!-- Require -->
    <script type="text/javascript" data-main="<@spring.url '/js/kickstart/atlas/content' />" src="//cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.min.js"></script>
</body>
</html>
