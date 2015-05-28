package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the ExpertService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertServiceTest {
    private ExpertService expertService;
    private AdminUnitReviewDao adminUnitReviewDao;
    private ExpertDao expertDao;
    private DiseaseGroupDao diseaseGroupDao;
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;
    private PasswordResetRequestDao passwordResetRequestDao;
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        adminUnitReviewDao = mock(AdminUnitReviewDao.class);
        expertDao = mock(ExpertDao.class);
        diseaseGroupDao = mock(DiseaseGroupDao.class);
        diseaseOccurrenceDao = mock(DiseaseOccurrenceDao.class);
        diseaseOccurrenceReviewDao = mock(DiseaseOccurrenceReviewDao.class);
        passwordResetRequestDao = mock(PasswordResetRequestDao.class);
        passwordEncoder = mock(PasswordEncoder.class);
        expertService = new ExpertServiceImpl(adminUnitReviewDao, expertDao, diseaseGroupDao, diseaseOccurrenceDao,
                diseaseOccurrenceReviewDao, passwordResetRequestDao, passwordEncoder);
    }

    @Test
    public void getDiseaseInterestsReturnsExpectedList() {
        // Arrange
        int expertId = 1;
        Expert expert = new Expert();
        ValidatorDiseaseGroup group1 = new ValidatorDiseaseGroup();
        ValidatorDiseaseGroup group2 = new ValidatorDiseaseGroup();

        List<ValidatorDiseaseGroup> testList = new ArrayList<>();
        testList.add(group2);
        testList.add(group1);

        expert.setValidatorDiseaseGroups(testList);
        when(expertDao.getById(expertId)).thenReturn(expert);

        // Act
        List<ValidatorDiseaseGroup> list = expertService.getDiseaseInterests(expertId);

        // Assert
        assertThat(list).isEqualTo(testList);
    }

    @Test
    public void getDiseaseOccurrenceReviewCountReturnsExpectedLong() {
        // Arrange
        List<DiseaseOccurrenceReview> reviews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            reviews.add(new DiseaseOccurrenceReview());
        }
        Long n = (long) reviews.size();
        when(diseaseOccurrenceReviewDao.getCountByExpertId(anyInt())).thenReturn(n);

        // Act
        Long reviewCount = expertService.getDiseaseOccurrenceReviewCount(1);

        // Assert
        assertThat(reviewCount).isEqualTo(n);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnExpectedListForSeegUser() {
        // Arrange
        List<DiseaseOccurrence> testList = new ArrayList<>();
        when(expertDao.getById(anyInt())).thenReturn(new Expert());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(anyInt(), anyBoolean(), anyInt())).thenReturn(testList);

        // Act
        List<DiseaseOccurrence> list = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, true, 1);

        // Assert
        assertThat(list).isSameAs(testList);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnExpectedListForNonSeegUser() {
        // Arrange
        List<DiseaseOccurrence> testList = new ArrayList<>();
        when(expertDao.getById(anyInt())).thenReturn(new Expert());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(anyInt(), anyBoolean(), anyInt())).thenReturn(testList);

        // Act
        List<DiseaseOccurrence> list = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, false, 1);

        // Assert
        assertThat(list).isSameAs(testList);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnEmptyListIfExpertDoesNotExist() {
        // Arrange
        when(expertDao.getById(anyInt())).thenReturn(null); // For any expertId, act as if the expert does not exist

        // Act
        List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(0, false, 0);

        // Assert
        assertThat(occurrences.size()).isEqualTo(0);
        assertThat(occurrences.isEmpty());
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnEmptyListIfDiseaseGroupDoesNotExist() {
        // Arrange
        when(diseaseGroupDao.getById(anyInt())).thenReturn(null); // For any diseaseGroupId, act as if the group does not exist

        // Act
        List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(0, false, 0);

        // Assert
        assertThat(occurrences.isEmpty());
    }

    @Test
    public void deletePasswordResetRequestRemovesRequest() {
        // Arrange
        PasswordResetRequest request = mock(PasswordResetRequest.class);

        // Act
        expertService.deletePasswordResetRequest(request);

        // Assert
        verify(passwordResetRequestDao).delete(request);
    }

    @Test
    public void deletePasswordResetRequestTriggersRemovalOfOldRequests() {
        // Arrange
        PasswordResetRequest request = mock(PasswordResetRequest.class);

        // Act
        expertService.deletePasswordResetRequest(request);

        // Assert
        verify(passwordResetRequestDao).removeOldRequests();
    }

    @Test
    public void checkPasswordResetRequestReturnsFalseForInvalidID() {
        // Arrange
        when(passwordResetRequestDao.getById(7)).thenReturn(null);
        when(passwordEncoder.matches(eq("any"), anyString())).thenReturn(true);

        // Act
        boolean result = expertService.checkPasswordResetRequest(7, "any");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkPasswordResetRequestReturnsFalseForInvalidKey() {
        // Arrange
        when(passwordResetRequestDao.getById(7)).thenReturn(mock(PasswordResetRequest.class));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act
        boolean result = expertService.checkPasswordResetRequest(7, "any");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void checkPasswordResetRequestReturnsTrueForValidIDKeyPair() {
        // Arrange
        PasswordResetRequest request = mock(PasswordResetRequest.class);
        when(request.getHashedKey()).thenReturn("hashedKey");
        when(passwordResetRequestDao.getById(7)).thenReturn(request);
        when(passwordEncoder.matches("key", "hashedKey")).thenReturn(true);

        // Act
        boolean result = expertService.checkPasswordResetRequest(7, "key");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void checkPasswordResetRequestTriggersRemovalOfOldRequests() {
        // Act
        expertService.checkPasswordResetRequest(7, "key");

        // Assert
        verify(passwordResetRequestDao).removeOldRequests();
    }


    @Test
    public void getPasswordResetRequestReturnsNullForMissingID() {
        // Act
        PasswordResetRequest result = expertService.getPasswordResetRequest(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    public void getPasswordResetRequestReturnsCorrectResult() {
        // Arrange
        PasswordResetRequest request = mock(PasswordResetRequest.class);
        when(passwordResetRequestDao.getById(7)).thenReturn(request);

        // Act
        PasswordResetRequest result = expertService.getPasswordResetRequest(7);

        // Assert
        assertThat(result).isEqualTo(request);
    }

    @Test
    public void getPasswordResetRequestTriggersRemovalOfOldRequests() {
        // Act
        expertService.getPasswordResetRequest(7);

        // Assert
        verify(passwordResetRequestDao).removeOldRequests();
    }

    @Test
    public void createAndSavePasswordResetRequestSavesNewRequest() {
        // Arrange
        String email = "email";
        String key = "key";
        Expert expert = mock(Expert.class);
        when(passwordEncoder.encode(key)).thenReturn(key);
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        expertService.createAndSavePasswordResetRequest(email, key);

        // Assert
        ArgumentCaptor<PasswordResetRequest> captor = ArgumentCaptor.forClass(PasswordResetRequest.class);
        verify(passwordResetRequestDao).save(captor.capture());
        PasswordResetRequest value = captor.getValue();
        assertThat(value.getHashedKey()).isEqualTo(key);
        assertThat(value.getExpert()).isEqualTo(expert);
    }

    @Test
    public void createAndSavePasswordResetRequestHashesTheSpecifiedKey() {
        // Arrange
        String email = "email";
        String key = "key";
        Expert expert = mock(Expert.class);
        String hashedKey = "hashedKey";
        when(passwordEncoder.encode(key)).thenReturn(hashedKey);
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        expertService.createAndSavePasswordResetRequest(email, key);

        // Assert
        ArgumentCaptor<PasswordResetRequest> captor = ArgumentCaptor.forClass(PasswordResetRequest.class);
        verify(passwordEncoder).encode(key);
        verify(passwordResetRequestDao).save(captor.capture());
        PasswordResetRequest value = captor.getValue();
        assertThat(value.getHashedKey()).isEqualTo(hashedKey);
    }

    @Test
    public void createAndSavePasswordResetRequestTriggersRemovalOfOldRequests() {
        // Arrange
        String email = "email";
        String key = "key";
        Expert expert = mock(Expert.class);
        when(passwordEncoder.encode(key)).thenReturn(key);
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        expertService.createAndSavePasswordResetRequest(email, key);

        // Assert
        verify(passwordResetRequestDao).removeOldRequests();
    }

    @Test
    public void createAndSavePasswordResetRequestTriggersRemovalOfUsersRequests() {
        // Arrange
        String email = "email";
        String key = "key";
        Expert expert = mock(Expert.class);
        when(passwordEncoder.encode(key)).thenReturn(key);
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        expertService.createAndSavePasswordResetRequest(email, key);

        // Assert
        InOrder inOrder = inOrder(passwordResetRequestDao);
        inOrder.verify(passwordResetRequestDao).removeRequestsIssuedForExpert(expert);
        inOrder.verify(passwordResetRequestDao).save(any(PasswordResetRequest.class));
    }

    @Test
    public void getAllExperts() {
        // Arrange
        List<Expert> experts = Arrays.asList(new Expert());
        when(expertDao.getAll()).thenReturn(experts);

        // Act
        List<Expert> testExperts = expertService.getAllExperts();

        // Assert
        assertThat(testExperts).isSameAs(experts);
    }

    @Test
    public void getExpertById() {
        // Arrange
        int id = 1;
        Expert expert = new Expert(id);
        when(expertDao.getById(id)).thenReturn(expert);

        // Act
        Expert testExpert = expertService.getExpertById(id);

        // Assert
        assertThat(testExpert).isSameAs(expert);
    }

    @Test
    public void getExpertByEmail() {
        // Arrange
        String email = "test@test.com";
        Expert expert = new Expert();
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        Expert testExpert = expertService.getExpertByEmail(email);

        // Assert
        assertThat(testExpert).isSameAs(expert);
    }

    @Test
    public void getAdminUnitReviewCountReturnsExpectedLong() {
        // Arrange
        int expertId = 1;
        long expectedLong = 2;
        when(adminUnitReviewDao.getCountByExpertId(expertId)).thenReturn(expectedLong);

        // Act
        Long count = expertService.getAdminUnitReviewCount(expertId);

        // Assert
        verify(adminUnitReviewDao).getCountByExpertId(expertId);
        assertThat(count).isEqualTo(expectedLong);
    }

    @Test
    public void getLastReviewDateReturnsCorrectReviewDateWhenAdminUnitReviewIsNewest() {
        // Arrange
        DateTime expectation = DateTime.now();
        when(adminUnitReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation);
        when(diseaseOccurrenceReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation.minusDays(1));

        // Act
        DateTime actual = expertService.getLastReviewDate(1);

        // Assert
        assertThat(actual).isEqualTo(expectation);
    }

    @Test
    public void getLastReviewDateReturnsCorrectReviewDateWhenDiseaseOccurrenceReviewIsNewest() {
        // Arrange
        DateTime expectation = DateTime.now();
        when(adminUnitReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation.minusDays(1));
        when(diseaseOccurrenceReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation);

        // Act
        DateTime actual = expertService.getLastReviewDate(1);

        // Assert
        assertThat(actual).isEqualTo(expectation);
    }

    @Test
    public void getLastReviewDateReturnsCorrectReviewDateWhenNoAdminUnitReviews() {
        // Arrange
        DateTime expectation = DateTime.now();
        when(diseaseOccurrenceReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation);

        // Act
        DateTime actual = expertService.getLastReviewDate(1);

        // Assert
        assertThat(actual).isEqualTo(expectation);
    }

    @Test
    public void getLastReviewDateReturnsCorrectReviewDateWhenNoDiseaseOccurrenceReviews() {
        // Arrange
        DateTime expectation = DateTime.now();
        when(adminUnitReviewDao.getLastReviewDateByExpertId(1)).thenReturn(expectation);

        // Act
        DateTime actual = expertService.getLastReviewDate(1);

        // Assert
        assertThat(actual).isEqualTo(expectation);
    }

    @Test
    public void getLastReviewDateReturnsCorrectReviewDateWhenNoReviews() {
        // Arrange
        // Act
        DateTime actual = expertService.getLastReviewDate(1);

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    public void getAllAdminUnitReviewsForDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        List<AdminUnitReview> adminUnitReviews = new ArrayList<>();
        when(adminUnitReviewDao.getByDiseaseGroupId(diseaseGroupId)).thenReturn(adminUnitReviews);

        // Act
        List<AdminUnitReview> testAdminUnitReviews =
                expertService.getAllAdminUnitReviewsForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(testAdminUnitReviews).isSameAs(adminUnitReviews);
    }

    @Test
    public void getCurrentAdminUnitReviewsForDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        List<AdminUnitReview> adminUnitReviews = new ArrayList<>();
        DateTime now = DateTime.now();

        AdminUnitReview old1 = mockAdminUnitReview(1, 1, now.minusMillis(2));
        adminUnitReviews.add(old1); // should be removed
        AdminUnitReview keep1 = mockAdminUnitReview(1, 1, now);
        adminUnitReviews.add(keep1);
        AdminUnitReview old2 = mockAdminUnitReview(1, 1, now.minusMillis(4));
        adminUnitReviews.add(old2); // should be removed
        AdminUnitReview keep2 = mockAdminUnitReview(2, 1, now.plusHours(1));
        adminUnitReviews.add(keep2);
        AdminUnitReview keep3 = mockAdminUnitReview(1, 2, now);
        adminUnitReviews.add(keep3);
        AdminUnitReview keep4 = mockAdminUnitReview(2, 2, now);
        adminUnitReviews.add(keep4);

        when(adminUnitReviewDao.getByDiseaseGroupId(diseaseGroupId)).thenReturn(adminUnitReviews);

        // Act
        Collection<AdminUnitReview> testAdminUnitReviews =
                expertService.getCurrentAdminUnitReviewsForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(testAdminUnitReviews).containsOnly(keep1, keep2, keep3, keep4);
    }

    private AdminUnitReview mockAdminUnitReview(int gaulCode, int expertId, DateTime createdDate) {
        AdminUnitReview mock = mock(AdminUnitReview.class);
        when(mock.getAdminUnitGlobalOrTropicalGaulCode()).thenReturn(gaulCode);
        when(mock.getExpert()).thenReturn(mock(Expert.class));
        when(mock.getExpert().getId()).thenReturn(expertId);
        when(mock.getCreatedDate()).thenReturn(createdDate);
        return mock;
    }

    @Test
    public void getAllAdminUnitReviewsForExpertAndDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        int expertId = 1;
        List<AdminUnitReview> adminUnitReviews = new ArrayList<>();
        when(adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(expertId, diseaseGroupId)).thenReturn(adminUnitReviews);

        // Act
        List<AdminUnitReview> testAdminUnitReviews =
                expertService.getAllAdminUnitReviewsForDiseaseGroup(expertId, diseaseGroupId);

        // Assert
        assertThat(testAdminUnitReviews).isSameAs(adminUnitReviews);
    }

    @Test
    public void saveGlobalAdminUnitReview() {
        // Arrange
        int expertId = 1;
        int diseaseGroupId = 2;
        int gaulCode = 3;
        DiseaseExtentClass extentClass = new DiseaseExtentClass(DiseaseExtentClass.ABSENCE);

        Expert expert = mockExpert(expertId);
        DiseaseGroup diseaseGroup = mockDiseaseGroup(diseaseGroupId, true);

        // Act
        expertService.saveAdminUnitReview(expertId, diseaseGroupId, gaulCode, extentClass);

        // Assert
        AdminUnitReview review = new AdminUnitReview(expert, gaulCode, null, diseaseGroup, extentClass);
        verify(adminUnitReviewDao).save(eq(review));
    }


    @Test
    public void saveTropicalAdminUnitReview() {
        // Arrange
        int expertId = 1;
        int diseaseGroupId = 2;
        int gaulCode = 3;
        DiseaseExtentClass extentClass = new DiseaseExtentClass(DiseaseExtentClass.ABSENCE);

        Expert expert = mockExpert(expertId);
        DiseaseGroup diseaseGroup = mockDiseaseGroup(diseaseGroupId, false);

        // Act
        expertService.saveAdminUnitReview(expertId, diseaseGroupId, gaulCode, extentClass);

        // Assert
        AdminUnitReview review = new AdminUnitReview(expert, null, gaulCode, diseaseGroup, extentClass);
        verify(adminUnitReviewDao).save(eq(review));
    }

    private Expert mockExpert(int expertId) {
        Expert expert = mock(Expert.class);
        when(expertDao.getById(expertId)).thenReturn(expert);
        return expert;
    }

    private DiseaseGroup mockDiseaseGroup(int diseaseGroupId, boolean isGlobal) {
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.isGlobal()).thenReturn(isGlobal);
        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        return diseaseGroup;
    }

    @Test
    public void getCountOfPubliclyVisibleExpertsCallsDaoCorrectly() {
        // Arrange
        long expectedResult = 987654321L;
        when(expertDao.getCountOfPubliclyVisible()).thenReturn(expectedResult);

        // Act
        long result = expertService.getCountOfPubliclyVisibleExperts();

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void doesAdminUnitReviewExistForLatestDiseaseExtentReturnsFalseForDiseaseWhichHasNeverHadAnExtentGenerated() {
        // Arrange
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(null);

        // Act
        boolean result = expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(1, diseaseGroup, mock(AdminUnitGlobalOrTropical.class));

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void doesAdminUnitReviewExistForLatestDiseaseExtentReturnsFalseNoReviewsExist() {
        // Arrange
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(DateTime.now());
        when(diseaseGroup.getId()).thenReturn(2);
        AdminUnitGlobalOrTropical adminUnit = mock(AdminUnitGlobalOrTropical.class);
        when(adminUnit.getGaulCode()).thenReturn(3);

        when(adminUnitReviewDao.getLastReviewDateByExpertIdAndDiseaseGroupIdAndGaulCode(1, 2, 3)).thenReturn(null);
        // Act
        boolean result = expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(1, diseaseGroup, adminUnit);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void doesAdminUnitReviewExistForLatestDiseaseExtentReturnsFalseIfMostRecentReviewIsBeforeLastExtentGeneration() {
        // Arrange
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        DateTime lastExtentGeneration = DateTime.now();
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(lastExtentGeneration);
        when(diseaseGroup.getId()).thenReturn(2);
        AdminUnitGlobalOrTropical adminUnit = mock(AdminUnitGlobalOrTropical.class);
        when(adminUnit.getGaulCode()).thenReturn(3);

        when(adminUnitReviewDao.getLastReviewDateByExpertIdAndDiseaseGroupIdAndGaulCode(1, 2, 3)).thenReturn(lastExtentGeneration.minusHours(1));

        // Act
        boolean result = expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(1, diseaseGroup, adminUnit);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void doesAdminUnitReviewExistForLatestDiseaseExtentReturnsTrueIfMostRecentReviewIsAfterLastExtentGeneration() {
        // Arrange
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        DateTime lastExtentGeneration = DateTime.now();
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(lastExtentGeneration);
        when(diseaseGroup.getId()).thenReturn(2);
        AdminUnitGlobalOrTropical adminUnit = mock(AdminUnitGlobalOrTropical.class);
        when(adminUnit.getGaulCode()).thenReturn(3);

        when(adminUnitReviewDao.getLastReviewDateByExpertIdAndDiseaseGroupIdAndGaulCode(1, 2, 3)).thenReturn(lastExtentGeneration.plusHours(1));

        // Act
        boolean result = expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(1, diseaseGroup, adminUnit);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void getPageOfPubliclyVisibleExpertsCallsDaoCorrectly() {
        // Arrange
        List<Expert> expectedResult = new ArrayList<>();
        int expectedPageSize = 67890;
        int expectedPageNumber = 12345;
        when(expertDao.getPageOfPubliclyVisible(expectedPageNumber, expectedPageSize)).thenReturn(expectedResult);

        // Act
        List<Expert> result = expertService.getPageOfPubliclyVisibleExperts(expectedPageNumber, expectedPageSize);

        // Assert
        assertThat(result).isSameAs(expectedResult);
    }

    @Test
    public void saveDiseaseOccurrenceReview() {
        //Arrange
        Expert expert = new Expert();
        when(expertDao.getById(1)).thenReturn(expert);
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        when(diseaseOccurrenceDao.getById(2)).thenReturn(occurrence);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        ArgumentCaptor<DiseaseOccurrenceReview> reviewArgumentCaptor =
                ArgumentCaptor.forClass(DiseaseOccurrenceReview.class);
        doNothing().when(diseaseOccurrenceReviewDao).save(reviewArgumentCaptor.capture());

        // Act
        expertService.saveDiseaseOccurrenceReview(1, 2, response);

        //Assert
        DiseaseOccurrenceReview value = reviewArgumentCaptor.getValue();
        assertThat(value.getExpert()).isEqualTo(expert);
        assertThat(value.getDiseaseOccurrence()).isEqualTo(occurrence);
        assertThat(value.getResponse()).isEqualTo(response);
    }

    @Test
    public void saveDiseaseOccurrenceReviewWithNullResponse() {
        //Arrange
        Expert expert = new Expert();
        when(expertDao.getById(1)).thenReturn(expert);
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        when(diseaseOccurrenceDao.getById(2)).thenReturn(occurrence);
        ArgumentCaptor<DiseaseOccurrenceReview> reviewArgumentCaptor =
                ArgumentCaptor.forClass(DiseaseOccurrenceReview.class);
        doNothing().when(diseaseOccurrenceReviewDao).save(reviewArgumentCaptor.capture());

        // Act
        expertService.saveDiseaseOccurrenceReview(1, 2, null);

        //Assert
        DiseaseOccurrenceReview value = reviewArgumentCaptor.getValue();
        assertThat(value.getExpert()).isEqualTo(expert);
        assertThat(value.getDiseaseOccurrence()).isEqualTo(occurrence);
        assertThat(value.getResponse()).isNull();
    }

    @Test
    public void saveExpert() {
        Expert expert = new Expert();
        expertService.saveExpert(expert);
        verify(expertDao).save(eq(expert));
    }
}
