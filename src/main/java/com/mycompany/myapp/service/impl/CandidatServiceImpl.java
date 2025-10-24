package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Candidat;
import com.mycompany.myapp.repository.CandidatRepository;
import com.mycompany.myapp.service.CandidatService;
import com.mycompany.myapp.service.dto.CandidatDTO;
import com.mycompany.myapp.service.mapper.CandidatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Candidat}.
 */
@Service
@Transactional
public class CandidatServiceImpl implements CandidatService {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatServiceImpl.class);

    private final CandidatRepository candidatRepository;

    private final CandidatMapper candidatMapper;

    public CandidatServiceImpl(CandidatRepository candidatRepository, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.candidatMapper = candidatMapper;
    }

    @Override
    public CandidatDTO save(CandidatDTO candidatDTO) {
        LOG.debug("Request to save Candidat : {}", candidatDTO);
        Candidat candidat = candidatMapper.toEntity(candidatDTO);
        candidat = candidatRepository.save(candidat);
        return candidatMapper.toDto(candidat);
    }

    @Override
    public CandidatDTO update(CandidatDTO candidatDTO) {
        LOG.debug("Request to update Candidat : {}", candidatDTO);
        Candidat candidat = candidatMapper.toEntity(candidatDTO);
        candidat = candidatRepository.save(candidat);
        return candidatMapper.toDto(candidat);
    }

    @Override
    public Optional<CandidatDTO> partialUpdate(CandidatDTO candidatDTO) {
        LOG.debug("Request to partially update Candidat : {}", candidatDTO);

        return candidatRepository
            .findById(candidatDTO.getId())
            .map(existingCandidat -> {
                candidatMapper.partialUpdate(existingCandidat, candidatDTO);

                return existingCandidat;
            })
            .map(candidatRepository::save)
            .map(candidatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CandidatDTO> findOne(Long id) {
        LOG.debug("Request to get Candidat : {}", id);
        return candidatRepository.findById(id).map(candidatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CandidatDTO> findByUserLogin(String userLogin) {
        LOG.debug("Request to get Candidat by user login : {}", userLogin);
        return candidatRepository.findByUserLogin(userLogin).map(candidatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Candidat : {}", id);
        candidatRepository.deleteById(id);
    }
}
