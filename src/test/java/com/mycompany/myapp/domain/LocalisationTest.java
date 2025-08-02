package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.LocalisationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocalisationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Localisation.class);
        Localisation localisation1 = getLocalisationSample1();
        Localisation localisation2 = new Localisation();
        assertThat(localisation1).isNotEqualTo(localisation2);

        localisation2.setId(localisation1.getId());
        assertThat(localisation1).isEqualTo(localisation2);

        localisation2 = getLocalisationSample2();
        assertThat(localisation1).isNotEqualTo(localisation2);
    }
}
