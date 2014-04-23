<div id="layerSelector">
    <h4>You are validating
        <select data-bind="options: validationTypes, value: selectedType"></select>
        of
        <select data-bind="foreach: groups, value: selectedDisease">
            <optgroup data-bind="attr: {label: groupLabel}, foreach: children">
                <option data-bind="html: name, option: $data"></option>
            </optgroup>
        </select>
        <span data-bind="if: selectedType() == 'disease extent'">
            <select data-bind="options: selectedDisease().diseaseGroups, optionsText: 'name', value: selectedDiseaseGroup"></select>
        </span>
    </h4>

    <div data-bind="if: selectedType() == 'disease occurrences'">
        <div class="alert alert-info alert-dismissable" data-bind="visible: noOccurrencesToReview()" style="text-align: center">
            There are no occurrences in need of review for this disease.
        </div>
    </div>
</div>