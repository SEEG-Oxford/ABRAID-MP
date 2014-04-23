package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.util.List;

/**
 * A DTO for "MultiPolygon Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#multipolygon
 * @param <TCrs> The type of
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonMultiPolygonGeometry<TCrs extends GeoJsonCrs>
        extends GeoJsonGeometry<TCrs, GeoJsonMultiPolygonCoordinateSet> {
    public GeoJsonMultiPolygonGeometry() {
    }

    public GeoJsonMultiPolygonGeometry(MultiPolygon multiPolygon, TCrs crs, List<Double> bbox) {
        super(GeoJsonGeometryType.MULTI_POLYGON, extractCoordinates(multiPolygon), crs, bbox);
    }

    private static GeoJsonMultiPolygonCoordinateSet extractCoordinates(MultiPolygon multiPolygon) {
        GeoJsonMultiPolygonCoordinateSet multiPolygonCoordinateSet = new GeoJsonMultiPolygonCoordinateSet();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            GeoJsonPolygonCoordinateSet polygonCoordinateSet = new GeoJsonPolygonCoordinateSet();

            GeoJsonRingCoordinateSet exteriorRingCoordinateSet = getExteriorRingCoordinateSet(polygon);
            polygonCoordinateSet.add(exteriorRingCoordinateSet);

            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                GeoJsonRingCoordinateSet interiorRingCoordinateSet = getInteriorRingCoordinateSet(polygon, j);
                polygonCoordinateSet.add(interiorRingCoordinateSet);
            }
            multiPolygonCoordinateSet.add(polygonCoordinateSet);
        }
        return multiPolygonCoordinateSet;
    }

    private static GeoJsonRingCoordinateSet getExteriorRingCoordinateSet(Polygon polygon) {
        GeoJsonRingCoordinateSet ringCoordinateSet = new GeoJsonRingCoordinateSet();
        LineString exteriorRing = polygon.getExteriorRing();
        for (Coordinate coordinate : exteriorRing.getCoordinates()) {
            double x = coordinate.x;
            double y = coordinate.y;
            ringCoordinateSet.add(new GeoJsonCoordinate(x, y));
        }
        return ringCoordinateSet;
    }

    private static GeoJsonRingCoordinateSet getInteriorRingCoordinateSet(Polygon polygon, Integer j) {
        GeoJsonRingCoordinateSet ringCoordinateSet = new GeoJsonRingCoordinateSet();
        LineString interiorRing = polygon.getInteriorRingN(j);
        for (Coordinate coordinate : interiorRing.getCoordinates()) {
            double x = coordinate.x;
            double y = coordinate.y;
            ringCoordinateSet.add(new GeoJsonCoordinate(x, y));
        }
        return ringCoordinateSet;
    }
}

