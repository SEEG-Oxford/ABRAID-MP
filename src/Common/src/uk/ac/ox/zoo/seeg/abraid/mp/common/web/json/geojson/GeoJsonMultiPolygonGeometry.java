package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.util.List;

/**
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonMultiPolygonGeometry<TCrs extends GeoJsonCrs> extends GeoJsonGeometry<TCrs, MultiPolygonCoordinateSet> {
    public GeoJsonMultiPolygonGeometry() {
    }

    public GeoJsonMultiPolygonGeometry(MultiPolygon multiPolygon, TCrs crs, List<Double> bbox) {
        super(GeoJsonGeometryType.MULTI_POLYGON, extractCoordinates(multiPolygon), crs, bbox);
    }

    private static MultiPolygonCoordinateSet extractCoordinates(MultiPolygon multiPolygon) {
        MultiPolygonCoordinateSet multiPolygonCoordinateSet = new MultiPolygonCoordinateSet();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            PolygonCoordinateSet polygonCoordinateSet = new PolygonCoordinateSet();

            RingCoordinateSet exteriorRingCoordinateSet = getExteriorRingCoordinateSet(polygon);
            polygonCoordinateSet.add(exteriorRingCoordinateSet);

            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                RingCoordinateSet interiorRingCoordinateSet = getInteriorRingCoordinateSet(polygon, j);
                polygonCoordinateSet.add(interiorRingCoordinateSet);
            }
            multiPolygonCoordinateSet.add(polygonCoordinateSet);
        }
        return multiPolygonCoordinateSet;
    }

    private static RingCoordinateSet getExteriorRingCoordinateSet(Polygon polygon) {
        RingCoordinateSet ringCoordinateSet = new RingCoordinateSet();
        LineString exteriorRing = polygon.getExteriorRing();
        for (Coordinate coordinate : exteriorRing.getCoordinates()) {
            double x = coordinate.x;
            double y = coordinate.y;
            ringCoordinateSet.add(new PairCoordinateSet(x, y));
        }
        return ringCoordinateSet;
    }

    private static RingCoordinateSet getInteriorRingCoordinateSet(Polygon polygon, Integer j) {
        RingCoordinateSet ringCoordinateSet = new RingCoordinateSet();
        LineString interiorRing = polygon.getInteriorRingN(j);
        for (Coordinate coordinate : interiorRing.getCoordinates()) {
            double x = coordinate.x;
            double y = coordinate.y;
            ringCoordinateSet.add(new PairCoordinateSet(x, y));
        }
        return ringCoordinateSet;
    }
}

