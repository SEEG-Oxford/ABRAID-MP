<div id="counterDiv">
    <span>You have validated</span>
    <div id="counter" data-bind="counter: count"></div>
    <span data-bind="text: count() == 1 ? 'occurrence' : 'occurrences'"></span>
</div>
