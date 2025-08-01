package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.RecruteurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecruteurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recruteur.class);
        Recruteur recruteur1 = getRecruteurSample1();
        Recruteur recruteur2 = new Recruteur();
        assertThat(recruteur1).isNotEqualTo(recruteur2);

        recruteur2.setId(recruteur1.getId());
        assertThat(recruteur1).isEqualTo(recruteur2);

        recruteur2 = getRecruteurSample2();
        assertThat(recruteur1).isNotEqualTo(recruteur2);
    }
}
