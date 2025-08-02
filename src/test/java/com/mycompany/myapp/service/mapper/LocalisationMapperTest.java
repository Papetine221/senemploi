package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.LocalisationAsserts.*;
import static com.mycompany.myapp.domain.LocalisationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalisationMapperTest {

    private LocalisationMapper localisationMapper;

    @BeforeEach
    void setUp() {
        localisationMapper = new LocalisationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocalisationSample1();
        var actual = localisationMapper.toEntity(localisationMapper.toDto(expected));
        assertLocalisationAllPropertiesEquals(expected, actual);
    }
}
