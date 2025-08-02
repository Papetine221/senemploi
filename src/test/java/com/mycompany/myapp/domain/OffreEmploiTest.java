package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CompetenceTestSamples.*;
import static com.mycompany.myapp.domain.LocalisationTestSamples.*;
import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;
import static com.mycompany.myapp.domain.RecruteurTestSamples.*;
import static com.mycompany.myapp.domain.TypeContratTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OffreEmploiTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OffreEmploi.class);
        OffreEmploi offreEmploi1 = getOffreEmploiSample1();
        OffreEmploi offreEmploi2 = new OffreEmploi();
        assertThat(offreEmploi1).isNotEqualTo(offreEmploi2);

        offreEmploi2.setId(offreEmploi1.getId());
        assertThat(offreEmploi1).isEqualTo(offreEmploi2);

        offreEmploi2 = getOffreEmploiSample2();
        assertThat(offreEmploi1).isNotEqualTo(offreEmploi2);
    }

    @Test
    void recruteurTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        Recruteur recruteurBack = getRecruteurRandomSampleGenerator();

        offreEmploi.setRecruteur(recruteurBack);
        assertThat(offreEmploi.getRecruteur()).isEqualTo(recruteurBack);

        offreEmploi.recruteur(null);
        assertThat(offreEmploi.getRecruteur()).isNull();
    }

    @Test
    void typeContratTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        TypeContrat typeContratBack = getTypeContratRandomSampleGenerator();

        offreEmploi.setTypeContrat(typeContratBack);
        assertThat(offreEmploi.getTypeContrat()).isEqualTo(typeContratBack);

        offreEmploi.typeContrat(null);
        assertThat(offreEmploi.getTypeContrat()).isNull();
    }

    @Test
    void localisationTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        Localisation localisationBack = getLocalisationRandomSampleGenerator();

        offreEmploi.setLocalisation(localisationBack);
        assertThat(offreEmploi.getLocalisation()).isEqualTo(localisationBack);

        offreEmploi.localisation(null);
        assertThat(offreEmploi.getLocalisation()).isNull();
    }

    @Test
    void competencesTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        Competence competenceBack = getCompetenceRandomSampleGenerator();

        offreEmploi.addCompetences(competenceBack);
        assertThat(offreEmploi.getCompetences()).containsOnly(competenceBack);

        offreEmploi.removeCompetences(competenceBack);
        assertThat(offreEmploi.getCompetences()).doesNotContain(competenceBack);

        offreEmploi.competences(new HashSet<>(Set.of(competenceBack)));
        assertThat(offreEmploi.getCompetences()).containsOnly(competenceBack);

        offreEmploi.setCompetences(new HashSet<>());
        assertThat(offreEmploi.getCompetences()).doesNotContain(competenceBack);
    }
}
