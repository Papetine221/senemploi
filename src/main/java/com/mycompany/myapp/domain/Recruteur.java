package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Recruteur.
 */
@Entity
@Table(name = "recruteur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Recruteur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom_entreprise", nullable = false)
    private String nomEntreprise;

    @Column(name = "secteur")
    private String secteur;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recruteur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return this.nomEntreprise;
    }

    public Recruteur nomEntreprise(String nomEntreprise) {
        this.setNomEntreprise(nomEntreprise);
        return this;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getSecteur() {
        return this.secteur;
    }

    public Recruteur secteur(String secteur) {
        this.setSecteur(secteur);
        return this;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recruteur user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recruteur)) {
            return false;
        }
        return getId() != null && getId().equals(((Recruteur) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recruteur{" +
            "id=" + getId() +
            ", nomEntreprise='" + getNomEntreprise() + "'" +
            ", secteur='" + getSecteur() + "'" +
            "}";
    }
}
