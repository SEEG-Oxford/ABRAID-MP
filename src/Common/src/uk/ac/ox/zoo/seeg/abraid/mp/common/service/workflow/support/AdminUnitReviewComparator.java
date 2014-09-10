package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Custom comparator to sort list of reviews by experts (asc), admin unit gaul code (asc), then date (desc).
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitReviewComparator implements Comparator<AdminUnitReview> {

    private Comparator<AdminUnitReview> expertComparator;
    private Comparator<AdminUnitReview> gaulCodeComparator;
    private Comparator<AdminUnitReview> dateComparator;

    public AdminUnitReviewComparator() {
         expertComparator = new Comparator<AdminUnitReview>() {
            @Override
            public int compare(AdminUnitReview o1, AdminUnitReview o2) {
                return o1.getExpert().getId().compareTo(o2.getExpert().getId());
            }
        };
        gaulCodeComparator = new Comparator<AdminUnitReview>() {
            @Override
            public int compare(AdminUnitReview o1, AdminUnitReview o2) {
                return o1.getAdminUnitGlobalOrTropicalGaulCode().compareTo(o2.getAdminUnitGlobalOrTropicalGaulCode());
            }
        };
        dateComparator = new Comparator<AdminUnitReview>() {
            @Override
            public int compare(AdminUnitReview o1, AdminUnitReview o2) {
                return o1.getCreatedDate().compareTo(o2.getCreatedDate());
            }
        };
    }

    @Override
    public int compare(AdminUnitReview o1, AdminUnitReview o2) {
        List<Comparator<AdminUnitReview>> listComparators =
                Arrays.asList(expertComparator, gaulCodeComparator, Collections.reverseOrder(dateComparator));

        for (Comparator<AdminUnitReview> comparator : listComparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
