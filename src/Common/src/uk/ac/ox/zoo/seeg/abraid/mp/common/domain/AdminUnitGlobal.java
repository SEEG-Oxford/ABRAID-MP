package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents an admin unit from the global shapefile.
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries(
        @NamedQuery(
                name = "getGlobalAdminUnitByGaulCode",
                query = "from AdminUnitGlobal where gaulCode=:gaulCode"
        )
)
@Entity
@Table(name = "admin_unit_global")
@Immutable
public class AdminUnitGlobal extends AdminUnitGlobalOrTropical {
}
