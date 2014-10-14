<div id="latestOccurrencesPanel" class="leaflet-top leaflet-right" style="display: none" data-bind="visible: occurrences().length > 0">
    <div class="leaflet-bar leaflet-control">
        <div data-bind="click: function () { toggle(); }, clickBubble: false" style="text-align: center; cursor: pointer">
            <strong><span data-bind="text: showOccurrences() ? 'Hide' : 'Show'"></span> recent occurrences</strong>
        </div>

        <div data-bind="if: showOccurrences">
            <div><hr /></div>
            <div data-bind="foreach: occurrences">
                <div><i class="fa fa-map-marker"></i> <span data-bind="text: properties.locationName"></span></div>
                <div><i class="fa fa-calendar"></i> <span data-bind="date: properties.occurrenceDate"></span></div>
                <a data-bind="attr: {href: properties.alert.url || '#'}" target="_blank">
                    <i class="fa fa-external-link"></i>
                    <span data-bind="text: properties.alert.feedName"></span>
                </a>
                <div data-bind="ifnot: ($index() + 1) === $parent.occurrences().length"><hr /></div>
            </div>
            <div data-bind="if: count() > 5">
                <div><hr /></div>
                <div style="text-align: center">Showing the most recent 5 of <span data-bind="text: count"></span> occurrences</div>
            </div>
        </div>
    </div>
</div>
