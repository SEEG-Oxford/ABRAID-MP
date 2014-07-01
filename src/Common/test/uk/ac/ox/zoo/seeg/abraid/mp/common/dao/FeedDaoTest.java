package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the FeedDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class FeedDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private FeedDao feedDao;

    @Autowired
    private ProvenanceDao provenanceDao;

    @Test
    public void saveAndReloadFeed() {
        // Arrange
        String feedName = "Test feed";
        double feedWeighting = 0.3;
        String feedLanguage = "vi";

        Provenance provenance = provenanceDao.getByName(ProvenanceNames.HEALTHMAP);
        Feed feed = new Feed();
        feed.setProvenance(provenance);
        feed.setWeighting(feedWeighting);
        feed.setName(feedName);
        feed.setLanguage(feedLanguage);

        // Act
        feedDao.save(feed);
        assertThat(feed.getCreatedDate()).isNotNull();

        // Assert
        Integer id = feed.getId();
        flushAndClear();
        feed = feedDao.getById(id);

        assertThat(feed).isNotNull();
        assertThat(feed.getName()).isEqualTo(feedName);
        assertThat(feed.getProvenance()).isEqualTo(provenance);
        assertThat(feed.getCreatedDate()).isNotNull();
        assertThat(feed.getWeighting()).isEqualTo(feedWeighting);
        assertThat(feed.getLanguage()).isEqualTo(feedLanguage);
    }

    @Test
    public void loadNonExistentFeed() {
        Feed feed = feedDao.getById(-1);
        assertThat(feed).isNull();
    }

    @Test
    public void getFeedsByProvenanceName() {
        List<Feed> feeds = feedDao.getByProvenanceName(ProvenanceNames.HEALTHMAP);
        assertThat(feeds).hasSize(66);
    }
}
