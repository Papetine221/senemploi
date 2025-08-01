package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Recruteur} and its DTO {@link RecruteurDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecruteurMapper extends EntityMapper<RecruteurDTO, Recruteur> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    RecruteurDTO toDto(Recruteur s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
