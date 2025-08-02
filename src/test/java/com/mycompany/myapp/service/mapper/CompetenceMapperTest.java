package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CompetenceAsserts.*;
import static com.mycompany.myapp.domain.CompetenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompetenceMapperTest {

    private CompetenceMapper competenceMapper;

    @BeforeEach
    void setUp() {
        competenceMapper = new CompetenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompetenceSample1();
        var actual = competenceMapper.toEntity(competenceMapper.toDto(expected));
        assertCompetenceAllPropertiesEquals(expected, actual);
    }
}
