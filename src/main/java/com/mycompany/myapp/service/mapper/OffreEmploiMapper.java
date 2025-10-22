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
<<<<<<< HEAD

    @Named("recruteurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomEntreprise", source = "nomEntreprise")
    RecruteurDTO toDtoRecruteurId(Recruteur recruteur);

    @Named("typeContratId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    TypeContratDTO toDtoTypeContratId(TypeContrat typeContrat);

    @Named("localisationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "ville", source = "ville")
    @Mapping(target = "region", source = "region")
    LocalisationDTO toDtoLocalisationId(Localisation localisation);

    @Named("competenceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CompetenceDTO toDtoCompetenceId(Competence competence);

    @Named("competenceIdSet")
    default Set<CompetenceDTO> toDtoCompetenceIdSet(Set<Competence> competence) {
        return competence.stream().map(this::toDtoCompetenceId).collect(Collectors.toSet());
    }
=======
>>>>>>> f05d900991ef76322668b5aa628a93b7261d32be
}
