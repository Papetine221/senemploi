package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.TypeContratService;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.mapper.TypeContratMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TypeContrat}.
 */
@Service
@Transactional
public class TypeContratServiceImpl implements TypeContratService {

    private static final Logger LOG = LoggerFactory.getLogger(TypeContratServiceImpl.class);

    private final TypeContratRepository typeContratRepository;

    private final TypeContratMapper typeContratMapper;

    public TypeContratServiceImpl(TypeContratRepository typeContratRepository, TypeContratMapper typeContratMapper) {
        this.typeContratRepository = typeContratRepository;
        this.typeContratMapper = typeContratMapper;
    }

    @Override
    public TypeContratDTO save(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to save TypeContrat : {}", typeContratDTO);
        TypeContrat typeContrat = typeContratMapper.toEntity(typeContratDTO);
        typeContrat = typeContratRepository.save(typeContrat);
        return typeContratMapper.toDto(typeContrat);
    }

    @Override
    public TypeContratDTO update(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to update TypeContrat : {}", typeContratDTO);
        TypeContrat typeContrat = typeContratMapper.toEntity(typeContratDTO);
        typeContrat = typeContratRepository.save(typeContrat);
        return typeContratMapper.toDto(typeContrat);
    }

    @Override
    public Optional<TypeContratDTO> partialUpdate(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to partially update TypeContrat : {}", typeContratDTO);

        return typeContratRepository
            .findById(typeContratDTO.getId())
            .map(existingTypeContrat -> {
                typeContratMapper.partialUpdate(existingTypeContrat, typeContratDTO);

                return existingTypeContrat;
            })
            .map(typeContratRepository::save)
            .map(typeContratMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypeContratDTO> findOne(Long id) {
        LOG.debug("Request to get TypeContrat : {}", id);
        return typeContratRepository.findById(id).map(typeContratMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TypeContrat : {}", id);
        typeContratRepository.deleteById(id);
    }
}
