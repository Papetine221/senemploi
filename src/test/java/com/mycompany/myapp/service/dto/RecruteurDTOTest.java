package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecruteurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecruteurDTO.class);
        RecruteurDTO recruteurDTO1 = new RecruteurDTO();
        recruteurDTO1.setId(1L);
        RecruteurDTO recruteurDTO2 = new RecruteurDTO();
        assertThat(recruteurDTO1).isNotEqualTo(recruteurDTO2);
        recruteurDTO2.setId(recruteurDTO1.getId());
        assertThat(recruteurDTO1).isEqualTo(recruteurDTO2);
        recruteurDTO2.setId(2L);
        assertThat(recruteurDTO1).isNotEqualTo(recruteurDTO2);
        recruteurDTO1.setId(null);
        assertThat(recruteurDTO1).isNotEqualTo(recruteurDTO2);
    }
}
