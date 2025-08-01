package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OffreEmploi} and its DTO {@link OffreEmploiDTO}.
 */
@Mapper(componentModel = "spring")
public interface OffreEmploiMapper extends EntityMapper<OffreEmploiDTO, OffreEmploi> {
    @Mapping(target = "recruteur", source = "recruteur", qualifiedByName = "recruteurId")
    OffreEmploiDTO toDto(OffreEmploi s);

    @Named("recruteurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RecruteurDTO toDtoRecruteurId(Recruteur recruteur);
}
