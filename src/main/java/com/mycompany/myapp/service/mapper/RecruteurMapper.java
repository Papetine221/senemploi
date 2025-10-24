package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Recruteur} and its DTO {@link RecruteurDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface RecruteurMapper extends EntityMapper<RecruteurDTO, Recruteur> {

    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    RecruteurDTO toDto(Recruteur recruteur);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "login", source = "login")
    })
    UserDTO toDtoUserLogin(User user);
}
