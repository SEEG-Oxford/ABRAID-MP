<div id="helpText">
    <div class="leaflet-top leaflet-left">
        <div class="leaflet-bar leaflet-control" style="margin-top: 180px">
            <a data-bind="click: function () { showPanel() }">
                <i class="fa fa-lg fa-info-circle"></i>
            </a>
        </div>
    </div>
    <div style="display: none" data-bind="visible: visible">
        <div class="overlayBackground"></div>
        <div id="helpTextPanel" class="leaflet-bar leaflet-control">
            <button type="button" class="close" data-bind="click: function () { hidePanel() }">x</button>
            <div id="helpTextContent">
                <#include "helptextcontent.ftl"/>
            </div>
        </div>
    </div>
</div>
