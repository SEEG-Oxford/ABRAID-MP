package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ContributorsController.
 * Copyright (c) 2014 University of Oxford
 */
public class ContributorsControllerTest {
    public static final int NUMBER_OF_EXPERTS_PER_PAGE = 16;
    public static final int NUMBER_OF_EXPERTS = (NUMBER_OF_EXPERTS_PER_PAGE * 3) + 1;
    public static final int NUMBER_OF_PAGES = (int) Math.ceil((double) NUMBER_OF_EXPERTS / (double) NUMBER_OF_EXPERTS_PER_PAGE);
    private ExpertService expertService;
    private List<Expert> experts;

    @Before
    public void setup() {
        expertService = mock(ExpertService.class);
        experts = new ArrayList<>();
        for (int i = 1; i <= NUMBER_OF_EXPERTS_PER_PAGE; i++) {
            experts.add(mock(Expert.class));
        }
        when(expertService.getPageOfPubliclyVisibleExperts(1, NUMBER_OF_EXPERTS_PER_PAGE)).thenReturn(experts);
        when(expertService.getCountOfPubliclyVisibleExperts()).thenReturn((long) NUMBER_OF_EXPERTS);
    }

    @Test
    public void showExpertsAddsCorrectPageCountToModel() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        target.showExperts(model, 1);

        // Assert
        verify(model).addAttribute(eq("pageCount"), eq(NUMBER_OF_PAGES));
    }

    @Test
    public void showExpertsAddsCorrectPageNumberToModel() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);
        int pageNumber = 2;

        // Act
        target.showExperts(model, pageNumber);

        // Assert
        verify(model).addAttribute(eq("pageNumber"), eq(pageNumber));
    }

    @Test
    public void showExpertsAddsCorrectExpertsToModel() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        target.showExperts(model, 1);

        // Assert
        verify(expertService).getPageOfPubliclyVisibleExperts(1, NUMBER_OF_EXPERTS_PER_PAGE);
        verify(model).addAttribute(eq("page"), eq(experts));
    }

    @Test
    public void showExpertsReturnsCorrectTemplate() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);

        // Act
        String result = target.showExperts(mock(ModelMap.class), 1);

        // Assert
        assertThat(result).isEqualTo("experts");
    }

    @Test
    public void showExpertsBehavesCorrectlyForZeroVisibleExperts() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        String result = target.showExperts(model, 1);

        // Assert
        verify(model).addAttribute(eq("pageCount"), eq(NUMBER_OF_PAGES));
        assertThat(result).isEqualTo("experts");
    }

    @Test
    public void showExpertsReturnsRedirectForTooHighPageNumber() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        String result = target.showExperts(model, NUMBER_OF_PAGES + 1);

        // Assert
        assertThat(result).isEqualTo("redirect:/experts?page=1");
    }

    @Test
    public void showExpertsReturnsRedirectForTooLowPageNumber() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        String result = target.showExperts(model, 0);

        // Assert
        assertThat(result).isEqualTo("redirect:/experts?page=1");
    }

    @Test
    public void showExpertsReturnsRedirectForMissingPageNumber() throws Exception {
        // Arrange
        ContributorsController target = new ContributorsController(expertService);
        ModelMap model = mock(ModelMap.class);

        // Act
        String result = target.showExperts(model, null);

        // Assert
        assertThat(result).isEqualTo("redirect:/experts?page=1");
    }
}
