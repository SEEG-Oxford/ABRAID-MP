package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import ch.lambdaj.group.Group;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ReportingService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Controller for the admin reporting pages.
 * Copyright (c) 2015 University of Oxford
 */
@Controller
public class ReportingController extends AbstractController {
    private static final String ADMIN_REPORTING_BASE_URL = "/admin/report";

    private ReportingService reportingService;
    private final GeometryService geometryService;
    private final DiseaseService diseaseService;

    @Autowired
    public ReportingController(ReportingService reportingService,
                               GeometryService geometryService,
                               DiseaseService diseaseService) {
        this.reportingService = reportingService;
        this.geometryService = geometryService;
        this.diseaseService = diseaseService;
    }

    /**
     * Get the HealthMap disease report.
     * @param model The page template model.
     * @return The page template.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_REPORTING_BASE_URL + "/healthMapByDisease", method = RequestMethod.GET)
    public String getHealthMapDiseaseReport(Model model) {
        return getHealthMapReport(
                model,
                reportingService.getHealthMapDiseaseReportEntries(),
                diseaseService.getDiseaseGroupNamesForHealthMapReport(),
                false);
    }

    /**
     * Get the HealthMap country report.
     * @param model The page template model.
     * @return The page template.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_REPORTING_BASE_URL + "/healthMapByCountry", method = RequestMethod.GET)
    public String getHealthMapCountryReport(Model model) {
        return getHealthMapReport(
                model,
                reportingService.getHealthMapCountryReportEntries(),
                geometryService.getCountryNamesForHealthMapReport(),
                true);
    }

    /**
     * Setup the template data for a HealthMap report.
     */
    private String getHealthMapReport(
            Model model, List<HealthMapReportEntry> data, List<String> qualifiers, boolean includeOtherRow) {
        List<String> months = getMonths();
        Map<String, Map<String, HealthMapReportEntry>> processed = process(data, qualifiers, months, includeOtherRow);

        List<String> columns = new ArrayList<>(months);
        columns.add("Total");
        List<String> rows = new ArrayList<>(qualifiers);
        if (includeOtherRow) {
            rows.add("Other");
        }
        rows.add("Total");

        model.addAttribute("data", processed);
        model.addAttribute("months", columns);
        model.addAttribute("qualifiers", rows);

        return "admin/healthMapReport";
    }

    /**
     * Index the data by month and qualifier, fill in the gaps with 0, add "Other" and "Total" rows/columns.
     */
    private Map<String, Map<String, HealthMapReportEntry>> process(
            List<HealthMapReportEntry> data, List<String> qualifiers, List<String> months, boolean includeOtherRow) {
        Map<String, Map<String, HealthMapReportEntry>> unprocessed = indexData(data, months);
        Map<String, Map<String, HealthMapReportEntry>> processed = new HashMap<>();

        for (String month : months) {
            Map<String, HealthMapReportEntry> processedForMonth = new HashMap<>();
            processed.put(month, processedForMonth);

            // Add the known qualifiers
            for (String qualifier : qualifiers) {
                Map<String, HealthMapReportEntry> unprocessedForMonth = unprocessed.get(month);
                if (unprocessedForMonth.containsKey(qualifier)) {
                    processedForMonth.put(qualifier, unprocessedForMonth.get(qualifier));
                } else {
                    processedForMonth.put(qualifier,
                            new HealthMapReportEntry(month, qualifier, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L));
                }
            }

            // Add other row
            if (includeOtherRow) {
                HealthMapReportEntry otherEntry = sumDataForUnknownQualifiers(month, qualifiers, unprocessed);
                processedForMonth.put("Other", otherEntry);
            }

            // Add totals row
            HealthMapReportEntry totalEntry = sumDataForAllQualifiers(month, unprocessed);
            processedForMonth.put("Total", totalEntry);
        }

        // Add totals column
        Map<String, HealthMapReportEntry> processedTotals = new HashMap<>();
        for (String qualifier : qualifiers) {
            processedTotals.put(qualifier, sumDataForAllMonths(months, qualifier, processed));
        }
        if (includeOtherRow) {
            processedTotals.put("Other", sumDataForAllMonths(months, "Other", processed));
        }
        processedTotals.put("Total", sumDataForAllMonths(months, "Total", processed));
        processed.put("Total", processedTotals);

        return processed;
    }

