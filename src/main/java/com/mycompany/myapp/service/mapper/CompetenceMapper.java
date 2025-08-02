package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Competence;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.service.dto.CompetenceDTO;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Competence} and its DTO {@link CompetenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompetenceMapper extends EntityMapper<CompetenceDTO, Competence> {
    @Mapping(target = "offreEmplois", source = "offreEmplois", qualifiedByName = "offreEmploiIdSet")
    CompetenceDTO toDto(Competence s);

    @Mapping(target = "offreEmplois", ignore = true)
    @Mapping(target = "removeOffreEmploi", ignore = true)
    Competence toEntity(CompetenceDTO competenceDTO);

    @Named("offreEmploiId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OffreEmploiDTO toDtoOffreEmploiId(OffreEmploi offreEmploi);

    @Named("offreEmploiIdSet")
    default Set<OffreEmploiDTO> toDtoOffreEmploiIdSet(Set<OffreEmploi> offreEmploi) {
        return offreEmploi.stream().map(this::toDtoOffreEmploiId).collect(Collectors.toSet());
    }
}
