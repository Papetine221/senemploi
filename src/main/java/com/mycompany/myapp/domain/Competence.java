package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Competence.
 */
@Entity
@Table(name = "competence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Competence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "competences")
    @JsonIgnoreProperties(value = { "recruteur", "typeContrat", "localisation", "competences" }, allowSetters = true)
    private Set<OffreEmploi> offreEmplois = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Competence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Competence nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<OffreEmploi> getOffreEmplois() {
        return this.offreEmplois;
    }

    public void setOffreEmplois(Set<OffreEmploi> offreEmplois) {
        if (this.offreEmplois != null) {
            this.offreEmplois.forEach(i -> i.removeCompetences(this));
        }
        if (offreEmplois != null) {
            offreEmplois.forEach(i -> i.addCompetences(this));
        }
        this.offreEmplois = offreEmplois;
    }

    public Competence offreEmplois(Set<OffreEmploi> offreEmplois) {
        this.setOffreEmplois(offreEmplois);
        return this;
    }

    public Competence addOffreEmploi(OffreEmploi offreEmploi) {
        this.offreEmplois.add(offreEmploi);
        offreEmploi.getCompetences().add(this);
        return this;
    }

    public Competence removeOffreEmploi(OffreEmploi offreEmploi) {
        this.offreEmplois.remove(offreEmploi);
        offreEmploi.getCompetences().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Competence)) {
            return false;
        }
        return getId() != null && getId().equals(((Competence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Competence{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
