package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.commons.lang.builder.CompareToBuilder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Custom comparator to sort list of reviews by expert (asc), admin unit gaul code (asc), then date (desc).
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitReviewComparator implements Comparator<AdminUnitReview>, Serializable {

    @Override
    public int compare(AdminUnitReview o1, AdminUnitReview o2) {
        return new CompareToBuilder()
                .append(o1.getExpert().getId(), o2.getExpert().getId()) // ascending
                .append(o1.getAdminUnitGlobalOrTropicalGaulCode(), o2.getAdminUnitGlobalOrTropicalGaulCode()) // ascend.
                .append(o2.getCreatedDate(), o1.getCreatedDate()) // descending
                .toComparison();
    }
}
