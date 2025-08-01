package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Candidat;
import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.service.dto.CandidatDTO;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidature} and its DTO {@link CandidatureDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidatureMapper extends EntityMapper<CandidatureDTO, Candidature> {
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatId")
    @Mapping(target = "offre", source = "offre", qualifiedByName = "offreEmploiId")
    CandidatureDTO toDto(Candidature s);

    @Named("candidatId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidatDTO toDtoCandidatId(Candidat candidat);

    @Named("offreEmploiId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OffreEmploiDTO toDtoOffreEmploiId(OffreEmploi offreEmploi);
}
