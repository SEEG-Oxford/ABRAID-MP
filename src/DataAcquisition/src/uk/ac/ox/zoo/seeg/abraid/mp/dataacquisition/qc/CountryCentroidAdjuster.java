package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.Map;

/**
 * If a location refers to an entire country, the point obtained from HealthMap will be the country's centroid.
 * Some country centroids are not on land (e.g. Philippines), so this class replaces such country points with some
 * predetermined centroids stored in the healthmap_country table.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CountryCentroidAdjuster {
    private static final String REPLACED_MESSAGE =
            "location (%.5f,%.5f) replaced with fixed country centroid (%.5f,%.5f)";

    private String message;

    public String getMessage() {
        return message;
    }

    /**
     * If the location is a country, determines whether the associated HealthMap country has a predetermined centroid.
     * If so, replaces the location point with the centroid.
     * @param location The location.
     * @param countryMap A mapping between HealthMap country IDs and HealthMap countries.
     * @return True if the location needed to be adjusted, otherwise false.
     */
    public boolean adjustCountryCentroid(Location location, Map<Integer, HealthMapCountry> countryMap) {
        if (location.getPrecision() == LocationPrecision.COUNTRY) {
            HealthMapCountry country = countryMap.get(location.getHealthMapCountryId());
            if (country != null) {
                Point centroid = country.getCentroidOverride();
                if (centroid != null) {
                    // Location is a country and a predetermined centroid exists
                    Point originalPoint = location.getGeom();
                    message = String.format(REPLACED_MESSAGE, originalPoint.getX(), originalPoint.getY(),
                            centroid.getX(), centroid.getY());
                    location.setGeom(country.getCentroidOverride());
                    return true;
                }
            }
        }

        return false;
    }
}
