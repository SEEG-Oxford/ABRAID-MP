package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

/**
 * A Json DTO for countries, including their geographic bounds.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCountry {
    // Fall back extent, used during tests, when the database geoms are missing.
    private static final int FALLBACK_MIN_X = -180;
    private static final int FALLBACK_MAX_X = 180;
    private static final int FALLBACK_MIN_Y = -60;
    private static final int FALLBACK_MAX_Y = 85;

    private String name;
    private int gaulCode;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public JsonCountry(Country country) {
        this.name = country.getName();
        this.gaulCode = country.getGaulCode();
        Envelope envelope = extractEnvelope(country.getGeom());
        this.minX = envelope.getMinX();
        this.maxX = envelope.getMaxX();
        this.minY = envelope.getMinY();
        this.maxY = envelope.getMaxY();
    }

    private static Envelope extractEnvelope(MultiPolygon geom) {
        return (geom != null) ?
                geom.getEnvelopeInternal() :
                new Envelope(FALLBACK_MIN_X, FALLBACK_MAX_X, FALLBACK_MIN_Y, FALLBACK_MAX_Y);
    }

    public String getName() {
        return name;
    }

    public int getGaulCode() {
        return gaulCode;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }
}
