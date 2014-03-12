<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->

<div id="sidePanel">
    <div id="datapointInfo">
        <div data-bind="ifnot: hasSelectedPoint">Select a point to view more details...</div>
        <ul id="locationDiv" data-bind="if: hasSelectedPoint" style="list-style-type: none; padding:0; margin:0;" >
            <li><h4 data-bind="text: selectedPoint().properties.alert.title"></h4></li>
            <li><i class="fa fa-map-marker" style="padding: 2px"></i>&nbsp;<p data-bind="text: selectedPoint().properties.countryName"></p></li>
            <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="text: moment(selectedPoint().properties.alert.publicationDate).lang('en-gb').format('LLL Z')"></p></li>
            <li style="margin:0 0 10px 0"> </li>
            <li><i class="fa fa-quote-left"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.summary"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.url"></p></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.alert.feedName"></p></li>
        </ul>
        <button class="btn btn-large btn-block btn-primary" type="button">Log in to submit review</button>
    </div>

    <div id="counter">
        Validated n points
    </div>
</div>