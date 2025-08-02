package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.repository.LocalisationRepository;
import com.mycompany.myapp.service.LocalisationService;
import com.mycompany.myapp.service.dto.LocalisationDTO;
import com.mycompany.myapp.service.mapper.LocalisationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Localisation}.
 */
@Service
@Transactional
public class LocalisationServiceImpl implements LocalisationService {

    private static final Logger LOG = LoggerFactory.getLogger(LocalisationServiceImpl.class);

    private final LocalisationRepository localisationRepository;

    private final LocalisationMapper localisationMapper;

    public LocalisationServiceImpl(LocalisationRepository localisationRepository, LocalisationMapper localisationMapper) {
        this.localisationRepository = localisationRepository;
        this.localisationMapper = localisationMapper;
    }

    @Override
    public LocalisationDTO save(LocalisationDTO localisationDTO) {
        LOG.debug("Request to save Localisation : {}", localisationDTO);
        Localisation localisation = localisationMapper.toEntity(localisationDTO);
        localisation = localisationRepository.save(localisation);
        return localisationMapper.toDto(localisation);
    }

    @Override
    public LocalisationDTO update(LocalisationDTO localisationDTO) {
        LOG.debug("Request to update Localisation : {}", localisationDTO);
        Localisation localisation = localisationMapper.toEntity(localisationDTO);
        localisation = localisationRepository.save(localisation);
        return localisationMapper.toDto(localisation);
    }

    @Override
    public Optional<LocalisationDTO> partialUpdate(LocalisationDTO localisationDTO) {
        LOG.debug("Request to partially update Localisation : {}", localisationDTO);

        return localisationRepository
            .findById(localisationDTO.getId())
            .map(existingLocalisation -> {
                localisationMapper.partialUpdate(existingLocalisation, localisationDTO);

                return existingLocalisation;
            })
            .map(localisationRepository::save)
            .map(localisationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalisationDTO> findOne(Long id) {
        LOG.debug("Request to get Localisation : {}", id);
        return localisationRepository.findById(id).map(localisationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Localisation : {}", id);
        localisationRepository.deleteById(id);
    }
}
