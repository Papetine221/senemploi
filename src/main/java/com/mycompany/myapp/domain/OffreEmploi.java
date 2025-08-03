package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A OffreEmploi.
 */
@Entity
@Table(name = "offre_emploi")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OffreEmploi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @DecimalMin(value = "0")
    @Column(name = "salaire")
    private Double salaire;

    @NotNull
    @Column(name = "date_publication", nullable = false)
    private Instant datePublication;

    @NotNull
    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Recruteur recruteur;

    @ManyToOne(optional = false)
    @NotNull
    private TypeContrat typeContrat;

    @ManyToOne(optional = false)
    @NotNull
    private Localisation localisation;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    @JoinTable(
        name = "rel_offre_emploi__competences",
        joinColumns = @JoinColumn(name = "offre_emploi_id"),
        inverseJoinColumns = @JoinColumn(name = "competences_id")
    )
    @JsonIgnoreProperties(value = { "offreEmplois" }, allowSetters = true)
    private Set<Competence> competences = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OffreEmploi id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public OffreEmploi titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public OffreEmploi description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSalaire() {
        return this.salaire;
    }

    public OffreEmploi salaire(Double salaire) {
        this.setSalaire(salaire);
        return this;
    }

    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Instant getDatePublication() {
        return this.datePublication;
    }

    public OffreEmploi datePublication(Instant datePublication) {
        this.setDatePublication(datePublication);
        return this;
    }

    public void setDatePublication(Instant datePublication) {
        this.datePublication = datePublication;
    }

    public Instant getDateExpiration() {
        return this.dateExpiration;
    }

    public OffreEmploi dateExpiration(Instant dateExpiration) {
        this.setDateExpiration(dateExpiration);
        return this;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Recruteur getRecruteur() {
        return this.recruteur;
    }

    public void setRecruteur(Recruteur recruteur) {
        this.recruteur = recruteur;
    }

    public OffreEmploi recruteur(Recruteur recruteur) {
        this.setRecruteur(recruteur);
        return this;
    }

    public TypeContrat getTypeContrat() {
        return this.typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public OffreEmploi typeContrat(TypeContrat typeContrat) {
        this.setTypeContrat(typeContrat);
        return this;
    }

    public Localisation getLocalisation() {
        return this.localisation;
    }

    public void setLocalisation(Localisation localisation) {
        this.localisation = localisation;
    }

    public OffreEmploi localisation(Localisation localisation) {
        this.setLocalisation(localisation);
        return this;
    }

    public Set<Competence> getCompetences() {
        return this.competences;
    }

    public void setCompetences(Set<Competence> competences) {
        this.competences = competences;
    }

    public OffreEmploi competences(Set<Competence> competences) {
        this.setCompetences(competences);
        return this;
    }

    public OffreEmploi addCompetences(Competence competence) {
        this.competences.add(competence);
        return this;
    }

    public OffreEmploi removeCompetences(Competence competence) {
        this.competences.remove(competence);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OffreEmploi)) {
            return false;
        }
        return getId() != null && getId().equals(((OffreEmploi) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OffreEmploi{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", salaire=" + getSalaire() +
            ", datePublication='" + getDatePublication() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            "}";
    }
}
