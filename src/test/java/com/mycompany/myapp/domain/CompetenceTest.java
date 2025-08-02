package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CompetenceTestSamples.*;
import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompetenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Competence.class);
        Competence competence1 = getCompetenceSample1();
        Competence competence2 = new Competence();
        assertThat(competence1).isNotEqualTo(competence2);

        competence2.setId(competence1.getId());
        assertThat(competence1).isEqualTo(competence2);

        competence2 = getCompetenceSample2();
        assertThat(competence1).isNotEqualTo(competence2);
    }

    @Test
    void offreEmploiTest() {
        Competence competence = getCompetenceRandomSampleGenerator();
        OffreEmploi offreEmploiBack = getOffreEmploiRandomSampleGenerator();

        competence.addOffreEmploi(offreEmploiBack);
        assertThat(competence.getOffreEmplois()).containsOnly(offreEmploiBack);
        assertThat(offreEmploiBack.getCompetences()).containsOnly(competence);

        competence.removeOffreEmploi(offreEmploiBack);
        assertThat(competence.getOffreEmplois()).doesNotContain(offreEmploiBack);
        assertThat(offreEmploiBack.getCompetences()).doesNotContain(competence);

        competence.offreEmplois(new HashSet<>(Set.of(offreEmploiBack)));
        assertThat(competence.getOffreEmplois()).containsOnly(offreEmploiBack);
        assertThat(offreEmploiBack.getCompetences()).containsOnly(competence);

        competence.setOffreEmplois(new HashSet<>());
        assertThat(competence.getOffreEmplois()).doesNotContain(offreEmploiBack);
        assertThat(offreEmploiBack.getCompetences()).doesNotContain(competence);
    }
}
