package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents an admin unit from the global shapefile.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "admin_unit_global_view")
@Immutable
public class AdminUnitGlobal extends AdminUnitGlobalOrTropical {
    public AdminUnitGlobal() {
    }

    public AdminUnitGlobal(Integer gaulCode) {
        super(gaulCode);
    }

    public AdminUnitGlobal(Integer gaulCode, Integer countryGaulCode) {
        super(gaulCode, countryGaulCode);
    }
}
