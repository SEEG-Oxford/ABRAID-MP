package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.collection.IsIn.isIn;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertUpdateHelper {
    private static final String FAIL_NO_ID_MATCH = "No matching expert found to update (S5).";
    private ExpertService expertService;
    private DiseaseService diseaseService;

    @Autowired
    public ExpertUpdateHelper(ExpertService expertService, DiseaseService diseaseService) {
        this.expertService = expertService;
        this.diseaseService = diseaseService;
    }

    /**
     * Updates the database entry for an experts.
     * @param id The expert to update.
     * @param expertDto The data to overwrite.
     * @throws ValidationException Thrown if an id matching expert can not be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertsAsTransaction(int id, JsonExpertDetails expertDto) throws ValidationException {
        // Start of transaction
        Expert expert = expertService.getExpertById(id);
        if (expert == null) {
            // Roll back
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_ID_MATCH, id)));
        } else {
            boolean resetVisibility =
                    !expert.getName().equals(expertDto.getName()) ||
                    !expert.getJobTitle().equals(expertDto.getJobTitle()) ||
                    !expert.getInstitution().equals(expertDto.getInstitution()) ||
                    expert.getVisibilityRequested() != expertDto.getVisibilityRequested();

            List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
            List<ValidatorDiseaseGroup> interests = filter(
                    having(on(ValidatorDiseaseGroup.class).getId(), isIn(expertDto.getDiseaseInterests())),
                    allValidatorDiseaseGroups);

            expert.setValidatorDiseaseGroups(interests);
            expert.setName(expertDto.getName());
            expert.setJobTitle(expertDto.getJobTitle());
            expert.setInstitution(expertDto.getInstitution());
            expert.setVisibilityRequested(expertDto.getVisibilityRequested());

            if (resetVisibility) {
                expert.setVisibilityApproved(false);
                expert.setUpdatedDate(DateTime.now());
            }

            expertService.saveExpert(expert);
        }
        // End of transaction
    }


}
