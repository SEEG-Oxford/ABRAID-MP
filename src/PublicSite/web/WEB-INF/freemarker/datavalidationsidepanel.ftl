<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div id="sidePanel">
    <div id="datapointInfo" data-bind="template: { name: hasSelectedPoint() ? 'selected-point-template' : 'no-selected-point-template' }"></div>
    <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
        <form id="logIn">
            <p data-bind="text: formAlert"></p>
            <p class="form-group">
                <span class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-user"></i>
                    </span>
                    <input type="text" class="form-control" placeholder="Email address" data-bind="value: formUsername" >
                </span>
            </p>
            <p class="form-group">
                <span class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-lock"></i>
                    </span>
                    <input type="password" class="form-control" placeholder="Password" data-bind="value: formPassword">
                </span>
            </p>
            <p class="form-group">
                <input type="submit" class="btn btn-primary" value="Log in to start validating" data-bind="click: attemptFormLogin">
            </p>
        </form>
    </@security.authorize>
    <@security.authorize ifAnyGranted="ROLE_USER">
    <div id="counterDiv">
        You have validated<div id="counter" data-bind="counter: reviewCount"></div>occurrence<span data-bind="if: reviewCount() != 1">s</span> for this disease
    </div>
    </@security.authorize>
</div>

<script type="text/html" id="no-selected-point-template">
    <ul>
        <li>
            <div class="alert alert-info alert-dismissable" id="submitReviewSuccess" style="display:none">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
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
        <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="date: selectedPoint().properties.alert.publicationDate"></p></li>
        <li>
            <i class="fa fa-external-link"></i>
            <a data-bind="attr: {href: selectedPoint().properties.alert.url}">
                <span data-bind="text: selectedPoint().properties.alert.feedName"></span>
            </a>
        </li>
        <li><i class="fa fa-quote-left"></i></li>
        <li>
            <div id="summary" data-bind="html: selectedPoint().properties.alert.summary || '<i>No summary available</i>'"></div>
            <div data-bind="if: selectedPoint().properties.alert.summary != null">
                <a id="translationLink" data-bind="attr: {href: translationUrl}" target="_blank">View translation</a>
            <div>
        </li>
        <li><i class="fa fa-quote-right"></i></li>
    </ul>
    <div id="reviewButtons">
    <@security.authorize ifAnyGranted="ROLE_USER">
        <div class="btn-group">
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('YES')"><i class="fa fa-check"></i>&nbsp;Valid</button>
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('UNSURE')">Unsure<br /></button>
            <button type="button" class="btn btn-primary" data-bind="click: submitReview('NO')"><i class="fa fa-times"></i>&nbsp;Invalid</button>
        </div>
    </@security.authorize>
    </div>
</script>
