package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;

import static org.assertj.core.api.Assertions.assertThat;

public class GeoNameDaoTest extends AbstractCommonSpringIntegrationTests {
        @Autowired
        private GeoNameDao geoNameDao;

        @Test
        public void saveAndReloadGeoName() {
            // Arrange
            GeoName geoName = new GeoName();
            geoName.setFeatureCode("ADM1");
            geoName.setId(213);

            // Act
            geoNameDao.save(geoName);

            // Assert
            flushAndClear();
            geoName = geoNameDao.getById(213);

            assertThat(geoName).isNotNull();
            assertThat(geoName.getFeatureCode()).isEqualTo("ADM1");
            assertThat(geoName.getId()).isEqualTo(213);
        }
}
