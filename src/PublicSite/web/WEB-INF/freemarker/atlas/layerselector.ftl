<div id="layerSelector">
    <h4 class="hidden" data-bind="css: { 'hidden': false }">You are viewing the
        <select data-bind="options: types, optionsText: 'display', value: selectedType, preventBubble: true"></select>
        of
        <select data-bind="options: diseases, optionsText: 'disease', value: selectedDisease, preventBubble: true"></select>
        for
        <select data-bind="options: runs, optionsText: 'date', value: selectedRun, preventBubble: true"></select>
    </h4>
</div>
