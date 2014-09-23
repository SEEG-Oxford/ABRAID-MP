package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents an expert's response to the presence or absence of a disease group across an admin unit.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getAdminUnitReviewCountByExpertId",
                query = "select count(*) from AdminUnitReview where expert.id=:expertId"
        ),
        @NamedQuery(
                name = "getAdminUnitReviewsByDiseaseGroupId",
                query = "from AdminUnitReview where diseaseGroup.id=:diseaseGroupId and response is not null"
        ),
        @NamedQuery(
                name = "getAdminUnitReviewsByExpertIdAndDiseaseGroupId",
                query = "from AdminUnitReview where expert.id=:expertId and diseaseGroup.id=:diseaseGroupId"
        )
})
@Entity
@Table(name = "admin_unit_review")
public class AdminUnitReview {
    // The id of the review.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The expert.
    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    // The GAUL code of the global administrative unit.
    @Column(name = "global_gaul_code")
    private Integer adminUnitGlobalGaulCode;

    // The GAUL code of the tropical administrative unit.
    @Column(name = "tropical_gaul_code")
    private Integer adminUnitTropicalGaulCode;

    // The disease group (to clarify, this is not referring to the validator disease group).
    @ManyToOne
    @JoinColumn(name = "disease_group_id", nullable = false)
    private DiseaseGroup diseaseGroup;

    // The expert's response.
    @ManyToOne
    @JoinColumn(name = "response")
    private DiseaseExtentClass response;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public AdminUnitReview() {
    }

    public AdminUnitReview(Expert expert, Integer adminUnitGlobalGaulCode, Integer adminUnitTropicalGaulCode,
                           DiseaseGroup diseaseGroup, DiseaseExtentClass response) {
        this.expert = expert;
        this.adminUnitGlobalGaulCode = adminUnitGlobalGaulCode;
        this.adminUnitTropicalGaulCode = adminUnitTropicalGaulCode;
        this.diseaseGroup = diseaseGroup;
        this.response = response;
    }

    /**
     * The GAUL code of the global or tropical admin unit, whichever is not null.
     * @return The (global or tropical) admin unit.
     */
    public Integer getAdminUnitGlobalOrTropicalGaulCode() {
        return (adminUnitGlobalGaulCode == null) ? adminUnitTropicalGaulCode : adminUnitGlobalGaulCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public Integer getAdminUnitGlobalGaulCode() {
        return adminUnitGlobalGaulCode;
    }

    public void setAdminUnitGlobalGaulCode(Integer adminUnitGlobalGaulCode) {
        this.adminUnitGlobalGaulCode = adminUnitGlobalGaulCode;
    }

    public Integer getAdminUnitTropicalGaulCode() {
        return adminUnitTropicalGaulCode;
    }

    public void setAdminUnitTropicalGaulCode(Integer adminUnitTropicalGaulCode) {
        this.adminUnitTropicalGaulCode = adminUnitTropicalGaulCode;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public DiseaseExtentClass getResponse() {
        return response;
    }

    public void setResponse(DiseaseExtentClass response) {
        this.response = response;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminUnitReview that = (AdminUnitReview) o;

        if (adminUnitGlobalGaulCode != null ? !adminUnitGlobalGaulCode.equals(that.adminUnitGlobalGaulCode) : that.adminUnitGlobalGaulCode != null)
            return false;
        if (adminUnitTropicalGaulCode != null ? !adminUnitTropicalGaulCode.equals(that.adminUnitTropicalGaulCode) : that.adminUnitTropicalGaulCode != null)
            return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (expert != null ? !expert.equals(that.expert) : that.expert != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (response != null ? !response.equals(that.response) : that.response != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (expert != null ? expert.hashCode() : 0);
        result = 31 * result + (adminUnitGlobalGaulCode != null ? adminUnitGlobalGaulCode.hashCode() : 0);
        result = 31 * result + (adminUnitTropicalGaulCode != null ? adminUnitTropicalGaulCode.hashCode() : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
