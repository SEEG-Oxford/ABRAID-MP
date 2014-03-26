<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div id="sidePanel">
    <div id="datapointInfo" data-bind="template: { name: hasSelectedPoint() ? 'selected-point-template' : 'no-selected-point-template' }"></div>
    <div id="counter">
        <@security.authorize ifAnyGranted="ROLE_USER">
            <ul>You have validated <span data-bind="text: reviewCount()">0</span> point<span data-bind="if: reviewCount() != 1">s</span></ul>
        </@security.authorize>
    </div>
</div>

<script type="text/html" id="no-selected-point-template">
    <ul>
        <li>
            <div class="alert alert-info alert-dismissable" id="submitReviewSuccess" style="display:none">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                Review submitted.
            </div>
        </li>
        <li>Select a point on the map to view more details here...</li>
    </ul>
</script>
<script type="text/html" id="selected-point-template">
    <ul>
        <li><h4 data-bind="text: selectedPoint().properties.alert.title"></h4></li>
        <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.locationName"></p></li>
        <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="text: moment(selectedPoint().properties.alert.publicationDate).lang('en-gb').format('LL')"></p></li>
        <li>
            <i class="fa fa-external-link"></i>
            <a data-bind="attr: {href: selectedPoint().properties.alert.url}">
                <span data-bind="text: selectedPoint().properties.alert.feedName"></span>
            </a>
        </li>
        <li><i class="fa fa-quote-left"></i></li>
        <li><div id="summary" data-bind="html: selectedPoint().properties.alert.summary"></div></li>
        <li><i class="fa fa-quote-right"></i></li>

    </ul>
    <div id="datapointInfoLowerPane">
    <@security.authorize ifAnyGranted="ROLE_USER">
        <div class="btn-group">
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('YES')"><i class="fa fa-check"></i>&nbsp;Valid</button>
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('UNSURE')">Unsure<br /></button>
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('NO')"><i class="fa fa-times"></i>&nbsp;Invalid</button>
        </div>
    </@security.authorize>
    <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
        <div>
            <button class="btn btn-block btn-primary" type="button" disabled="disabled">Log in to submit review</button>
        </div>
    </@security.authorize>
    </div>
</script>