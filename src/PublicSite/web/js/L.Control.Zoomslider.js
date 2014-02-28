/**
 * Zoom slider widget, available from Leaflet plugin page.
 * Taken from http://kartena.github.io/Leaflet.zoomslider/
 *
 * Copyright (c) 2012-2014, Kartena AB, Mattias Bengtsson
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

(function (factory) {
	// Packaging/modules magic dance
	var L;
	if (typeof define === 'function' && define.amd) {
		// AMD
		define(['leaflet'], factory);
	} else if (typeof module !== 'undefined') {
		// Node/CommonJS
		L = require('leaflet');
		module.exports = factory(L);
	} else {
		// Browser globals
		if (typeof window.L === 'undefined') {
			throw new Error('Leaflet must be loaded first');
        }
		factory(window.L);
	}
}(function (L) {
	'use strict';

	L.Control.Zoomslider = (function () {

		var Knob = L.Draggable.extend({
			initialize: function (element, stepHeight, knobHeight) {
				L.Draggable.prototype.initialize.call(this, element, element);
				this._element = element;

				this._stepHeight = stepHeight;
				this._knobHeight = knobHeight;

				this.on('predrag', function () {
					this._newPos.x = 0;
					this._newPos.y = this._adjust(this._newPos.y);
				}, this);
			},

			_adjust: function (y) {
				var value = Math.round(this._toValue(y));
				value = Math.max(0, Math.min(this._maxValue, value));
				return this._toY(value);
			},

			// y = k*v + m
			_toY: function (value) {
				return this._k * value + this._m;
			},
			// v = (y - m) / k
			_toValue: function (y) {
				return (y - this._m) / this._k;
			},

			setSteps: function (steps) {
				var sliderHeight = steps * this._stepHeight;
				this._maxValue = steps - 1;

				// conversion parameters
				// the conversion is just a common linear function.
				this._k = -this._stepHeight;
				this._m = sliderHeight - (this._stepHeight + this._knobHeight) / 2;
			},

			setPosition: function (y) {
				L.DomUtil.setPosition(this._element,
									  L.point(0, this._adjust(y)));
			},

			setValue: function (v) {
				this.setPosition(this._toY(v));
			},

			getValue: function () {
				return this._toValue(L.DomUtil.getPosition(this._element).y);
			}
		});

		var Zoomslider = L.Control.extend({
			options: {
				position: 'topleft',
				// Height of zoom-slider.png in px
				stepHeight: 8,
				// Height of the knob div in px (including border)
				knobHeight: 6,
				styleNS: 'leaflet-control-zoomslider'
			},

			onAdd: function (map) {
				this._map = map;
				this._ui = this._createUI();
				this._knob = new Knob(this._ui.knob,
									  this.options.stepHeight,
									  this.options.knobHeight);

				map.whenReady(this._initKnob,        this)
					.whenReady(this._initEvents,      this)
					.whenReady(this._updateSize,      this)
					.whenReady(this._updateKnobValue, this)
					.whenReady(this._updateDisabled,  this);
				return this._ui.bar;
			},

			onRemove: function (map) {
				map.off('zoomlevelschange',         this._updateSize,      this)
					.off('zoomend zoomlevelschange', this._updateKnobValue, this)
					.off('zoomend zoomlevelschange', this._updateDisabled,  this);
			},

			_createUI: function () {
				var ui = {},
					ns = this.options.styleNS;

				ui.bar     = L.DomUtil.create('div', ns + ' leaflet-bar');
				ui.zoomIn  = this._createZoomBtn('in', 'top', ui.bar);
				ui.wrap    = L.DomUtil.create('div', ns + '-wrap leaflet-bar-part', ui.bar);
				ui.zoomOut = this._createZoomBtn('out', 'bottom', ui.bar);
				ui.body    = L.DomUtil.create('div', ns + '-body', ui.wrap);
				ui.knob    = L.DomUtil.create('div', ns + '-knob');

				L.DomEvent.disableClickPropagation(ui.bar);
				L.DomEvent.disableClickPropagation(ui.knob);

				return ui;
			},
			_createZoomBtn: function (zoomDir, end, container) {
				var classDef = this.options.styleNS + '-' + zoomDir +
						' leaflet-bar-part' +
						' leaflet-bar-part-' + end,
					link = L.DomUtil.create('a', classDef, container);

				link.href = '#';
				link.title = 'Zoom ' + zoomDir;

				L.DomEvent.on(link, 'click', L.DomEvent.preventDefault);

				return link;
			},

			_initKnob: function () {
				this._knob.enable();
				this._ui.body.appendChild(this._ui.knob);
			},
			_initEvents: function () {
				this._map
					.on('zoomlevelschange',         this._updateSize,      this)
					.on('zoomend zoomlevelschange', this._updateKnobValue, this)
					.on('zoomend zoomlevelschange', this._updateDisabled,  this);

				L.DomEvent.on(this._ui.body,    'click', this._onSliderClick, this);
				L.DomEvent.on(this._ui.zoomIn,  'click', this._zoomIn,        this);
				L.DomEvent.on(this._ui.zoomOut, 'click', this._zoomOut,       this);

				this._knob.on('dragend', this._updateMapZoom, this);
			},

			_onSliderClick: function (e) {
				var first = (e.touches && e.touches.length === 1 ? e.touches[0] : e),
					y = L.DomEvent.getMousePosition(first, this._ui.body).y;

				this._knob.setPosition(y);
				this._updateMapZoom();
			},

			_zoomIn: function (e) {
				this._map.zoomIn(e.shiftKey ? 3 : 1);
			},
			_zoomOut: function (e) {
				this._map.zoomOut(e.shiftKey ? 3 : 1);
			},

			_zoomLevels: function () {
				var zoomLevels = this._map.getMaxZoom() - this._map.getMinZoom() + 1;
				return zoomLevels < Infinity ? zoomLevels : 0;
			},
			_toZoomLevel: function (value) {
				return value + this._map.getMinZoom();
			},
			_toValue: function (zoomLevel) {
				return zoomLevel - this._map.getMinZoom();
			},

			_updateSize: function () {
				var steps = this._zoomLevels();

				this._ui.body.style.height = this.options.stepHeight * steps + 'px';
				this._knob.setSteps(steps);
			},
			_updateMapZoom: function () {
				this._map.setZoom(this._toZoomLevel(this._knob.getValue()));
			},
			_updateKnobValue: function () {
				this._knob.setValue(this._toValue(this._map.getZoom()));
			},
			_updateDisabled: function () {
				var zoomLevel = this._map.getZoom(),
					className = this.options.styleNS + '-disabled';

				L.DomUtil.removeClass(this._ui.zoomIn,  className);
				L.DomUtil.removeClass(this._ui.zoomOut, className);

				if (zoomLevel === this._map.getMinZoom()) {
					L.DomUtil.addClass(this._ui.zoomOut, className);
				}
				if (zoomLevel === this._map.getMaxZoom()) {
					L.DomUtil.addClass(this._ui.zoomIn, className);
				}
			}
		});

		return Zoomslider;
	})();

	L.Map.addInitHook(function () {
		if (this.options.zoomsliderControl) {
			this.zoomsliderControl = new L.Control.Zoomslider();
			this.addControl(this.zoomsliderControl);
		}
	});

	L.control.zoomslider = function (options) {
		return new L.Control.Zoomslider(options);
	};
}));
