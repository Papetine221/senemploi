package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.OffreEmploiService;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.OffreEmploi}.
 */
@Service
@Transactional
public class OffreEmploiServiceImpl implements OffreEmploiService {

    private static final Logger LOG = LoggerFactory.getLogger(OffreEmploiServiceImpl.class);

    private final OffreEmploiRepository offreEmploiRepository;

    private final OffreEmploiMapper offreEmploiMapper;

    public OffreEmploiServiceImpl(OffreEmploiRepository offreEmploiRepository, OffreEmploiMapper offreEmploiMapper) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.offreEmploiMapper = offreEmploiMapper;
    }

    @Override
    public OffreEmploiDTO save(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to save OffreEmploi : {}", offreEmploiDTO);
        OffreEmploi offreEmploi = offreEmploiMapper.toEntity(offreEmploiDTO);
        offreEmploi = offreEmploiRepository.save(offreEmploi);
        return offreEmploiMapper.toDto(offreEmploi);
    }

    @Override
    public OffreEmploiDTO update(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to update OffreEmploi : {}", offreEmploiDTO);
        OffreEmploi offreEmploi = offreEmploiMapper.toEntity(offreEmploiDTO);
        offreEmploi = offreEmploiRepository.save(offreEmploi);
        return offreEmploiMapper.toDto(offreEmploi);
    }

    @Override
    public Optional<OffreEmploiDTO> partialUpdate(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to partially update OffreEmploi : {}", offreEmploiDTO);

        return offreEmploiRepository
            .findById(offreEmploiDTO.getId())
            .map(existingOffreEmploi -> {
                offreEmploiMapper.partialUpdate(existingOffreEmploi, offreEmploiDTO);

                return existingOffreEmploi;
            })
            .map(offreEmploiRepository::save)
            .map(offreEmploiMapper::toDto);
    }

    public Page<OffreEmploiDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offreEmploiRepository.findAllWithEagerRelationships(pageable).map(offreEmploiMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OffreEmploiDTO> findOne(Long id) {
        LOG.debug("Request to get OffreEmploi : {}", id);
        return offreEmploiRepository.findOneWithEagerRelationships(id).map(offreEmploiMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete OffreEmploi : {}", id);
        offreEmploiRepository.deleteById(id);
    }
}
