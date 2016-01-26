package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import ch.lambdaj.collection.LambdaMap;
import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.BinningRasterSummaryCollator;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RangeRasterSummaryCollator;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.ValuesRasterSummaryCollator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.collection.LambdaCollections.with;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class CovariatesControllerHelperImpl implements CovariatesControllerHelper {
    private final CovariateService covariateService;
    private final DiseaseService diseaseService;
    private static final String ERROR_CREATE_SUBDIRECTORY = "Could not create subdirectory for new covariate file";
    private static final int NUMBER_OF_HISTOGRAM_BINS = 10;

    @Autowired
    public CovariatesControllerHelperImpl(CovariateService covariateService, DiseaseService diseaseService) {
        this.covariateService = covariateService;
        this.diseaseService = diseaseService;
    }

    /**
     * Builds a covariate file storage location.
     * @param subdirectory The subdirectory of the covariate directory in which to store the file.
     * @param file The covariate file.
     * @return The covariate file storage location
     */
    @Override
    public String extractTargetPath(String subdirectory, MultipartFile file) {
        String covariateDirectory = covariateService.getCovariateDirectory();
        Path path = Paths.get(covariateDirectory, subdirectory, file.getOriginalFilename()).normalize();
        return FilenameUtils.separatorsToUnix(path.toAbsolutePath().toString());
    }

    /**
     * Gets the JSON version of the covariate configuration.
     * @return The covariate configuration
     * @throws java.io.IOException Thrown if the covariate directory can not be checked for new files.
     */
    @Override
    public JsonCovariateConfiguration getCovariateConfiguration() throws IOException {
        return new JsonCovariateConfiguration(
                convert(diseaseService.getAllDiseaseGroups(), new Converter<DiseaseGroup, JsonModelDisease>() {
                    @Override
                    public JsonModelDisease convert(DiseaseGroup diseaseGroup) {
                        return new JsonModelDisease(diseaseGroup.getId(), diseaseGroup.getName());
                    }
                }),
                convert(covariateService.getAllCovariateFiles(), new Converter<CovariateFile, JsonCovariateFile>() {
                    @Override
                    public JsonCovariateFile convert(CovariateFile covariateFile) {
                        List<JsonCovariateSubFile> subFiles = extractSubFiles(covariateFile);
                        return new JsonCovariateFile(
                                covariateFile.getId(),
                                subFiles,
                                covariateFile.getName(),
                                covariateFile.getInfo(),
                                covariateFile.getHide(),
                                covariateFile.getDiscrete(),
                                extract(covariateFile.getEnabledDiseaseGroups(), on(DiseaseGroup.class).getId())
                        );
                    }


                })
        );
    }

    private List<JsonCovariateSubFile> extractSubFiles(CovariateFile covariateFile) {
        return convert(covariateFile.getFiles(), new Converter<CovariateSubFile, JsonCovariateSubFile>() {
            @Override
            public JsonCovariateSubFile convert(CovariateSubFile covariateSubFile) {
                return new JsonCovariateSubFile(
                        covariateSubFile.getId(),
                        covariateSubFile.getFile(),
                        covariateSubFile.getQualifier()
                );
            }
        });
    }

    /**
     * Persist the JSON version of the covariate configuration into the database.
     * Note: Id's have been checked by this point in CovariatesControllerValidator.
     * @param config The covariate configuration
     */
    @Override
    public  void setCovariateConfiguration(JsonCovariateConfiguration config) {
        final LambdaMap<Integer, CovariateFile> allCovariateFiles = with(covariateService.getAllCovariateFiles())
                .index(on(CovariateFile.class).getId());
        final LambdaMap<Integer, DiseaseGroup> allDiseaseGroups = with(diseaseService.getAllDiseaseGroups())
                .index(on(DiseaseGroup.class).getId());

        for (JsonCovariateFile jsonFile : config.getFiles()) {
            boolean changed = false;
            CovariateFile dbFile = allCovariateFiles.get(jsonFile.getId());
            LambdaMap<Integer, CovariateSubFile> dbSubFiles = with(dbFile.getFiles())
                    .index(on(CovariateSubFile.class).getId());

            for (JsonCovariateSubFile jsonSubFile : jsonFile.getSubFiles()) {
                CovariateSubFile dbSubFile = dbSubFiles.get(jsonSubFile.getId());

                if (!StringUtils.equals(dbSubFile.getQualifier(), jsonSubFile.getQualifier())) {
                    dbSubFile.setQualifier(jsonSubFile.getQualifier());
                    changed = true;
                }
            }

            if (!StringUtils.equals(dbFile.getName(), jsonFile.getName())) {
                dbFile.setName(jsonFile.getName());
                changed = true;
            }

            if (!StringUtils.equals(dbFile.getInfo(), jsonFile.getInfo())) {
                dbFile.setInfo(jsonFile.getInfo());
                changed = true;
            }

            if (dbFile.getHide() == null || !dbFile.getHide().equals(jsonFile.getHide())) {
                dbFile.setHide(jsonFile.getHide());
                changed = true;
            }

            Collection<DiseaseGroup> enabledDiseases =
                    convert(jsonFile.getEnabled(), new Converter<Integer, DiseaseGroup>() {
                        @Override
                        public DiseaseGroup convert(Integer diseaseGroupId) {
                            return allDiseaseGroups.get(diseaseGroupId);
                        }
                    });

            if (dbFile.getEnabledDiseaseGroups() == null || !dbFile.getEnabledDiseaseGroups().equals(enabledDiseases)) {
                dbFile.setEnabledDiseaseGroups(enabledDiseases);
                changed = true;
            }


            if (changed) {
                covariateService.saveCovariateFile(dbFile);
            }
        }
    }

    /**
     * Persist a single new covariate file to the filesystem and database.
     * @param name The display name for the covariate (null if a sub file).
     * @param qualifier The qualifier name for the covariate sub file (ie the year/month).
     * @param parentId The ID of the parent covariate for this file (or null if this is the first file).
     * @param isDiscrete True if this covariate contains discrete values
     * @param path The location to store the covariate.
     * @param file The covariate.
     * @throws IOException Thrown if the covariate director can not be writen to.
     */
    @Override
    public void saveNewCovariateFile(String name, String qualifier, Integer parentId, boolean isDiscrete,
                                     String path, MultipartFile file)
            throws IOException {
        File rasterFile = writeCovariateFileToDisk(file, path);

        CovariateFile covariateFile = null;

        if (parentId == null) {
            // Create top level covariate - Histogram is always taken from first uploaded
            Map<DoubleRange, Integer> binnedCovariateValueData =
                    generateCovariateValuesHistogram(rasterFile, isDiscrete);
            covariateFile = createDatabaseCovariate(name, isDiscrete, binnedCovariateValueData);
        } else {
            // Find top level covariate
            covariateFile = covariateService.getCovariateFileById(parentId);
        }

        CovariateSubFile subFile = new CovariateSubFile(covariateFile, qualifier, extractRelativePath(path));
        addSubFileToCovariate(covariateFile, subFile);

        covariateService.saveCovariateFile(covariateFile);
    }

    private void addSubFileToCovariate(CovariateFile covariateFile, CovariateSubFile subFile) {
        List<CovariateSubFile> files = covariateFile.getFiles();
        if (files == null) {
            // Safe add
            files = new ArrayList<>();
            covariateFile.setFiles(files);
        }
        files.add(subFile);
    }

    private CovariateFile createDatabaseCovariate(String name, boolean isDiscrete,
                                        Map<DoubleRange, Integer> binnedCovariateValueData) throws IOException {

        CovariateFile covariateFile = new CovariateFile(
                name,
                false,
                isDiscrete,
                ""
        );

        List<CovariateValueBin> bins = new ArrayList<>();
        for (Map.Entry<DoubleRange, Integer> bin : binnedCovariateValueData.entrySet()) {
            bins.add(new CovariateValueBin(covariateFile,
                    bin.getKey().getMinimumDouble(), bin.getKey().getMaximumDouble(), bin.getValue()));
        }
        covariateFile.setCovariateValueHistogramData(bins);
        covariateFile.setFiles(new ArrayList<CovariateSubFile>());

        return covariateFile;
    }

    private File writeCovariateFileToDisk(MultipartFile file, String path) throws IOException {
        // Create directory
        createDirectoryForCovariate(path);

        File serverFile = Paths.get(path).toFile();
        file.transferTo(serverFile);
        return serverFile;
    }

    private Map<DoubleRange, Integer> generateCovariateValuesHistogram(File rasterFile, boolean isDiscrete)
            throws IOException {
        // Find bins
        List<DoubleRange> histogramBins = new ArrayList<>();
        if (isDiscrete) {
            Collection<Double> values = RasterUtils.summarizeRaster(rasterFile, new ValuesRasterSummaryCollator());

            for (Double value : values)  {
                histogramBins.add(new DoubleRange(value, value));
            }
        } else {
            DoubleRange range = RasterUtils.summarizeRaster(rasterFile, new RangeRasterSummaryCollator());

            double min = range.getMinimumDouble();
            double max = range.getMaximumDouble();
            double size = (max - min) / ((double) NUMBER_OF_HISTOGRAM_BINS);
            for (int i = 1; i <= NUMBER_OF_HISTOGRAM_BINS; i++) {
                histogramBins.add(new DoubleRange(
                        min + ((i - 1) * size),
                        (i == NUMBER_OF_HISTOGRAM_BINS) ? max : (min + (i * size)))
                );
            }
        }

        // Count
        return RasterUtils.summarizeRaster(rasterFile, new BinningRasterSummaryCollator(histogramBins));
    }

    private void createDirectoryForCovariate(String path) throws IOException {
        File dir = Paths.get(path).getParent().toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException(ERROR_CREATE_SUBDIRECTORY);
            }
        }
    }

    private String extractRelativePath(String path) {
        Path parent = Paths.get(covariateService.getCovariateDirectory()).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        Path relativePath = parent.relativize(child).normalize();
        return FilenameUtils.separatorsToUnix(relativePath.toString());
    }
}
