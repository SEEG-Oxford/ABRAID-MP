package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;
import java.util.Collection;

/**
 * Helper for the AdminExpertsController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminExpertsHelper {
    private static final String FAIL_NO_ID_MATCH = "At least one expert was specified with an invalid ID (%s).";
    private ExpertService expertService;

    @Autowired
    public AdminExpertsHelper(ExpertService expertService) {
        this.expertService = expertService;
    }

    /**
     * Updates the database entries for a set of experts.
     * @param experts The experts to update.
     * @throws ValidationException Thrown if an id matching expert can not be found for one of the argument experts.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertsAsTransaction(Collection<JsonExpertFull> experts) throws ValidationException {
        // Start of transaction
        for (JsonExpertFull expertDto : experts) {
            Expert expert = expertService.getExpertById(expertDto.getId());
            if (expert == null) {
                // Roll back
                throw new ValidationException(Arrays.asList(String.format(FAIL_NO_ID_MATCH, expertDto.getId())));
            } else {
                expert.setVisibilityApproved(expertDto.getVisibilityApproved());
                expert.setWeighting(expertDto.getWeighting());
                expert.setAdministrator(expertDto.isAdministrator());
                expert.setSeegMember(expertDto.isSEEGMember());
                expertService.saveExpert(expert);
            }
        }
        // End of transaction
    }
}
