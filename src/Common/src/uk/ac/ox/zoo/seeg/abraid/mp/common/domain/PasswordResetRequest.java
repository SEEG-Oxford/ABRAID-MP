package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Represents a password reset request.
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "removeRequestsIssuedBeforeDate",
                query = "delete from PasswordResetRequest where requestDate < :cutOffDate"
        ),
        @NamedQuery(
                name = "removeRequestsIssuedForExpert",
                query = "delete from PasswordResetRequest where expert=:expert"
        )
})
@Entity
@Table(name = "password_reset_request")
public class PasswordResetRequest {
    private static final int RESET_KEY_SIZE = 16;

    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // A hash (bcrypt) of the request key.
    @Column(name = "hashed_key", nullable = false)
    private String hashedKey;

    // The associated expert
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    // The date on which the request was issued.
    @Column(name = "request_date", insertable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime requestDate;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(Expert expert, String hashedKey) {
        setExpert(expert);
        setHashedKey(hashedKey);
    }

    public Integer getId() {
        return id;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    /**
     * Creates a new password reset key.
     * @return The new key.
     */
    public static String createPasswordResetRequestKey() {
        SecureRandom rand = new SecureRandom();
        byte[] randomBytes = new byte[RESET_KEY_SIZE];
        rand.nextBytes(randomBytes);
        return new String(Base64.encodeBase64(randomBytes), StandardCharsets.US_ASCII);
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordResetRequest that = (PasswordResetRequest) o;

        if (expert != null ? !expert.equals(that.expert) : that.expert != null) return false;
        if (hashedKey != null ? !hashedKey.equals(that.hashedKey) : that.hashedKey != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (requestDate != null ? !requestDate.equals(that.requestDate) : that.requestDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (hashedKey != null ? hashedKey.hashCode() : 0);
        result = 31 * result + (expert != null ? expert.hashCode() : 0);
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
