package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Candidature} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidatureDTO implements Serializable {

    private Long id;

    @Lob
    private String lettreMotivation;

    @NotNull
    private Instant datePostulation;

    @NotNull
    private StatutCandidature statut;

    @NotNull
    private CandidatDTO candidat;

    @NotNull
    private OffreEmploiDTO offre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public Instant getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Instant datePostulation) {
        this.datePostulation = datePostulation;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public CandidatDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatDTO candidat) {
        this.candidat = candidat;
    }

    public OffreEmploiDTO getOffre() {
        return offre;
    }

    public void setOffre(OffreEmploiDTO offre) {
        this.offre = offre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidatureDTO)) {
            return false;
        }

        CandidatureDTO candidatureDTO = (CandidatureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, candidatureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidatureDTO{" +
            "id=" + getId() +
            ", lettreMotivation='" + getLettreMotivation() + "'" +
            ", datePostulation='" + getDatePostulation() + "'" +
            ", statut='" + getStatut() + "'" +
            ", candidat=" + getCandidat() +
            ", offre=" + getOffre() +
            "}";
    }
}
