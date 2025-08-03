package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Candidature.
 */
@Entity
@Table(name = "candidature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Candidature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "lettre_motivation")
    private String lettreMotivation;

    @NotNull
    @Column(name = "date_postulation", nullable = false)
    private Instant datePostulation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutCandidature statut;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Candidat candidat;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "recruteur", "typeContrat", "localisation", "competences" }, allowSetters = true)
    private OffreEmploi offre;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Candidature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLettreMotivation() {
        return this.lettreMotivation;
    }

    public Candidature lettreMotivation(String lettreMotivation) {
        this.setLettreMotivation(lettreMotivation);
        return this;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public Instant getDatePostulation() {
        return this.datePostulation;
    }

    public Candidature datePostulation(Instant datePostulation) {
        this.setDatePostulation(datePostulation);
        return this;
    }

    public void setDatePostulation(Instant datePostulation) {
        this.datePostulation = datePostulation;
    }

    public StatutCandidature getStatut() {
        return this.statut;
    }

    public Candidature statut(StatutCandidature statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Candidature candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    public OffreEmploi getOffre() {
        return this.offre;
    }

    public void setOffre(OffreEmploi offreEmploi) {
        this.offre = offreEmploi;
    }

    public Candidature offre(OffreEmploi offreEmploi) {
        this.setOffre(offreEmploi);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidature)) {
            return false;
        }
        return getId() != null && getId().equals(((Candidature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Candidature{" +
            "id=" + getId() +
            ", lettreMotivation='" + getLettreMotivation() + "'" +
            ", datePostulation='" + getDatePostulation() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
