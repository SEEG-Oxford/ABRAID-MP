package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceName;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the FeedDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class FeedDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private FeedDao feedDao;

    @Autowired
    private ProvenanceDao provenanceDao;

    @Test
    public void saveAndReloadFeed() {
        // Arrange
        String feedName = "Test feed";
        double feedWeighting = 0.3;

        Provenance provenance = provenanceDao.getByName(ProvenanceName.HEALTHMAP.getName());
        Feed feed = new Feed();
        feed.setProvenance(provenance);
        feed.setWeighting(feedWeighting);
        feed.setName(feedName);

        // Act
        feedDao.save(feed);
        Integer id = feed.getId();
        flushAndClear();

        // Assert
        feed = feedDao.getById(id);
        assertThat(feed).isNotNull();
        assertThat(feed.getName()).isEqualTo(feedName);
        assertThat(feed.getProvenance()).isEqualTo(provenance);
        assertThat(feed.getCreatedDate()).isNotNull();
        assertThat(feed.getWeighting()).isEqualTo(feedWeighting);
    }

    @Test
    public void loadNonExistentFeed() {
        Feed feed = feedDao.getById(-1);
        assertThat(feed).isNull();
    }

    @Test
    public void getFeedsByProvenanceName() {
        List<Feed> feeds = feedDao.getByProvenanceName(ProvenanceName.HEALTHMAP);
        assertThat(feeds).hasSize(64);
    }
}
