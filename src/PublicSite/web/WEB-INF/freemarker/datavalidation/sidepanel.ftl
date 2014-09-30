<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div id="sidePanel">
    <div id="sidePanelContent" data-bind="template: { name : templateName() }"></div>
</div>

<script type="text/html" id="admin-units-template">
    <!-- ko with:selectedAdminUnitViewModel -->
    <div>
        <div>
            <table id="adminUnitTable" class="table table-responsive table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Occurrences</th>
                        <th>Administrative Unit</th>
                    </tr>
                </thead>
                <tbody data-bind="foreach: adminUnits" >
                    <tr data-bind="click: function () { $parent.selectedAdminUnit($data); },
                                   highlight: { target: $parent.selectedAdminUnit(), compareOn: 'id' }">
                        <td data-bind="text: count"></td>
                        <td data-bind="text: name"></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div id="remainingCount">
            <span class="sidePanelTextAnnotation" data-bind="visible: adminUnits().length > 0">
                <span data-bind="text: adminUnits().length + ' region' + (adminUnits().length !== 1 ? 's' : '') + ' remaining'"></span>
            </span>
        </div>
        <div data-bind="if: hasSelectedAdminUnit()">
        <@security.authorize ifAnyGranted="ROLE_USER">
            <div id="reviewButtons">
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('PRESENCE') }">Presence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('POSSIBLE_PRESENCE') }">Possible presence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('UNCERTAIN') }">Uncertain<br /></button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('POSSIBLE_ABSENCE') }">Possible absence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('ABSENCE') }">Absence</button>
                <button type="button" class="btn btn-danger btn-sm btn-block" data-bind="click: function () { submitReview(null) }">I don't know</button>
            </div>
        </@security.authorize>
        </div>
        <div data-bind="if: !hasSelectedAdminUnit()">
            <div id="submitReviewSuccess" style="display:none">
                <button type="button" class="btn btn-primary" disabled="disabled">Review submitted</button>
            </div>
        </div>
    </div>
    <@security.authorize ifAnyGranted="ROLE_USER">
        <div id="counterDiv" data-bind="with: counter">
            <div data-bind="text: count() > 99999 ? 'You have validated more than' : 'You have validated'"></div>
            <div id="counter">
                <div><div data-bind="counter: count"></div></div>
            </div>
            <div data-bind="text: count() == 1 ? 'region' : 'regions'"></div>
        </div>
    </@security.authorize>
    <!-- /ko -->
</script>

<script type="text/html" id="occurrences-template">
    <!-- ko with:selectedPointViewModel -->
        <div data-bind="template: hasSelectedPoint() ? 'selected-point-template' : 'no-selected-point-template'"></div>
        <@security.authorize ifAnyGranted="ROLE_USER">
            <div id="counterDiv" data-bind="with: counter">
                <div data-bind="text: count() > 99999 ? 'You have validated more than' : 'You have validated'"></div>
                <div id="counter">
                    <div><div data-bind="counter: count"></div></div>
                </div>
                <div data-bind="text: count() == 1 ? 'occurrence' : 'occurrences'"></div>
            </div>
        </@security.authorize>
    <!-- /ko -->
</script>

<script type="text/html" id="no-selected-point-template">
    <ul>
        <div class="sidePanelText">Select a feature on the map to view more details here...</div>
    </ul>
    <div id="submitReviewSuccess" style="display:none">
        <button type="button" class="btn btn-primary" disabled="disabled">Review submitted</button>
    </div>
</script>

<script type="text/html" id="selected-point-template">
    <ul>
        <li><h4 data-bind="text: selectedPoint().properties.alert.title"></h4></li>
        <li><i class="fa fa-medkit"></i>&nbsp;<p data-bind="html: selectedPoint().properties.diseaseGroupPublicName"></p></li>
        <li><i class="fa fa-map-marker"></i>&nbsp;<p data-bind="text: selectedPoint().properties.locationName"></p></li>
        <li><i class="fa fa-calendar"></i>&nbsp;<p data-bind="date: selectedPoint().properties.occurrenceDate"></p></li>
        <li data-bind="if: selectedPoint().properties.alert.url">
            <i class="fa fa-external-link"></i>
            <a data-bind="text: selectedPoint().properties.alert.feedName,
                          attr: {href: selectedPoint().properties.alert.url || '#'}" target="_blank"></a>
        </li>
        <li data-bind="ifnot: selectedPoint().properties.alert.url">
            <i class="fa fa-external-link"></i>
            <span data-bind="text: selectedPoint().properties.alert.feedName"></span>
        </li>
        <li style="position: absolute; top: 165px; bottom: 195px; width: 265px;">
            <ul style="padding: 0px; max-height: 100%; overflow: hidden">
                <li><i class="fa fa-quote-left"></i></li>
                <li style="position: absolute; top: 20px; bottom: 20px; width: 100%;">
                    <div class="sidePanelText" style="max-height: 100%; margin: 0 5px;" data-bind="html: selectedPoint().properties.alert.summary || '<i>No summary available</i>'"></div>
                    <div style="margin: 2px 0;"><i class="fa fa-quote-right"></i><a id="translationLink" data-bind="if: selectedPoint().properties.alert.summary, attr: {href: translationUrl}" target="_blank">View translation</a></div>
                </li>

            </ul>
        </li>
    </ul>
    <div id="reviewButtons">
    <@security.authorize ifAnyGranted="ROLE_USER">
        <div class="btn-group">
            <button type="button" class="btn btn-primary" data-bind="click: function () { submitReview('YES') }"><i class="fa fa-check"></i>&nbsp;Valid</button>
            <button type="button" class="btn btn-primary" data-bind="click: function () { submitReview('UNSURE') }">Unsure<br /></button>
            <button type="button" class="btn btn-primary" data-bind="click: function () { submitReview('NO') }"><i class="fa fa-times"></i>&nbsp;Invalid</button>
        </div>
    </@security.authorize>
    </div>
</script>
