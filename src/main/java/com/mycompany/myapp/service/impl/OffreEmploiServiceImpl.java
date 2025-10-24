package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.repository.RecruteurRepository;
import com.mycompany.myapp.service.OffreEmploiService;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OffreEmploiServiceImpl implements OffreEmploiService {

    private final Logger LOG = LoggerFactory.getLogger(OffreEmploiServiceImpl.class);

    private final OffreEmploiRepository offreEmploiRepository;
    private final OffreEmploiMapper offreEmploiMapper;
    private final RecruteurRepository recruteurRepository;

    public OffreEmploiServiceImpl(
        OffreEmploiRepository offreEmploiRepository,
        OffreEmploiMapper offreEmploiMapper,
        RecruteurRepository recruteurRepository
    ) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.offreEmploiMapper = offreEmploiMapper;
        this.recruteurRepository = recruteurRepository;
    }

    //  Créer une offre
    @Override
    public OffreEmploiDTO save(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to save OffreEmploi : {}", offreEmploiDTO);
        OffreEmploi offreEmploi = offreEmploiMapper.toEntity(offreEmploiDTO);
        offreEmploi = offreEmploiRepository.save(offreEmploi);
        return offreEmploiMapper.toDto(offreEmploi);
    }

    //  Mettre à jour une offre
    @Override
    public OffreEmploiDTO update(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to update OffreEmploi : {}", offreEmploiDTO);
        OffreEmploi offreEmploi = offreEmploiMapper.toEntity(offreEmploiDTO);
        offreEmploi = offreEmploiRepository.save(offreEmploi);
        return offreEmploiMapper.toDto(offreEmploi);
    }

    //  Mise à jour partielle
    @Override
    public Optional<OffreEmploiDTO> partialUpdate(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to partially update OffreEmploi : {}", offreEmploiDTO);
        return offreEmploiRepository
            .findById(offreEmploiDTO.getId())
            .map(existing -> {
                offreEmploiMapper.partialUpdate(existing, offreEmploiDTO);
                return existing;
            })
            .map(offreEmploiRepository::save)
            .map(offreEmploiMapper::toDto);
    }

    //  Récupérer une offre par ID
    @Override
    @Transactional(readOnly = true)
    public Optional<OffreEmploiDTO> findOne(Long id) {
        LOG.debug("Request to get OffreEmploi : {}", id);
        return offreEmploiRepository.findById(id).map(offreEmploiMapper::toDto);
    }

    //  Supprimer une offre
    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete OffreEmploi : {}", id);
        offreEmploiRepository.deleteById(id);
    }

    //  Nouvelle méthode : récupérer les offres du recruteur connecté
    @Override
    @Transactional(readOnly = true)
    public List<OffreEmploiDTO> findByRecruteurLogin(String login) {
        LOG.debug("Request to get Offres by Recruteur login : {}", login);
        Optional<Recruteur> recruteurOpt = recruteurRepository.findByUser_Login(login);

        if (recruteurOpt.isEmpty()) {
            LOG.warn("⚠️ Aucun recruteur trouvé pour le login {}", login);
            return Collections.emptyList();
        }

        Long recruteurId = recruteurOpt.get().getId();
        return offreEmploiRepository.findByRecruteurId(recruteurId)
            .stream()
            .map(offreEmploiMapper::toDto)
            .collect(Collectors.toList());
    }

    //  (optionnel) méthode de compatibilité pour findAllWithEagerRelationships si elle existe dans l’interface
    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OffreEmploiDTO> findAllWithEagerRelationships(
        org.springframework.data.domain.Pageable pageable
    ) {
        return offreEmploiRepository.findAll(pageable).map(offreEmploiMapper::toDto);
    }
}
