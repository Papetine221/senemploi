package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Localisation} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.LocalisationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /localisations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocalisationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter region;

    private StringFilter departement;

    private StringFilter ville;

    private Boolean distinct;

    public LocalisationCriteria() {}

    public LocalisationCriteria(LocalisationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.region = other.optionalRegion().map(StringFilter::copy).orElse(null);
        this.departement = other.optionalDepartement().map(StringFilter::copy).orElse(null);
        this.ville = other.optionalVille().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LocalisationCriteria copy() {
        return new LocalisationCriteria(this);
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

    public StringFilter getRegion() {
        return region;
    }

    public Optional<StringFilter> optionalRegion() {
        return Optional.ofNullable(region);
    }

    public StringFilter region() {
        if (region == null) {
            setRegion(new StringFilter());
        }
        return region;
    }

    public void setRegion(StringFilter region) {
        this.region = region;
    }

    public StringFilter getDepartement() {
        return departement;
    }

    public Optional<StringFilter> optionalDepartement() {
        return Optional.ofNullable(departement);
    }

    public StringFilter departement() {
        if (departement == null) {
            setDepartement(new StringFilter());
        }
        return departement;
    }

    public void setDepartement(StringFilter departement) {
        this.departement = departement;
    }

    public StringFilter getVille() {
        return ville;
    }

    public Optional<StringFilter> optionalVille() {
        return Optional.ofNullable(ville);
    }

    public StringFilter ville() {
        if (ville == null) {
            setVille(new StringFilter());
        }
        return ville;
    }

    public void setVille(StringFilter ville) {
        this.ville = ville;
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
        final LocalisationCriteria that = (LocalisationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(region, that.region) &&
            Objects.equals(departement, that.departement) &&
            Objects.equals(ville, that.ville) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region, departement, ville, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocalisationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRegion().map(f -> "region=" + f + ", ").orElse("") +
            optionalDepartement().map(f -> "departement=" + f + ", ").orElse("") +
            optionalVille().map(f -> "ville=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
