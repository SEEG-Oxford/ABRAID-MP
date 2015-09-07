package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.vividsolutions.jts.geom.Envelope;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

/**
 * A Json DTO for countries, including their geographic bounds.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCountry {
    private String name;
    private int gaulCode;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public JsonCountry(Country country) {
        this.name = country.getName();
        this.gaulCode = country.getGaulCode();
        Envelope envelope = country.getGeom().getEnvelopeInternal();
        this.minX = envelope.getMinX();
        this.maxX = envelope.getMaxX();
        this.minY = envelope.getMinY();
        this.maxY = envelope.getMaxY();
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
