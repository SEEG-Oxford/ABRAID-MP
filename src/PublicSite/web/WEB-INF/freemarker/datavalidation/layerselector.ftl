<div id="layerSelector">
    <h4>You are validating
        <select data-bind="options: validationTypes, value: selectedType"></select>
        of
        <select data-bind="foreach: groups, value: selectedDiseaseSet">
            <optgroup data-bind="attr: {label: groupLabel}, foreach: children">
                <option data-bind="html: name, option: $data"></option>
            </optgroup>
        </select>
        <span data-bind="if: showDiseaseExtentLayer">
            <select data-bind="options: selectedDiseaseSet().diseaseGroups, optionsText: 'name', value: selectedDisease"></select>
        </span>
    </h4>

    <div class="alert alert-info alert-dismissable" data-bind="visible: noFeaturesToReview()" style="text-align: center">
        <div data-bind="text: showDiseaseExtentLayer() ?
            'There are no administrative units in need of review for this disease' :
            'There are no occurrences in need of review for this disease'">
        </div>
    </div>

    <div class="alert alert-info alert-dismissable" data-bind="visible: notReadyForReview()" style="text-align: center">
        <div data-bind="text: showDiseaseExtentLayer() ?
            'This disease is not ready for disease extent reviews.' :
            'This disease is not ready for disease occurrence reviews.'">
        </div>
    </div>
</div>
