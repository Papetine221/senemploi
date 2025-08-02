package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.OffreEmploiAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Competence;
import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.OffreEmploiService;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OffreEmploiResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OffreEmploiResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_SALAIRE = 0D;
    private static final Double UPDATED_SALAIRE = 1D;
    private static final Double SMALLER_SALAIRE = 0D - 1D;

    private static final Instant DEFAULT_DATE_PUBLICATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PUBLICATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_EXPIRATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EXPIRATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/offre-emplois";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OffreEmploiRepository offreEmploiRepository;

    @Mock
    private OffreEmploiRepository offreEmploiRepositoryMock;

    @Autowired
    private OffreEmploiMapper offreEmploiMapper;

    @Mock
    private OffreEmploiService offreEmploiServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOffreEmploiMockMvc;

    private OffreEmploi offreEmploi;

    private OffreEmploi insertedOffreEmploi;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OffreEmploi createEntity(EntityManager em) {
        OffreEmploi offreEmploi = new OffreEmploi()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .salaire(DEFAULT_SALAIRE)
            .datePublication(DEFAULT_DATE_PUBLICATION)
            .dateExpiration(DEFAULT_DATE_EXPIRATION);
        // Add required entity
        Recruteur recruteur;
        if (TestUtil.findAll(em, Recruteur.class).isEmpty()) {
            recruteur = RecruteurResourceIT.createEntity(em);
            em.persist(recruteur);
            em.flush();
        } else {
            recruteur = TestUtil.findAll(em, Recruteur.class).get(0);
        }
        offreEmploi.setRecruteur(recruteur);
        // Add required entity
        TypeContrat typeContrat;
        if (TestUtil.findAll(em, TypeContrat.class).isEmpty()) {
            typeContrat = TypeContratResourceIT.createEntity();
            em.persist(typeContrat);
            em.flush();
        } else {
            typeContrat = TestUtil.findAll(em, TypeContrat.class).get(0);
        }
        offreEmploi.setTypeContrat(typeContrat);
        // Add required entity
        Localisation localisation;
        if (TestUtil.findAll(em, Localisation.class).isEmpty()) {
            localisation = LocalisationResourceIT.createEntity();
            em.persist(localisation);
            em.flush();
        } else {
            localisation = TestUtil.findAll(em, Localisation.class).get(0);
        }
        offreEmploi.setLocalisation(localisation);
        // Add required entity
        Competence competence;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            competence = CompetenceResourceIT.createEntity();
            em.persist(competence);
            em.flush();
        } else {
            competence = TestUtil.findAll(em, Competence.class).get(0);
        }
        offreEmploi.getCompetences().add(competence);
        return offreEmploi;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OffreEmploi createUpdatedEntity(EntityManager em) {
        OffreEmploi updatedOffreEmploi = new OffreEmploi()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);
        // Add required entity
        Recruteur recruteur;
        if (TestUtil.findAll(em, Recruteur.class).isEmpty()) {
            recruteur = RecruteurResourceIT.createUpdatedEntity(em);
            em.persist(recruteur);
            em.flush();
        } else {
            recruteur = TestUtil.findAll(em, Recruteur.class).get(0);
        }
        updatedOffreEmploi.setRecruteur(recruteur);
        // Add required entity
        TypeContrat typeContrat;
        if (TestUtil.findAll(em, TypeContrat.class).isEmpty()) {
            typeContrat = TypeContratResourceIT.createUpdatedEntity();
            em.persist(typeContrat);
            em.flush();
        } else {
            typeContrat = TestUtil.findAll(em, TypeContrat.class).get(0);
        }
        updatedOffreEmploi.setTypeContrat(typeContrat);
        // Add required entity
        Localisation localisation;
        if (TestUtil.findAll(em, Localisation.class).isEmpty()) {
            localisation = LocalisationResourceIT.createUpdatedEntity();
            em.persist(localisation);
            em.flush();
        } else {
            localisation = TestUtil.findAll(em, Localisation.class).get(0);
        }
        updatedOffreEmploi.setLocalisation(localisation);
        // Add required entity
        Competence competence;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            competence = CompetenceResourceIT.createUpdatedEntity();
            em.persist(competence);
            em.flush();
        } else {
            competence = TestUtil.findAll(em, Competence.class).get(0);
        }
        updatedOffreEmploi.getCompetences().add(competence);
        return updatedOffreEmploi;
    }

    @BeforeEach
    void initTest() {
        offreEmploi = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedOffreEmploi != null) {
            offreEmploiRepository.delete(insertedOffreEmploi);
            insertedOffreEmploi = null;
        }
    }

    @Test
    @Transactional
    void createOffreEmploi() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);
        var returnedOffreEmploiDTO = om.readValue(
            restOffreEmploiMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OffreEmploiDTO.class
        );

        // Validate the OffreEmploi in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOffreEmploi = offreEmploiMapper.toEntity(returnedOffreEmploiDTO);
        assertOffreEmploiUpdatableFieldsEquals(returnedOffreEmploi, getPersistedOffreEmploi(returnedOffreEmploi));

        insertedOffreEmploi = returnedOffreEmploi;
    }

    @Test
    @Transactional
    void createOffreEmploiWithExistingId() throws Exception {
        // Create the OffreEmploi with an existing ID
        offreEmploi.setId(1L);
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOffreEmploiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setTitre(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        restOffreEmploiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatePublicationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setDatePublication(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        restOffreEmploiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateExpirationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setDateExpiration(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        restOffreEmploiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOffreEmplois() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offreEmploi.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].salaire").value(hasItem(DEFAULT_SALAIRE)))
            .andExpect(jsonPath("$.[*].datePublication").value(hasItem(DEFAULT_DATE_PUBLICATION.toString())))
            .andExpect(jsonPath("$.[*].dateExpiration").value(hasItem(DEFAULT_DATE_EXPIRATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOffreEmploisWithEagerRelationshipsIsEnabled() throws Exception {
        when(offreEmploiServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreEmploiMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(offreEmploiServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOffreEmploisWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(offreEmploiServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreEmploiMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(offreEmploiRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOffreEmploi() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get the offreEmploi
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL_ID, offreEmploi.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(offreEmploi.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.salaire").value(DEFAULT_SALAIRE))
            .andExpect(jsonPath("$.datePublication").value(DEFAULT_DATE_PUBLICATION.toString()))
            .andExpect(jsonPath("$.dateExpiration").value(DEFAULT_DATE_EXPIRATION.toString()));
    }

    @Test
    @Transactional
    void getOffreEmploisByIdFiltering() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        Long id = offreEmploi.getId();

        defaultOffreEmploiFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOffreEmploiFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOffreEmploiFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where titre equals to
        defaultOffreEmploiFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where titre in
        defaultOffreEmploiFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where titre is not null
        defaultOffreEmploiFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where titre contains
        defaultOffreEmploiFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where titre does not contain
        defaultOffreEmploiFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire equals to
        defaultOffreEmploiFiltering("salaire.equals=" + DEFAULT_SALAIRE, "salaire.equals=" + UPDATED_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire in
        defaultOffreEmploiFiltering("salaire.in=" + DEFAULT_SALAIRE + "," + UPDATED_SALAIRE, "salaire.in=" + UPDATED_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire is not null
        defaultOffreEmploiFiltering("salaire.specified=true", "salaire.specified=false");
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire is greater than or equal to
        defaultOffreEmploiFiltering("salaire.greaterThanOrEqual=" + DEFAULT_SALAIRE, "salaire.greaterThanOrEqual=" + UPDATED_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire is less than or equal to
        defaultOffreEmploiFiltering("salaire.lessThanOrEqual=" + DEFAULT_SALAIRE, "salaire.lessThanOrEqual=" + SMALLER_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire is less than
        defaultOffreEmploiFiltering("salaire.lessThan=" + UPDATED_SALAIRE, "salaire.lessThan=" + DEFAULT_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisBySalaireIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where salaire is greater than
        defaultOffreEmploiFiltering("salaire.greaterThan=" + SMALLER_SALAIRE, "salaire.greaterThan=" + DEFAULT_SALAIRE);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDatePublicationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where datePublication equals to
        defaultOffreEmploiFiltering(
            "datePublication.equals=" + DEFAULT_DATE_PUBLICATION,
            "datePublication.equals=" + UPDATED_DATE_PUBLICATION
        );
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDatePublicationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where datePublication in
        defaultOffreEmploiFiltering(
            "datePublication.in=" + DEFAULT_DATE_PUBLICATION + "," + UPDATED_DATE_PUBLICATION,
            "datePublication.in=" + UPDATED_DATE_PUBLICATION
        );
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDatePublicationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where datePublication is not null
        defaultOffreEmploiFiltering("datePublication.specified=true", "datePublication.specified=false");
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDateExpirationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where dateExpiration equals to
        defaultOffreEmploiFiltering("dateExpiration.equals=" + DEFAULT_DATE_EXPIRATION, "dateExpiration.equals=" + UPDATED_DATE_EXPIRATION);
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDateExpirationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where dateExpiration in
        defaultOffreEmploiFiltering(
            "dateExpiration.in=" + DEFAULT_DATE_EXPIRATION + "," + UPDATED_DATE_EXPIRATION,
            "dateExpiration.in=" + UPDATED_DATE_EXPIRATION
        );
    }

    @Test
    @Transactional
    void getAllOffreEmploisByDateExpirationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        // Get all the offreEmploiList where dateExpiration is not null
        defaultOffreEmploiFiltering("dateExpiration.specified=true", "dateExpiration.specified=false");
    }

    @Test
    @Transactional
    void getAllOffreEmploisByRecruteurIsEqualToSomething() throws Exception {
        Recruteur recruteur;
        if (TestUtil.findAll(em, Recruteur.class).isEmpty()) {
            offreEmploiRepository.saveAndFlush(offreEmploi);
            recruteur = RecruteurResourceIT.createEntity(em);
        } else {
            recruteur = TestUtil.findAll(em, Recruteur.class).get(0);
        }
        em.persist(recruteur);
        em.flush();
        offreEmploi.setRecruteur(recruteur);
        offreEmploiRepository.saveAndFlush(offreEmploi);
        Long recruteurId = recruteur.getId();
        // Get all the offreEmploiList where recruteur equals to recruteurId
        defaultOffreEmploiShouldBeFound("recruteurId.equals=" + recruteurId);

        // Get all the offreEmploiList where recruteur equals to (recruteurId + 1)
        defaultOffreEmploiShouldNotBeFound("recruteurId.equals=" + (recruteurId + 1));
    }

    @Test
    @Transactional
    void getAllOffreEmploisByTypeContratIsEqualToSomething() throws Exception {
        TypeContrat typeContrat;
        if (TestUtil.findAll(em, TypeContrat.class).isEmpty()) {
            offreEmploiRepository.saveAndFlush(offreEmploi);
            typeContrat = TypeContratResourceIT.createEntity();
        } else {
            typeContrat = TestUtil.findAll(em, TypeContrat.class).get(0);
        }
        em.persist(typeContrat);
        em.flush();
        offreEmploi.setTypeContrat(typeContrat);
        offreEmploiRepository.saveAndFlush(offreEmploi);
        Long typeContratId = typeContrat.getId();
        // Get all the offreEmploiList where typeContrat equals to typeContratId
        defaultOffreEmploiShouldBeFound("typeContratId.equals=" + typeContratId);

        // Get all the offreEmploiList where typeContrat equals to (typeContratId + 1)
        defaultOffreEmploiShouldNotBeFound("typeContratId.equals=" + (typeContratId + 1));
    }

    @Test
    @Transactional
    void getAllOffreEmploisByLocalisationIsEqualToSomething() throws Exception {
        Localisation localisation;
        if (TestUtil.findAll(em, Localisation.class).isEmpty()) {
            offreEmploiRepository.saveAndFlush(offreEmploi);
            localisation = LocalisationResourceIT.createEntity();
        } else {
            localisation = TestUtil.findAll(em, Localisation.class).get(0);
        }
        em.persist(localisation);
        em.flush();
        offreEmploi.setLocalisation(localisation);
        offreEmploiRepository.saveAndFlush(offreEmploi);
        Long localisationId = localisation.getId();
        // Get all the offreEmploiList where localisation equals to localisationId
        defaultOffreEmploiShouldBeFound("localisationId.equals=" + localisationId);

        // Get all the offreEmploiList where localisation equals to (localisationId + 1)
        defaultOffreEmploiShouldNotBeFound("localisationId.equals=" + (localisationId + 1));
    }

    @Test
    @Transactional
    void getAllOffreEmploisByCompetencesIsEqualToSomething() throws Exception {
        Competence competences;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            offreEmploiRepository.saveAndFlush(offreEmploi);
            competences = CompetenceResourceIT.createEntity();
        } else {
            competences = TestUtil.findAll(em, Competence.class).get(0);
        }
        em.persist(competences);
        em.flush();
        offreEmploi.addCompetences(competences);
        offreEmploiRepository.saveAndFlush(offreEmploi);
        Long competencesId = competences.getId();
        // Get all the offreEmploiList where competences equals to competencesId
        defaultOffreEmploiShouldBeFound("competencesId.equals=" + competencesId);

        // Get all the offreEmploiList where competences equals to (competencesId + 1)
        defaultOffreEmploiShouldNotBeFound("competencesId.equals=" + (competencesId + 1));
    }

    private void defaultOffreEmploiFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOffreEmploiShouldBeFound(shouldBeFound);
        defaultOffreEmploiShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOffreEmploiShouldBeFound(String filter) throws Exception {
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offreEmploi.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].salaire").value(hasItem(DEFAULT_SALAIRE)))
            .andExpect(jsonPath("$.[*].datePublication").value(hasItem(DEFAULT_DATE_PUBLICATION.toString())))
            .andExpect(jsonPath("$.[*].dateExpiration").value(hasItem(DEFAULT_DATE_EXPIRATION.toString())));

        // Check, that the count call also returns 1
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOffreEmploiShouldNotBeFound(String filter) throws Exception {
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOffreEmploiMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOffreEmploi() throws Exception {
        // Get the offreEmploi
        restOffreEmploiMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOffreEmploi() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi
        OffreEmploi updatedOffreEmploi = offreEmploiRepository.findById(offreEmploi.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOffreEmploi are not directly saved in db
        em.detach(updatedOffreEmploi);
        updatedOffreEmploi
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(updatedOffreEmploi);

        restOffreEmploiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, offreEmploiDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(offreEmploiDTO))
            )
            .andExpect(status().isOk());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOffreEmploiToMatchAllProperties(updatedOffreEmploi);
    }

    @Test
    @Transactional
    void putNonExistingOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, offreEmploiDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(offreEmploiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(offreEmploiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOffreEmploiWithPatch() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi using partial update
        OffreEmploi partialUpdatedOffreEmploi = new OffreEmploi();
        partialUpdatedOffreEmploi.setId(offreEmploi.getId());

        partialUpdatedOffreEmploi.description(UPDATED_DESCRIPTION).salaire(UPDATED_SALAIRE).dateExpiration(UPDATED_DATE_EXPIRATION);

        restOffreEmploiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffreEmploi.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOffreEmploi))
            )
            .andExpect(status().isOk());

        // Validate the OffreEmploi in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreEmploiUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOffreEmploi, offreEmploi),
            getPersistedOffreEmploi(offreEmploi)
        );
    }

    @Test
    @Transactional
    void fullUpdateOffreEmploiWithPatch() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi using partial update
        OffreEmploi partialUpdatedOffreEmploi = new OffreEmploi();
        partialUpdatedOffreEmploi.setId(offreEmploi.getId());

        partialUpdatedOffreEmploi
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);

        restOffreEmploiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffreEmploi.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOffreEmploi))
            )
            .andExpect(status().isOk());

        // Validate the OffreEmploi in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreEmploiUpdatableFieldsEquals(partialUpdatedOffreEmploi, getPersistedOffreEmploi(partialUpdatedOffreEmploi));
    }

    @Test
    @Transactional
    void patchNonExistingOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, offreEmploiDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(offreEmploiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(offreEmploiDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreEmploiMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(offreEmploiDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOffreEmploi() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.saveAndFlush(offreEmploi);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offreEmploi
        restOffreEmploiMockMvc
            .perform(delete(ENTITY_API_URL_ID, offreEmploi.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offreEmploiRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected OffreEmploi getPersistedOffreEmploi(OffreEmploi offreEmploi) {
        return offreEmploiRepository.findById(offreEmploi.getId()).orElseThrow();
    }

    protected void assertPersistedOffreEmploiToMatchAllProperties(OffreEmploi expectedOffreEmploi) {
        assertOffreEmploiAllPropertiesEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
    }

    protected void assertPersistedOffreEmploiToMatchUpdatableProperties(OffreEmploi expectedOffreEmploi) {
        assertOffreEmploiAllUpdatablePropertiesEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
    }
}
