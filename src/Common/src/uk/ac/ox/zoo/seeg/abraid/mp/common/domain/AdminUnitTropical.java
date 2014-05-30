package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents an admin unit from the tropical shapefile.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "admin_unit_tropical_view")
@Immutable
public class AdminUnitTropical extends AdminUnitGlobalOrTropical {
    public AdminUnitTropical() {
    }

    public AdminUnitTropical(Integer gaulCode) {
        super(gaulCode);
    }
}
