package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.PasswordResetRequest;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Tests the PasswordResetRequestDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class PasswordResetRequestDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private PasswordResetRequestDao passwordResetRequestDao;

    @Autowired
    private ExpertDao expertDao;

    private PasswordResetRequest createPasswordResetRequest(String email, String hashedKey) {
        PasswordResetRequest request = new PasswordResetRequest(expertDao.getByEmail(email), hashedKey);
        passwordResetRequestDao.save(request);
        flushAndClear();
        return request;
    }

    private PasswordResetRequest createPasswordResetRequestDirectly(String email, String hashedKey, DateTime requestDate) {
        executeSQLUpdate(String.format("INSERT INTO password_reset_request (expert_id, hashed_key, request_date) VALUES (%s, '%s', TIMESTAMP '%s')",
                expertDao.getByEmail(email).getId(), hashedKey, requestDate.toString("yyyy-MM-dd HH:mm:ss")));
        flushAndClear();
        return (PasswordResetRequest) selectUnique(passwordResetRequestDao.getAll(), having(on(PasswordResetRequest.class).getExpert().getEmail(), equalTo(email)));
    }

    @Test
    public void saveAndReloadPasswordResetRequest() {
        PasswordResetRequest request = createPasswordResetRequest("helena.patching@zoo.ox.ac.uk", "hashedKey1");
        request = passwordResetRequestDao.getById(request.getId());
        assertThat(request.getExpert().getEmail()).isEqualTo("helena.patching@zoo.ox.ac.uk");
        assertThat(request.getHashedKey()).isEqualTo("hashedKey1");
        assertThat(request.getRequestDate().isBefore(DateTime.now())).isTrue();
        assertThat(request.getRequestDate().isAfter(DateTime.now().minusHours(1))).isTrue();
    }

    @Test
    public void loadNonExistentPasswordResetRequest() {
        PasswordResetRequest request = passwordResetRequestDao.getById(54321);
        assertThat(request).isNull();
    }

    @Test
    public void removeRequestsIssuedForExpertRemovesCorrectRequests() {
        int retainedID = createPasswordResetRequest("helena.patching@zoo.ox.ac.uk", "hashedKey1").getId();
        createPasswordResetRequest("edward.wiles@zoo.ox.ac.uk", "hashedKey2");
        passwordResetRequestDao.removeRequestsIssuedForExpert(expertDao.getByEmail("edward.wiles@zoo.ox.ac.uk"));
        assertThat(passwordResetRequestDao.getAll()).hasSize(1);
        assertThat(passwordResetRequestDao.getAll().get(0).getId()).isEqualTo(retainedID);
    }

    @Test
    public void removeOldRequestsRemovesCorrectRequests() {
        int retainedID = createPasswordResetRequestDirectly("helena.patching@zoo.ox.ac.uk", "hashedKey1", DateTime.now().minusHours(23)).getId();
        createPasswordResetRequestDirectly("edward.wiles@zoo.ox.ac.uk", "hashedKey2", DateTime.now().minusHours(25));
        passwordResetRequestDao.removeOldRequests();
        assertThat(passwordResetRequestDao.getAll()).hasSize(1);
        assertThat(passwordResetRequestDao.getAll().get(0).getId()).isEqualTo(retainedID);
    }

    @Test
    public void deleteRemovesCorrectRequest() {
        int retainedID = createPasswordResetRequest("helena.patching@zoo.ox.ac.uk", "hashedKey1").getId();
        PasswordResetRequest removed = createPasswordResetRequest("edward.wiles@zoo.ox.ac.uk", "hashedKey2");
        passwordResetRequestDao.delete(removed);
        assertThat(passwordResetRequestDao.getAll()).hasSize(1);
        assertThat(passwordResetRequestDao.getAll().get(0).getId()).isEqualTo(retainedID);
    }

    @Test
    public void getAllPasswordResetRequestLocations() {
        int id1 = createPasswordResetRequest("helena.patching@zoo.ox.ac.uk", "hashedKey1").getId();
        int id2 = createPasswordResetRequest("edward.wiles@zoo.ox.ac.uk", "hashedKey2").getId();
        assertThat(passwordResetRequestDao.getAll()).hasSize(2);
        assertThat(extract(passwordResetRequestDao.getAll(), on(PasswordResetRequest.class).getId())).containsOnly(id1, id2);
    }
}
