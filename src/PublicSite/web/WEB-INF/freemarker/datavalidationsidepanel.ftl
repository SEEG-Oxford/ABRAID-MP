<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->

<div id="sidePanel">
    <div id="datapointInfo">
        <!-- ko ifnot: hasSelectedPoint -->
            <div>
                Select a point on the map to view more details here...
            </div>
        <!-- /ko -->
        <!-- ko if: hasSelectedPoint -->
            <div>
                <ul>
                    <li><h4 data-bind="text: selectedPoint().properties.alert.title"></h4></li>
                    <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.countryName"></p></li>
                    <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="text: moment(selectedPoint().properties.alert.publicationDate).lang('en-gb').format('LL')"></p></li>
                    <li>
                        <div id="summary">
                            <ul>
                                <li><i class="fa fa-quote-left"></i></li>
                                <li><p data-bind="text: selectedPoint().properties.alert.summary"></p></li>
                                <li><i class="fa fa-quote-right"></i></li>
                            </ul>
                        </div>
                    </li>
                    <li><i class="fa fa-external-link"></i>&nbsp;
                        <a data-bind="attr: {href: selectedPoint().properties.alert.url}">
                            <span data-bind="text: selectedPoint().properties.alert.feedName"></span>
                        </a>
                    </li>
                </ul>
                <button class="btn btn-large btn-block btn-primary" type="button">Log in to submit review</button>
            </div>
        <!-- /ko -->
    </div>

    <div id="counter">
        Validated n points
    </div>
</div>