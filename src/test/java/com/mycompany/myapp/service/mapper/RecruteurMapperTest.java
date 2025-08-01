package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.RecruteurAsserts.*;
import static com.mycompany.myapp.domain.RecruteurTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecruteurMapperTest {

    private RecruteurMapper recruteurMapper;

    @BeforeEach
    void setUp() {
        recruteurMapper = new RecruteurMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRecruteurSample1();
        var actual = recruteurMapper.toEntity(recruteurMapper.toDto(expected));
        assertRecruteurAllPropertiesEquals(expected, actual);
    }
}
