package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GeometryService.
 * Copyright (c) 2015 University of Oxford
 */
public class GeometryServiceTest {
    private GeometryService geometryService;
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private AdminUnitQCDao adminUnitQCDao;
    private NativeSQL nativeSQL;
    private LandSeaBorderDao landSeaBorderDao;
    private AdminUnitGlobalDao adminUnitGlobalDao;
    private AdminUnitTropicalDao adminUnitTropicalDao;

    @Before
    public void setUp() {
        countryDao = mock(CountryDao.class);
        healthMapCountryDao = mock(HealthMapCountryDao.class);
        adminUnitQCDao = mock(AdminUnitQCDao.class);
        nativeSQL = mock(NativeSQL.class);
        landSeaBorderDao = mock(LandSeaBorderDao.class);
        landSeaBorderDao = mock(LandSeaBorderDao.class);
        adminUnitGlobalDao = mock(AdminUnitGlobalDao.class);
        adminUnitTropicalDao = mock(AdminUnitTropicalDao.class);
        geometryService = new GeometryServiceImpl(countryDao, healthMapCountryDao, adminUnitQCDao, nativeSQL,
                landSeaBorderDao, adminUnitGlobalDao, adminUnitTropicalDao);
    }

    @Test
    public void getAllCountries() {
        // Arrange
        List<Country> countries = Arrays.asList(new Country());
        when(countryDao.getAll()).thenReturn(countries);

        // Act
        List<Country> testCountries = geometryService.getAllCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllHealthMapCountries() {
        // Arrange
        List<HealthMapCountry> countries = Arrays.asList(new HealthMapCountry());
        when(healthMapCountryDao.getAll()).thenReturn(countries);

        // Act
        List<HealthMapCountry> testCountries = geometryService.getAllHealthMapCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllAdminUnits() {
        // Arrange
        List<AdminUnitQC> adminUnits = Arrays.asList(new AdminUnitQC());
        when(adminUnitQCDao.getAll()).thenReturn(adminUnits);

        // Act
        List<AdminUnitQC> testAdminUnits = geometryService.getAllAdminUnitQCs();

        // Assert
        assertThat(testAdminUnits).isSameAs(adminUnits);
    }


    @Test
    public void getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupReturnsGlobalsForGlobalDisease() {
        // Arrange
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setGlobal(true);
        List<AdminUnitGlobal> expectedAdminUnits = new ArrayList<>();

        when(adminUnitGlobalDao.getAll()).thenReturn(expectedAdminUnits);

        // Act
        List actualAdminUnits =
                geometryService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(diseaseGroup);

        // Assert
        //noinspection unchecked
        assertThat(actualAdminUnits).isSameAs(expectedAdminUnits);
    }

    @Test
    public void getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupReturnsTropicalsForTropicalDisease() {
        // Arrange
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setGlobal(false);
        List<AdminUnitTropical> expectedAdminUnits = new ArrayList<>();

        when(adminUnitTropicalDao.getAll()).thenReturn(expectedAdminUnits);

        // Act
        List actualAdminUnits =
                geometryService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(diseaseGroup);

        // Assert
        //noinspection unchecked
        assertThat(actualAdminUnits).isSameAs(expectedAdminUnits);
    }

    @Test
    public void getAdminUnitGlobalOrTropicalByGaulCodeReturnsCorrectAdminUnitForTropicalDisease() {
        // Arrange
        int gaulCode = 123;
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.isGlobal()).thenReturn(false);
        AdminUnitTropical expectedAdminUnit = mock(AdminUnitTropical.class);

        when(adminUnitTropicalDao.getByGaulCode(gaulCode)).thenReturn(expectedAdminUnit);

        // Act
        AdminUnitGlobalOrTropical actualAdminUnit =
                geometryService.getAdminUnitGlobalOrTropicalByGaulCode(diseaseGroup, gaulCode);

        // Assert
        assertThat(actualAdminUnit).isSameAs(expectedAdminUnit);
    }

    @Test
    public void getAdminUnitGlobalOrTropicalByGaulCodeReturnsCorrectAdminUnitForGlobalDisease() {
        // Arrange
        int gaulCode = 123;
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.isGlobal()).thenReturn(true);
        AdminUnitGlobal expectedAdminUnit = mock(AdminUnitGlobal.class);

        when(adminUnitGlobalDao.getByGaulCode(gaulCode)).thenReturn(expectedAdminUnit);

        // Act
        AdminUnitGlobalOrTropical actualAdminUnit =
                geometryService.getAdminUnitGlobalOrTropicalByGaulCode(diseaseGroup, gaulCode);

        // Assert
        assertThat(actualAdminUnit).isSameAs(expectedAdminUnit);
    }

    @Test
    public void getAdminUnitGlobalOrTropicalByGaulCodeReturnsCorrectAdminUnitForNullDisease() {
        // Arrange
        int gaulCode = 123;

        when(adminUnitGlobalDao.getByGaulCode(gaulCode)).thenReturn(mock(AdminUnitGlobal.class));
        when(adminUnitTropicalDao.getByGaulCode(gaulCode)).thenReturn(mock(AdminUnitTropical.class));

        // Act
        AdminUnitGlobalOrTropical actualAdminUnit =
                geometryService.getAdminUnitGlobalOrTropicalByGaulCode(null, gaulCode);

        // Assert
        assertThat(actualAdminUnit).isNull();
    }

    @Test
    public void getAllLandSeaBorders() {
        // Arrange
        List<LandSeaBorder> landSeaBorders = Arrays.asList(new LandSeaBorder());
        when(landSeaBorderDao.getAll()).thenReturn(landSeaBorders);

        // Act
        List<LandSeaBorder> testLandSeaBorders = geometryService.getAllLandSeaBorders();

        // Assert
        assertThat(testLandSeaBorders).isSameAs(landSeaBorders);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, true)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findAdminUnitGlobalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, false)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findAdminUnitTropicalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findCountryThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findCountryThatContainsPoint(point)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = geometryService.findCountryThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void doesLandSeaBorderContainPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        when(nativeSQL.doesLandSeaBorderContainPoint(point)).thenReturn(true);

        // Act
        boolean result = geometryService.doesLandSeaBorderContainPoint(point);

        // Assert
        assertThat(result).isTrue();
    }
}
