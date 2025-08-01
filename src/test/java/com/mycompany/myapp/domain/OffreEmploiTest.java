package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;
import static com.mycompany.myapp.domain.RecruteurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
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
}
