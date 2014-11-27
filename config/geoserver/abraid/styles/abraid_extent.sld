<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns:sld="http://www.opengis.net/sld" xmlns="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" version="1.0.0">
    <sld:UserLayer>
        <sld:LayerFeatureConstraints>
            <sld:FeatureTypeConstraint />
        </sld:LayerFeatureConstraints>
        <sld:UserStyle>
            <sld:FeatureTypeStyle>
                <sld:Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>disease_extent_class</ogc:PropertyName>
                            <ogc:Literal>PRESENCE</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">#8e1b65</sld:CssParameter>
                        </sld:Fill>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>disease_extent_class</ogc:PropertyName>
                            <ogc:Literal>POSSIBLE_PRESENCE</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">#c478a9</sld:CssParameter>
                        </sld:Fill>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>disease_extent_class</ogc:PropertyName>
                            <ogc:Literal>UNCERTAIN</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">#ffffbf</sld:CssParameter>
                        </sld:Fill>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>disease_extent_class</ogc:PropertyName>
                            <ogc:Literal>POSSIBLE_ABSENCE</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">#b5caaa</sld:CssParameter>
                        </sld:Fill>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>disease_extent_class</ogc:PropertyName>
                            <ogc:Literal>ABSENCE</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">#769766</sld:CssParameter>
                        </sld:Fill>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:PolygonSymbolizer>
                        <sld:Stroke>
                            <sld:CssParameter name="stroke">#8c8c8c</sld:CssParameter>
                            <sld:CssParameter name="stroke-width">0.25</sld:CssParameter>
                        </sld:Stroke>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
            </sld:FeatureTypeStyle>
        </sld:UserStyle>
    </sld:UserLayer>
</sld:StyledLayerDescriptor>