<coverage>
    <name>${basename}</name>
    <enabled>true</enabled>
    <nativeName>${basename}</nativeName>
    <nativeCoverageName>${basename}</nativeCoverageName>
    <nativeFormat>GeoTIFF</nativeFormat>
    <title>${basename}</title>
    <description>Generated from ${basename}</description>
    <keywords />
    <nativeCRS class="projected">GEOGCS[&quot;WGS84(DD)&quot;, &#xd;
        DATUM[&quot;WGS84&quot;, &#xd;
        SPHEROID[&quot;WGS84&quot;, 6378137.0, 298.257223563]], &#xd;
        PRIMEM[&quot;Greenwich&quot;, 0.0], &#xd;
        UNIT[&quot;degree&quot;, 0.017453292519943295], &#xd;
        AXIS[&quot;Geodetic longitude&quot;, EAST], &#xd;
        AXIS[&quot;Geodetic latitude&quot;, NORTH]]</nativeCRS>
    <srs>EPSG:4326</srs>
    <nativeBoundingBox>
        <minx>-180.0</minx>
        <maxx>180.0</maxx>
        <miny>-60.0</miny>
        <maxy>85.0</maxy>
        <crs class="projected">GEOGCS[&quot;WGS84(DD)&quot;, &#xd;
            DATUM[&quot;WGS84&quot;, &#xd;
            SPHEROID[&quot;WGS84&quot;, 6378137.0, 298.257223563]], &#xd;
            PRIMEM[&quot;Greenwich&quot;, 0.0], &#xd;
            UNIT[&quot;degree&quot;, 0.017453292519943295], &#xd;
            AXIS[&quot;Geodetic longitude&quot;, EAST], &#xd;
            AXIS[&quot;Geodetic latitude&quot;, NORTH]]</crs>
    </nativeBoundingBox>
    <latLonBoundingBox>
        <minx>-180.0</minx>
        <maxx>180.0</maxx>
        <miny>-60.0</miny>
        <maxy>85.0</maxy>
        <crs>GEOGCS[&quot;WGS84(DD)&quot;, &#xd;
            DATUM[&quot;WGS84&quot;, &#xd;
            SPHEROID[&quot;WGS84&quot;, 6378137.0, 298.257223563]], &#xd;
            PRIMEM[&quot;Greenwich&quot;, 0.0], &#xd;
            UNIT[&quot;degree&quot;, 0.017453292519943295], &#xd;
            AXIS[&quot;Geodetic longitude&quot;, EAST], &#xd;
            AXIS[&quot;Geodetic latitude&quot;, NORTH]]</crs>
    </latLonBoundingBox>
    <projectionPolicy>FORCE_DECLARED</projectionPolicy>
    <metadata>
        <entry key="time">
            <dimensionInfo>
                <enabled>false</enabled>
            </dimensionInfo>
        </entry>
        <entry key="cachingEnabled">true</entry>
        <entry key="elevation">
            <dimensionInfo>
                <enabled>false</enabled>
            </dimensionInfo>
        </entry>
        <entry key="dirName">${basename}</entry>
    </metadata>
    <grid dimension="2">
        <range>
            <low>0 0</low>
            <high>8640 3480</high>
        </range>
        <transform>
            <scaleX>0.041666666666666667</scaleX>
            <scaleY>-0.041666666666666667</scaleY>
            <shearX>0.0</shearX>
            <shearY>0.0</shearY>
            <translateX>-180</translateX>
            <translateY>85</translateY>
        </transform>
        <crs>EPSG:4326</crs>
    </grid>
    <supportedFormats>
        <string>GEOTIFF</string>
        <string>GIF</string>
        <string>PNG</string>
        <string>JPEG</string>
        <string>TIFF</string>
    </supportedFormats>
    <interpolationMethods>
        <string>bilinear</string>
        <string>bicubic</string>
    </interpolationMethods>
    <dimensions>
        <coverageDimension>
            <name>MEAN_RISK</name>
            <description>GridSampleDimension[0,1]</description>
            <range>
                <min>0.0</min>
                <max>1.0</max>
            </range>
            <nullValues>
                <double>-9999</double>
            </nullValues>
            <dimensionType>
                <name>REAL_64BITS</name>
            </dimensionType>
        </coverageDimension>
    </dimensions>
    <parameters>
        <entry>
            <string>InputTransparentColor</string>
            <string></string>
        </entry>
        <entry>
            <string>SUGGESTED_TILE_SIZE</string>
            <string>512,512</string>
        </entry>
    </parameters>
</coverage>