<div id="layerSelector">
    <h4 class="hidden" data-bind="css: { 'hidden': false }">You are validating
        <select data-bind="options: validationTypes, value: selectedType, preventBubble: true"></select>
        of
        <select data-bind="foreach: groups, value: selectedDiseaseSet, preventBubble: true">
            <optgroup data-bind="attr: {label: groupLabel}, foreach: children">
                <option data-bind="html: name, option: $data"></option>
            </optgroup>
        </select>
        <span data-bind="if: showDiseaseExtentLayer">
            <select data-bind="options: selectedDiseaseSet().diseaseGroups, optionsText: 'name', value: selectedDisease, valueAllowUnset: true, preventBubble: true"></select>
        </span>
    </h4>

    <div class="alert alert-info" style="display: none" data-bind="visible: noFeaturesToReview()">
        <div data-bind="text: showDiseaseExtentLayer() ?
            'Use the right-hand dropdown box to select the specific disease of interest.' :
            'There are no occurrences in need of review for this disease'">
        </div>
    </div>

    <#if !userLoggedIn>
        <div class="alert alert-info" style="display: none" data-bind="visible: !noFeaturesToReview()">
            <div data-bind="text: showDiseaseExtentLayer() ?
                'Log in to start validating regions.' :
                'Log in to start validating occurrences.'">
            </div>
        </div>
    </#if>
</div>
