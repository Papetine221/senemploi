package com.mycompany.myapp.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.OffreEmploi} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OffreEmploiDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    @Lob
    private String description;

    @DecimalMin(value = "0")
    private Double salaire;

    @NotNull
    private Instant datePublication;

    @NotNull
    private Instant dateExpiration;

    @NotNull
    private RecruteurDTO recruteur;

    @NotNull
    private TypeContratDTO typeContrat;

    @NotNull
    private LocalisationDTO localisation;

    @NotNull
    private Set<CompetenceDTO> competences = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSalaire() {
        return salaire;
    }

    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Instant getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Instant datePublication) {
        this.datePublication = datePublication;
    }

    public Instant getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public RecruteurDTO getRecruteur() {
        return recruteur;
    }

    public void setRecruteur(RecruteurDTO recruteur) {
        this.recruteur = recruteur;
    }

    public TypeContratDTO getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContratDTO typeContrat) {
        this.typeContrat = typeContrat;
    }

    public LocalisationDTO getLocalisation() {
        return localisation;
    }

    public void setLocalisation(LocalisationDTO localisation) {
        this.localisation = localisation;
    }

    public Set<CompetenceDTO> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<CompetenceDTO> competences) {
        this.competences = competences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OffreEmploiDTO)) {
            return false;
        }

        OffreEmploiDTO offreEmploiDTO = (OffreEmploiDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, offreEmploiDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OffreEmploiDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", salaire=" + getSalaire() +
            ", datePublication='" + getDatePublication() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            ", recruteur=" + getRecruteur() +
            ", typeContrat=" + getTypeContrat() +
            ", localisation=" + getLocalisation() +
            ", competences=" + getCompetences() +
            "}";
    }
}
