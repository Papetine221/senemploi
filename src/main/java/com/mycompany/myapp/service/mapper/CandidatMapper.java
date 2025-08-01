package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Candidat;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.CandidatDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidat} and its DTO {@link CandidatDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidatMapper extends EntityMapper<CandidatDTO, Candidat> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    CandidatDTO toDto(Candidat s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
