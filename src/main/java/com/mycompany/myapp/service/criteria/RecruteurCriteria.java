package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Recruteur} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.RecruteurResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /recruteurs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RecruteurCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomEntreprise;

    private StringFilter secteur;

    private LongFilter userId;

    private Boolean distinct;

    public RecruteurCriteria() {}

    public RecruteurCriteria(RecruteurCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nomEntreprise = other.optionalNomEntreprise().map(StringFilter::copy).orElse(null);
        this.secteur = other.optionalSecteur().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RecruteurCriteria copy() {
        return new RecruteurCriteria(this);
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

    public StringFilter getNomEntreprise() {
        return nomEntreprise;
    }

    public Optional<StringFilter> optionalNomEntreprise() {
        return Optional.ofNullable(nomEntreprise);
    }

    public StringFilter nomEntreprise() {
        if (nomEntreprise == null) {
            setNomEntreprise(new StringFilter());
        }
        return nomEntreprise;
    }

    public void setNomEntreprise(StringFilter nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public StringFilter getSecteur() {
        return secteur;
    }

    public Optional<StringFilter> optionalSecteur() {
        return Optional.ofNullable(secteur);
    }

    public StringFilter secteur() {
        if (secteur == null) {
            setSecteur(new StringFilter());
        }
        return secteur;
    }

    public void setSecteur(StringFilter secteur) {
        this.secteur = secteur;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final RecruteurCriteria that = (RecruteurCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomEntreprise, that.nomEntreprise) &&
            Objects.equals(secteur, that.secteur) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomEntreprise, secteur, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecruteurCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNomEntreprise().map(f -> "nomEntreprise=" + f + ", ").orElse("") +
            optionalSecteur().map(f -> "secteur=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
