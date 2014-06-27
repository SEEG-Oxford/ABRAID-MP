<#--
    Display information about each disease occurrence
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<div id="sidePanel">
    <div id="sidePanelContent" data-bind="template: { name : templateName() }"></div>
    <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
        <#include "loginform.ftl"/>
    </@security.authorize>
</div>

<script type="text/html" id="admin-units-template">
    <!-- ko with:selectedAdminUnitViewModel -->
    <div>
        <div>
            <div id="adminUnitTable" class="table-responsive">
                <table class="table table-condensed table-hover">
                    <thead>
                        <tr>
                            <th>Occurrences</th>
                            <th>Administrative Unit</th>
                        </tr>
                    </thead>
                    <tbody data-bind="foreach: adminUnits" >
                        <tr data-bind="click: function (data, event) {
                            $parent.selectedAdminUnit(this);
                            $(event.target).parent().addClass('highlight').siblings().removeClass('highlight');
                        }">
                            <td class="occurrencesColumn" data-bind="text: count"></td>
                            <td data-bind="text: name"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div data-bind="if: hasSelectedAdminUnit()">
            <ul>
                <div class="sidePanelText">
                    <span data-bind="text: selectedAdminUnit().name"></span> has
                    <span data-bind="text: selectedAdminUnit().count"></span>
                    <span data-bind="text: selectedAdminUnit().count == 1 ? 'occurrence' : 'occurrences'"></span>
                </div>
            </ul>
        <@security.authorize ifAnyGranted="ROLE_USER">
            <div id="reviewButtons">
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('PRESENCE') }">Presence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('POSSIBLE_PRESENCE') }">Possible presence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('UNCERTAIN') }">Uncertain<br /></button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('POSSIBLE_ABSENCE') }">Possible absence</button>
                <button type="button" class="btn btn-primary btn-sm btn-block" data-bind="click: function () { submitReview('ABSENCE') }">Absence</button>
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
            <span>You have validated</span>
            <div id="counter" data-bind="counter: count"></div>
            <span data-bind="text: count() == 1 ? 'region' : 'regions'"></span>
        </div>
    </@security.authorize>
    <!-- /ko -->
</script>

<script type="text/html" id="occurrences-template">
    <!-- ko with:selectedPointViewModel -->
        <div data-bind="template: hasSelectedPoint() ? 'selected-point-template' : 'no-selected-point-template'"></div>
        <@security.authorize ifAnyGranted="ROLE_USER">
            <div id="counterDiv" data-bind="with: counter">
                <span>You have validated</span>
                <div id="counter" data-bind="counter: count"></div>
                <span data-bind="text: count() == 1 ? 'occurrence' : 'occurrences'"></span>
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
        <li>
            <i class="fa fa-external-link"></i>
            <a data-bind="text: selectedPoint().properties.alert.feedName,
                          attr: {href: selectedPoint().properties.alert.url}" target="_blank"></a>
        </li>
        <li><i class="fa fa-quote-left"></i></li>
        <li>
            <div class="sidePanelText" data-bind="html: selectedPoint().properties.alert.summary || '<i>No summary available</i>'"></div>
            <a id="translationLink" data-bind="if: selectedPoint().properties.alert.summary,
                                               attr: {href: translationUrl}" target="_blank">View translation</a>
        </li>
        <li><i class="fa fa-quote-right"></i></li>
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
