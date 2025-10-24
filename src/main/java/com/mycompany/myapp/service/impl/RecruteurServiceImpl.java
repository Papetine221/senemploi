package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.repository.RecruteurRepository;
import com.mycompany.myapp.service.RecruteurService;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.mapper.RecruteurMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Recruteur}.
 */
@Service
@Transactional
public class RecruteurServiceImpl implements RecruteurService {

    private static final Logger LOG = LoggerFactory.getLogger(RecruteurServiceImpl.class);

    private final RecruteurRepository recruteurRepository;
    private final RecruteurMapper recruteurMapper;

    public RecruteurServiceImpl(RecruteurRepository recruteurRepository, RecruteurMapper recruteurMapper) {
        this.recruteurRepository = recruteurRepository;
        this.recruteurMapper = recruteurMapper;
    }

    @Override
    public RecruteurDTO save(RecruteurDTO recruteurDTO) {
        LOG.debug("Request to save Recruteur : {}", recruteurDTO);
        Recruteur recruteur = recruteurMapper.toEntity(recruteurDTO);
        recruteur = recruteurRepository.save(recruteur);
        return recruteurMapper.toDto(recruteur);
    }

    @Override
    public RecruteurDTO update(RecruteurDTO recruteurDTO) {
        LOG.debug("Request to update Recruteur : {}", recruteurDTO);
        Recruteur recruteur = recruteurMapper.toEntity(recruteurDTO);
        recruteur = recruteurRepository.save(recruteur);
        return recruteurMapper.toDto(recruteur);
    }

    @Override
    public Optional<RecruteurDTO> partialUpdate(RecruteurDTO recruteurDTO) {
        LOG.debug("Request to partially update Recruteur : {}", recruteurDTO);

        return recruteurRepository
            .findById(recruteurDTO.getId())
            .map(existingRecruteur -> {
                recruteurMapper.partialUpdate(existingRecruteur, recruteurDTO);
                return existingRecruteur;
            })
            .map(recruteurRepository::save)
            .map(recruteurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecruteurDTO> findOne(Long id) {
        LOG.debug("Request to get Recruteur : {}", id);
        return recruteurRepository.findById(id).map(recruteurMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Recruteur : {}", id);
        recruteurRepository.deleteById(id);
    }

    /**
     * ✅ Méthode unique et correcte pour retrouver un recruteur par le login de l'utilisateur connecté.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RecruteurDTO> findByUserLogin(String login) {
        LOG.debug("Request to get Recruteur by user login : {}", login);
        return recruteurRepository.findByUser_Login(login)
            .map(recruteurMapper::toDto);
    }
}
