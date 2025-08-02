package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.service.dto.LocalisationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Localisation} and its DTO {@link LocalisationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocalisationMapper extends EntityMapper<LocalisationDTO, Localisation> {}
