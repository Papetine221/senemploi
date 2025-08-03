package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Candidature} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.CandidatureResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /candidatures?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidatureCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutCandidature
     */
    public static class StatutCandidatureFilter extends Filter<StatutCandidature> {

        public StatutCandidatureFilter() {}

        public StatutCandidatureFilter(StatutCandidatureFilter filter) {
            super(filter);
        }

        @Override
        public StatutCandidatureFilter copy() {
            return new StatutCandidatureFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter datePostulation;

    private StatutCandidatureFilter statut;

    private LongFilter candidatId;

    private LongFilter offreId;

    private Boolean distinct;

    public CandidatureCriteria() {}

    public CandidatureCriteria(CandidatureCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.datePostulation = other.optionalDatePostulation().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutCandidatureFilter::copy).orElse(null);
        this.candidatId = other.optionalCandidatId().map(LongFilter::copy).orElse(null);
        this.offreId = other.optionalOffreId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CandidatureCriteria copy() {
        return new CandidatureCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getDatePostulation() {
        return datePostulation;
    }

    public Optional<InstantFilter> optionalDatePostulation() {
        return Optional.ofNullable(datePostulation);
    }

    public InstantFilter datePostulation() {
        if (datePostulation == null) {
            setDatePostulation(new InstantFilter());
        }
        return datePostulation;
    }

    public void setDatePostulation(InstantFilter datePostulation) {
        this.datePostulation = datePostulation;
    }

    public StatutCandidatureFilter getStatut() {
        return statut;
    }

    public Optional<StatutCandidatureFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutCandidatureFilter statut() {
        if (statut == null) {
            setStatut(new StatutCandidatureFilter());
        }
        return statut;
    }

    public void setStatut(StatutCandidatureFilter statut) {
        this.statut = statut;
    }

    public LongFilter getCandidatId() {
        return candidatId;
    }

    public Optional<LongFilter> optionalCandidatId() {
        return Optional.ofNullable(candidatId);
    }

    public LongFilter candidatId() {
        if (candidatId == null) {
            setCandidatId(new LongFilter());
        }
        return candidatId;
    }

    public void setCandidatId(LongFilter candidatId) {
        this.candidatId = candidatId;
    }

    public LongFilter getOffreId() {
        return offreId;
    }

    public Optional<LongFilter> optionalOffreId() {
        return Optional.ofNullable(offreId);
    }

    public LongFilter offreId() {
        if (offreId == null) {
            setOffreId(new LongFilter());
        }
        return offreId;
    }

    public void setOffreId(LongFilter offreId) {
        this.offreId = offreId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CandidatureCriteria that = (CandidatureCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(datePostulation, that.datePostulation) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(candidatId, that.candidatId) &&
            Objects.equals(offreId, that.offreId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datePostulation, statut, candidatId, offreId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidatureCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDatePostulation().map(f -> "datePostulation=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalCandidatId().map(f -> "candidatId=" + f + ", ").orElse("") +
            optionalOffreId().map(f -> "offreId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