    private Map<String, Map<String, HealthMapReportEntry>> indexData(
            List<HealthMapReportEntry> data, List<String> months) {
        Group<HealthMapReportEntry> dataByMonth = group(data, by(on(HealthMapReportEntry.class).getMonth()));
        Map<String, Map<String, HealthMapReportEntry>> indexed = new HashMap<>();

        for (String month : months) {
            Map<String, HealthMapReportEntry> forMonth =
                    index(dataByMonth.find(month), on(HealthMapReportEntry.class).getQualifier());
            indexed.put(month, forMonth);
        }

        return indexed;
    }

    private HealthMapReportEntry sumDataForAllMonths(
            List<String> months, String qualifier, Map<String, Map<String, HealthMapReportEntry>> data) {
        return sumData("Total", qualifier, months, Arrays.asList(qualifier), data);
    }

    private HealthMapReportEntry sumDataForAllQualifiers(
            String month, Map<String, Map<String, HealthMapReportEntry>> data) {
        List<String> allQualifiers = new ArrayList<>(data.get(month).keySet());
        return sumData(month, "Total", Arrays.asList(month), allQualifiers, data);
    }

    private HealthMapReportEntry sumDataForUnknownQualifiers(
            String month, List<String> knownQualifiers, Map<String, Map<String, HealthMapReportEntry>> data) {
        List<String> unknownQualifiers = new ArrayList<>(data.get(month).keySet());
        unknownQualifiers.removeAll(knownQualifiers);
        return sumData(month, "Other", Arrays.asList(month), unknownQualifiers, data);
    }

    private HealthMapReportEntry sumData(String outputMonth, String outputQualifier,
                                         List<String> months, List<String> qualifiers,
                                         Map<String, Map<String, HealthMapReportEntry>> data) {
        Long dataCountryCount = 0L;
        Long dataAdmin1Count = 0L;
        Long dataAdmin2Count = 0L;
        Long dataPreciseCount = 0L;
        Long locationCountryCount = 0L;
        Long locationAdmin1Count = 0L;
        Long locationAdmin2Count = 0L;
        Long locationPreciseCount = 0L;

        for (String month : months) {
            Map<String, HealthMapReportEntry> forMonth = data.get(month);
            for (String qualifier : qualifiers) {
                HealthMapReportEntry entry = forMonth.get(qualifier);
                dataCountryCount = dataCountryCount + entry.getDataCountryCount();
                dataAdmin1Count = dataAdmin1Count + entry.getDataAdmin1Count();
                dataAdmin2Count = dataAdmin2Count + entry.getDataAdmin2Count();
                dataPreciseCount = dataPreciseCount + entry.getDataPreciseCount();
                locationCountryCount = locationCountryCount + entry.getLocationCountryCount();
                locationAdmin1Count = locationAdmin1Count + entry.getLocationAdmin1Count();
                locationAdmin2Count = locationAdmin2Count + entry.getLocationAdmin2Count();
                locationPreciseCount = locationPreciseCount + entry.getLocationPreciseCount();
            }
        }

        return new HealthMapReportEntry(outputMonth, outputQualifier,
                dataCountryCount, dataAdmin1Count, dataAdmin2Count, dataPreciseCount,
                locationCountryCount, locationAdmin1Count, locationAdmin2Count, locationPreciseCount);
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        LocalDate date = new LocalDate("2015-02-01");
        while (date.isBefore(LocalDate.now())) {
            months.add(date.toString("YYYY-MM"));
            date = date.plusMonths(1);
        }
        return months;
    }
}
