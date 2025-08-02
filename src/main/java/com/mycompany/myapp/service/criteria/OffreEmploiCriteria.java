package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.OffreEmploi} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.OffreEmploiResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /offre-emplois?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OffreEmploiCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titre;

    private DoubleFilter salaire;

    private InstantFilter datePublication;

    private InstantFilter dateExpiration;

    private LongFilter recruteurId;

    private LongFilter typeContratId;

    private LongFilter localisationId;

    private LongFilter competencesId;

    private Boolean distinct;

    public OffreEmploiCriteria() {}

    public OffreEmploiCriteria(OffreEmploiCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.salaire = other.optionalSalaire().map(DoubleFilter::copy).orElse(null);
        this.datePublication = other.optionalDatePublication().map(InstantFilter::copy).orElse(null);
        this.dateExpiration = other.optionalDateExpiration().map(InstantFilter::copy).orElse(null);
        this.recruteurId = other.optionalRecruteurId().map(LongFilter::copy).orElse(null);
        this.typeContratId = other.optionalTypeContratId().map(LongFilter::copy).orElse(null);
        this.localisationId = other.optionalLocalisationId().map(LongFilter::copy).orElse(null);
        this.competencesId = other.optionalCompetencesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OffreEmploiCriteria copy() {
        return new OffreEmploiCriteria(this);
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

    public StringFilter getTitre() {
        return titre;
    }

    public Optional<StringFilter> optionalTitre() {
        return Optional.ofNullable(titre);
    }

    public StringFilter titre() {
        if (titre == null) {
            setTitre(new StringFilter());
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
    }

    public DoubleFilter getSalaire() {
        return salaire;
    }

    public Optional<DoubleFilter> optionalSalaire() {
        return Optional.ofNullable(salaire);
    }

    public DoubleFilter salaire() {
        if (salaire == null) {
            setSalaire(new DoubleFilter());
        }
        return salaire;
    }

    public void setSalaire(DoubleFilter salaire) {
        this.salaire = salaire;
    }

    public InstantFilter getDatePublication() {
        return datePublication;
    }

    public Optional<InstantFilter> optionalDatePublication() {
        return Optional.ofNullable(datePublication);
    }

    public InstantFilter datePublication() {
        if (datePublication == null) {
            setDatePublication(new InstantFilter());
        }
        return datePublication;
    }

    public void setDatePublication(InstantFilter datePublication) {
        this.datePublication = datePublication;
    }

    public InstantFilter getDateExpiration() {
        return dateExpiration;
    }

    public Optional<InstantFilter> optionalDateExpiration() {
        return Optional.ofNullable(dateExpiration);
    }

    public InstantFilter dateExpiration() {
        if (dateExpiration == null) {
            setDateExpiration(new InstantFilter());
        }
        return dateExpiration;
    }

    public void setDateExpiration(InstantFilter dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public LongFilter getRecruteurId() {
        return recruteurId;
    }

    public Optional<LongFilter> optionalRecruteurId() {
        return Optional.ofNullable(recruteurId);
    }

    public LongFilter recruteurId() {
        if (recruteurId == null) {
            setRecruteurId(new LongFilter());
        }
        return recruteurId;
    }

    public void setRecruteurId(LongFilter recruteurId) {
        this.recruteurId = recruteurId;
    }

    public LongFilter getTypeContratId() {
        return typeContratId;
    }

    public Optional<LongFilter> optionalTypeContratId() {
        return Optional.ofNullable(typeContratId);
    }

    public LongFilter typeContratId() {
        if (typeContratId == null) {
            setTypeContratId(new LongFilter());
        }
        return typeContratId;
    }

    public void setTypeContratId(LongFilter typeContratId) {
        this.typeContratId = typeContratId;
    }

    public LongFilter getLocalisationId() {
        return localisationId;
    }

    public Optional<LongFilter> optionalLocalisationId() {
        return Optional.ofNullable(localisationId);
    }

    public LongFilter localisationId() {
        if (localisationId == null) {
            setLocalisationId(new LongFilter());
        }
        return localisationId;
    }

    public void setLocalisationId(LongFilter localisationId) {
        this.localisationId = localisationId;
    }

    public LongFilter getCompetencesId() {
        return competencesId;
    }

    public Optional<LongFilter> optionalCompetencesId() {
        return Optional.ofNullable(competencesId);
    }

    public LongFilter competencesId() {
        if (competencesId == null) {
            setCompetencesId(new LongFilter());
        }
        return competencesId;
    }

    public void setCompetencesId(LongFilter competencesId) {
        this.competencesId = competencesId;
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
        final OffreEmploiCriteria that = (OffreEmploiCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(salaire, that.salaire) &&
            Objects.equals(datePublication, that.datePublication) &&
            Objects.equals(dateExpiration, that.dateExpiration) &&
            Objects.equals(recruteurId, that.recruteurId) &&
            Objects.equals(typeContratId, that.typeContratId) &&
            Objects.equals(localisationId, that.localisationId) &&
            Objects.equals(competencesId, that.competencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            titre,
            salaire,
            datePublication,
            dateExpiration,
            recruteurId,
            typeContratId,
            localisationId,
            competencesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OffreEmploiCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalSalaire().map(f -> "salaire=" + f + ", ").orElse("") +
            optionalDatePublication().map(f -> "datePublication=" + f + ", ").orElse("") +
            optionalDateExpiration().map(f -> "dateExpiration=" + f + ", ").orElse("") +
            optionalRecruteurId().map(f -> "recruteurId=" + f + ", ").orElse("") +
            optionalTypeContratId().map(f -> "typeContratId=" + f + ", ").orElse("") +
            optionalLocalisationId().map(f -> "localisationId=" + f + ", ").orElse("") +
            optionalCompetencesId().map(f -> "competencesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
