package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ReportingService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelRepositoryController.
 * Copyright (c) 2015 ReportingController of Oxford
 */
public class ReportingControllerTest {
    @Test
    public void getHealthMapDiseaseReport() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(1427992077000L);
        GeometryService geometryService = mock(GeometryService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupNamesForHealthMapReport()).thenReturn(Arrays.asList("Dengue", "Cholera", "CCHF"));
        ReportingService reportingService = mock(ReportingService.class);
        when(reportingService.getHealthMapDiseaseReportEntries()).thenReturn(Arrays.asList(
                new HealthMapReportEntry("2015-03", "Dengue",  1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                new HealthMapReportEntry("2015-03", "Cholera", 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L),
                new HealthMapReportEntry("2015-04", "Cholera", 1L, 0L, 1L, 0L, 1L, 1L, 0L, 1L),
                new HealthMapReportEntry("2015-03", "Polio",   0L, 3L, 3L, 0L, 0L, 3L, 3L, 0L),
                new HealthMapReportEntry("2015-04", "Polio",   1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L)
        ));
        ReportingController target = new ReportingController(reportingService, geometryService, diseaseService);
        Model model = mock(Model.class);

        ArgumentCaptor<List<String>> monthCaptor = ArgumentCaptor.forClass((Class<List<String>>) (Class) List.class);
        ArgumentCaptor<List<String>> qualifierCaptor = ArgumentCaptor.forClass((Class<List<String>>) (Class) List.class);
        ArgumentCaptor<Map<String, Map<String, HealthMapReportEntry>>> dataCaptor = ArgumentCaptor.forClass((Class<Map<String, Map<String, HealthMapReportEntry>>>) (Class) Map.class);

        // Act
        String template = target.getHealthMapDiseaseReport(model);

        // Assert
        assertThat(template).isEqualTo("admin/healthMapReport");

        verify(model).addAttribute(eq("data"), dataCaptor.capture());
        verify(model).addAttribute(eq("months"), monthCaptor.capture());
        verify(model).addAttribute(eq("qualifiers"), qualifierCaptor.capture());
        Map<String, Map<String, HealthMapReportEntry>> data = dataCaptor.getValue();
        List<String> months = monthCaptor.getValue();
        List<String> qualifiers = qualifierCaptor.getValue();

        assertThat(months).isEqualTo(Arrays.asList("2015-02", "2015-03", "2015-04", "Total"));
        assertThat(qualifiers).isEqualTo(Arrays.asList("Dengue", "Cholera", "CCHF", "Total"));
        assertThat(data.get("2015-02").get("Dengue")) .isEqualTo(new HealthMapReportEntry("2015-02", "Dengue",   0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("Cholera")).isEqualTo(new HealthMapReportEntry("2015-02", "Cholera",  0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("CCHF"))   .isEqualTo(new HealthMapReportEntry("2015-02", "CCHF",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("Total"))  .isEqualTo(new HealthMapReportEntry("2015-02", "Total",    0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-03").get("Dengue")) .isEqualTo(new HealthMapReportEntry("2015-03", "Dengue",   1L,  2L,  3L,  4L,  5L,  6L,  7L,  8L));
        assertThat(data.get("2015-03").get("Cholera")).isEqualTo(new HealthMapReportEntry("2015-03", "Cholera",  8L,  7L,  6L,  5L,  4L,  3L,  2L,  1L));
        assertThat(data.get("2015-03").get("CCHF"))   .isEqualTo(new HealthMapReportEntry("2015-03", "CCHF",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-03").get("Total"))  .isEqualTo(new HealthMapReportEntry("2015-03", "Total",    9L, 12L, 12L,  9L,  9L, 12L, 12L,  9L));
        assertThat(data.get("2015-04").get("Dengue")) .isEqualTo(new HealthMapReportEntry("2015-04", "Dengue",   0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-04").get("Cholera")).isEqualTo(new HealthMapReportEntry("2015-04", "Cholera",  1L,  0L,  1L,  0L,  1L,  1L,  0L,  1L));
        assertThat(data.get("2015-04").get("CCHF"))   .isEqualTo(new HealthMapReportEntry("2015-04", "CCHF",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-04").get("Total"))  .isEqualTo(new HealthMapReportEntry("2015-04", "Total",    2L,  1L,  2L,  1L,  2L,  2L,  1L,  2L));
        assertThat(data.get("Total")  .get("Dengue")) .isEqualTo(new HealthMapReportEntry("Total",   "Dengue",   1L,  2L,  3L,  4L,  5L,  6L,  7L,  8L));
        assertThat(data.get("Total")  .get("Cholera")).isEqualTo(new HealthMapReportEntry("Total",   "Cholera",  9L,  7L,  7L,  5L,  5L,  4L,  2L,  2L));
        assertThat(data.get("Total")  .get("CCHF"))   .isEqualTo(new HealthMapReportEntry("Total",   "CCHF",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("Total")  .get("Total"))  .isEqualTo(new HealthMapReportEntry("Total",   "Total",   11L, 13L, 14L, 10L, 11L, 14L, 13L, 11L));
    }

    @Test
    public void getHealthMapCountryReport() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(1427992077000L);
        GeometryService geometryService = mock(GeometryService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(geometryService.getCountryNamesForHealthMapReport()).thenReturn(Arrays.asList("UK", "USA", "JP"));
        ReportingService reportingService = mock(ReportingService.class);
        when(reportingService.getHealthMapCountryReportEntries()).thenReturn(Arrays.asList(
                new HealthMapReportEntry("2015-03", "UK",   1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                new HealthMapReportEntry("2015-03", "USA",  8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L),
                new HealthMapReportEntry("2015-04", "USA",  1L, 0L, 1L, 0L, 1L, 1L, 0L, 1L),
                new HealthMapReportEntry("2015-03", "FR",   0L, 3L, 3L, 0L, 0L, 3L, 3L, 0L),
                new HealthMapReportEntry("2015-04", "FR",   1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L)
        ));
        ReportingController target = new ReportingController(reportingService, geometryService, diseaseService);
        Model model = mock(Model.class);

        ArgumentCaptor<List<String>> monthCaptor = ArgumentCaptor.forClass((Class<List<String>>) (Class) List.class);
        ArgumentCaptor<List<String>> qualifierCaptor = ArgumentCaptor.forClass((Class<List<String>>) (Class) List.class);
        ArgumentCaptor<Map<String, Map<String, HealthMapReportEntry>>> dataCaptor = ArgumentCaptor.forClass((Class<Map<String, Map<String, HealthMapReportEntry>>>) (Class) Map.class);

        // Act
        String template = target.getHealthMapCountryReport(model);

        // Assert
        assertThat(template).isEqualTo("admin/healthMapReport");

        verify(model).addAttribute(eq("data"), dataCaptor.capture());
        verify(model).addAttribute(eq("months"), monthCaptor.capture());
        verify(model).addAttribute(eq("qualifiers"), qualifierCaptor.capture());
        Map<String, Map<String, HealthMapReportEntry>> data = dataCaptor.getValue();
        List<String> months = monthCaptor.getValue();
        List<String> qualifiers = qualifierCaptor.getValue();

        assertThat(months).isEqualTo(Arrays.asList("2015-02", "2015-03", "2015-04", "Total"));
        assertThat(qualifiers).isEqualTo(Arrays.asList("UK", "USA", "JP", "Other", "Total"));
        assertThat(data.get("2015-02").get("UK"))   .isEqualTo(new HealthMapReportEntry("2015-02", "UK",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("USA"))  .isEqualTo(new HealthMapReportEntry("2015-02", "USA",    0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("JP"))   .isEqualTo(new HealthMapReportEntry("2015-02", "JP",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("Other")).isEqualTo(new HealthMapReportEntry("2015-02", "Other",  0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-02").get("Total")).isEqualTo(new HealthMapReportEntry("2015-02", "Total",  0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-03").get("UK"))   .isEqualTo(new HealthMapReportEntry("2015-03", "UK",     1L,  2L,  3L,  4L,  5L,  6L,  7L,  8L));
        assertThat(data.get("2015-03").get("USA"))  .isEqualTo(new HealthMapReportEntry("2015-03", "USA",    8L,  7L,  6L,  5L,  4L,  3L,  2L,  1L));
        assertThat(data.get("2015-03").get("JP"))   .isEqualTo(new HealthMapReportEntry("2015-03", "JP",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-03").get("Other")).isEqualTo(new HealthMapReportEntry("2015-03", "Other",  0L,  3L,  3L,  0L,  0L,  3L,  3L,  0L));
        assertThat(data.get("2015-03").get("Total")).isEqualTo(new HealthMapReportEntry("2015-03", "Total",  9L, 12L, 12L,  9L,  9L, 12L, 12L,  9L));
        assertThat(data.get("2015-04").get("UK"))   .isEqualTo(new HealthMapReportEntry("2015-04", "UK",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-04").get("USA"))  .isEqualTo(new HealthMapReportEntry("2015-04", "USA",    1L,  0L,  1L,  0L,  1L,  1L,  0L,  1L));
        assertThat(data.get("2015-04").get("JP"))   .isEqualTo(new HealthMapReportEntry("2015-04", "JP",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("2015-04").get("Other")).isEqualTo(new HealthMapReportEntry("2015-04", "Other",  1L,  1L,  1L,  1L,  1L,  1L,  1L,  1L));
        assertThat(data.get("2015-04").get("Total")).isEqualTo(new HealthMapReportEntry("2015-04", "Total",  2L,  1L,  2L,  1L,  2L,  2L,  1L,  2L));
        assertThat(data.get("Total")  .get("UK"))   .isEqualTo(new HealthMapReportEntry("Total",   "UK",     1L,  2L,  3L,  4L,  5L,  6L,  7L,  8L));
        assertThat(data.get("Total")  .get("USA"))  .isEqualTo(new HealthMapReportEntry("Total",   "USA",    9L,  7L,  7L,  5L,  5L,  4L,  2L,  2L));
        assertThat(data.get("Total")  .get("JP"))   .isEqualTo(new HealthMapReportEntry("Total",   "JP",     0L,  0L,  0L,  0L,  0L,  0L,  0L,  0L));
        assertThat(data.get("Total")  .get("Other")).isEqualTo(new HealthMapReportEntry("Total",   "Other",  1L,  4L,  4L,  1L,  1L,  4L,  4L,  1L));
        assertThat(data.get("Total")  .get("Total")).isEqualTo(new HealthMapReportEntry("Total",   "Total", 11L, 13L, 14L, 10L, 11L, 14L, 13L, 11L));
    }
}
