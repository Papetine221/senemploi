package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Competence;
import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.service.dto.CompetenceDTO;
import com.mycompany.myapp.service.dto.LocalisationDTO;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OffreEmploi} and its DTO {@link OffreEmploiDTO}.
 */
@Mapper(componentModel = "spring", uses = {TypeContratMapper.class, LocalisationMapper.class, RecruteurMapper.class, CompetenceMapper.class})
public interface OffreEmploiMapper extends EntityMapper<OffreEmploiDTO, OffreEmploi> {

    @Mapping(target = "recruteur", source = "recruteur")
    @Mapping(target = "typeContrat", source = "typeContrat")
    @Mapping(target = "localisation", source = "localisation")
    @Mapping(target = "competences", source = "competences")
    OffreEmploiDTO toDto(OffreEmploi offreEmploi);

    @Mapping(target = "removeCompetences", ignore = true)
    OffreEmploi toEntity(OffreEmploiDTO offreEmploiDTO);
}
