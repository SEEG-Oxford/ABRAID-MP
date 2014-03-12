<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->

<div id="sidePanel">
    <div id="datapointInfo">
        <div data-bind="ifnot: hasSelectedPoint">Select a point to view more details...</div>
        <ul id="locationDiv" data-bind="if: hasSelectedPoint" style="list-style-type: none; padding:0; margin:0;" >
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.title"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.summary"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.url"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.feedName"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.publicationDate"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.countryName"></p></li>
        </ul>
        <button class="btn btn-large btn-block btn-primary" type="button">Log in to submit review</button>
    </div>

    <div id="counter">
        Validated n points
    </div>
</div>