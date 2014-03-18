<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div id="sidePanel" data-bind="template: { name: hasSelectedPoint() ? 'selected-point-template' : 'no-selected-point-template' }" ></div>

<script type="text/html" id="no-selected-point-template">
    <div class="datapointInfo">
        Select a point on the map to view more details here...
    </div>
</script>
<script type="text/html" id="selected-point-template">
    <div class="datapointInfo">
        <ul>
            <li><h4 data-bind="text: selectedPoint().properties.alert.title"></h4></li>
            <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.countryName"></p></li>
            <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="text: moment(selectedPoint().properties.alert.publicationDate).lang('en-gb').format('LL')"></p></li>
            <li><i class="fa fa-quote-left"></i></li>
            <li><div id="summary" data-bind="html: selectedPoint().properties.alert.summary"></div></li>
            <li><i class="fa fa-quote-right"></i></li>
        </ul>
        <div id="datapointInfoLowerPane">
            <i class="fa fa-external-link"></i>&nbsp;
            <a data-bind="attr: {href: selectedPoint().properties.alert.url}">
                <span data-bind="text: selectedPoint().properties.alert.feedName"></span>
            </a>
        <@security.authorize ifAnyGranted="ROLE_USER">
            <div class="btn-group">
                <button type="button" class="btn btn-success"><i class="fa fa-check"></i>&nbsp;Valid occurrence</button>
                <button type="button" class="btn btn-danger"><i class="fa fa-times"></i>&nbsp;Invalid occurrence</button>
            </div>
            <div id="counter">
                Validated n points
            </div>
        </@security.authorize>
        <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
            <div>
                <button id="newLogIn" class="btn btn-block btn-primary" type="button" disabled="disabled">Log in to submit review</button>
            </div>
        </@security.authorize>
        </div>
    </div>
</script>