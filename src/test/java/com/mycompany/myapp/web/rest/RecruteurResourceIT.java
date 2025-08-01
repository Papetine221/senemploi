package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.RecruteurAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.RecruteurRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.mapper.RecruteurMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RecruteurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecruteurResourceIT {

    private static final String DEFAULT_NOM_ENTREPRISE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_ENTREPRISE = "BBBBBBBBBB";

    private static final String DEFAULT_SECTEUR = "AAAAAAAAAA";
    private static final String UPDATED_SECTEUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recruteurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruteurMapper recruteurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecruteurMockMvc;

    private Recruteur recruteur;

    private Recruteur insertedRecruteur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recruteur createEntity() {
        return new Recruteur().nomEntreprise(DEFAULT_NOM_ENTREPRISE).secteur(DEFAULT_SECTEUR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recruteur createUpdatedEntity() {
        return new Recruteur().nomEntreprise(UPDATED_NOM_ENTREPRISE).secteur(UPDATED_SECTEUR);
    }

    @BeforeEach
    void initTest() {
        recruteur = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRecruteur != null) {
            recruteurRepository.delete(insertedRecruteur);
            insertedRecruteur = null;
        }
    }

    @Test
    @Transactional
    void createRecruteur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);
        var returnedRecruteurDTO = om.readValue(
            restRecruteurMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recruteurDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RecruteurDTO.class
        );

        // Validate the Recruteur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRecruteur = recruteurMapper.toEntity(returnedRecruteurDTO);
        assertRecruteurUpdatableFieldsEquals(returnedRecruteur, getPersistedRecruteur(returnedRecruteur));

        insertedRecruteur = returnedRecruteur;
    }

    @Test
    @Transactional
    void createRecruteurWithExistingId() throws Exception {
        // Create the Recruteur with an existing ID
        recruteur.setId(1L);
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecruteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recruteurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomEntrepriseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        recruteur.setNomEntreprise(null);

        // Create the Recruteur, which fails.
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        restRecruteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recruteurDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecruteurs() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recruteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomEntreprise").value(hasItem(DEFAULT_NOM_ENTREPRISE)))
            .andExpect(jsonPath("$.[*].secteur").value(hasItem(DEFAULT_SECTEUR)));
    }

    @Test
    @Transactional
    void getRecruteur() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get the recruteur
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL_ID, recruteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recruteur.getId().intValue()))
            .andExpect(jsonPath("$.nomEntreprise").value(DEFAULT_NOM_ENTREPRISE))
            .andExpect(jsonPath("$.secteur").value(DEFAULT_SECTEUR));
    }

    @Test
    @Transactional
    void getRecruteursByIdFiltering() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        Long id = recruteur.getId();

        defaultRecruteurFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRecruteurFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRecruteurFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRecruteursByNomEntrepriseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where nomEntreprise equals to
        defaultRecruteurFiltering("nomEntreprise.equals=" + DEFAULT_NOM_ENTREPRISE, "nomEntreprise.equals=" + UPDATED_NOM_ENTREPRISE);
    }

    @Test
    @Transactional
    void getAllRecruteursByNomEntrepriseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where nomEntreprise in
        defaultRecruteurFiltering(
            "nomEntreprise.in=" + DEFAULT_NOM_ENTREPRISE + "," + UPDATED_NOM_ENTREPRISE,
            "nomEntreprise.in=" + UPDATED_NOM_ENTREPRISE
        );
    }

    @Test
    @Transactional
    void getAllRecruteursByNomEntrepriseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where nomEntreprise is not null
        defaultRecruteurFiltering("nomEntreprise.specified=true", "nomEntreprise.specified=false");
    }

    @Test
    @Transactional
    void getAllRecruteursByNomEntrepriseContainsSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where nomEntreprise contains
        defaultRecruteurFiltering("nomEntreprise.contains=" + DEFAULT_NOM_ENTREPRISE, "nomEntreprise.contains=" + UPDATED_NOM_ENTREPRISE);
    }

    @Test
    @Transactional
    void getAllRecruteursByNomEntrepriseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where nomEntreprise does not contain
        defaultRecruteurFiltering(
            "nomEntreprise.doesNotContain=" + UPDATED_NOM_ENTREPRISE,
            "nomEntreprise.doesNotContain=" + DEFAULT_NOM_ENTREPRISE
        );
    }

    @Test
    @Transactional
    void getAllRecruteursBySecteurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where secteur equals to
        defaultRecruteurFiltering("secteur.equals=" + DEFAULT_SECTEUR, "secteur.equals=" + UPDATED_SECTEUR);
    }

    @Test
    @Transactional
    void getAllRecruteursBySecteurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where secteur in
        defaultRecruteurFiltering("secteur.in=" + DEFAULT_SECTEUR + "," + UPDATED_SECTEUR, "secteur.in=" + UPDATED_SECTEUR);
    }

    @Test
    @Transactional
    void getAllRecruteursBySecteurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where secteur is not null
        defaultRecruteurFiltering("secteur.specified=true", "secteur.specified=false");
    }

    @Test
    @Transactional
    void getAllRecruteursBySecteurContainsSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where secteur contains
        defaultRecruteurFiltering("secteur.contains=" + DEFAULT_SECTEUR, "secteur.contains=" + UPDATED_SECTEUR);
    }

    @Test
    @Transactional
    void getAllRecruteursBySecteurNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        // Get all the recruteurList where secteur does not contain
        defaultRecruteurFiltering("secteur.doesNotContain=" + UPDATED_SECTEUR, "secteur.doesNotContain=" + DEFAULT_SECTEUR);
    }

    @Test
    @Transactional
    void getAllRecruteursByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            recruteurRepository.saveAndFlush(recruteur);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        recruteur.setUser(user);
        recruteurRepository.saveAndFlush(recruteur);
        Long userId = user.getId();
        // Get all the recruteurList where user equals to userId
        defaultRecruteurShouldBeFound("userId.equals=" + userId);

        // Get all the recruteurList where user equals to (userId + 1)
        defaultRecruteurShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultRecruteurFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRecruteurShouldBeFound(shouldBeFound);
        defaultRecruteurShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecruteurShouldBeFound(String filter) throws Exception {
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recruteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomEntreprise").value(hasItem(DEFAULT_NOM_ENTREPRISE)))
            .andExpect(jsonPath("$.[*].secteur").value(hasItem(DEFAULT_SECTEUR)));

        // Check, that the count call also returns 1
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecruteurShouldNotBeFound(String filter) throws Exception {
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecruteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRecruteur() throws Exception {
        // Get the recruteur
        restRecruteurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecruteur() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recruteur
        Recruteur updatedRecruteur = recruteurRepository.findById(recruteur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRecruteur are not directly saved in db
        em.detach(updatedRecruteur);
        updatedRecruteur.nomEntreprise(UPDATED_NOM_ENTREPRISE).secteur(UPDATED_SECTEUR);
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(updatedRecruteur);

        restRecruteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recruteurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(recruteurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRecruteurToMatchAllProperties(updatedRecruteur);
    }

    @Test
    @Transactional
    void putNonExistingRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recruteurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(recruteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(recruteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recruteurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecruteurWithPatch() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recruteur using partial update
        Recruteur partialUpdatedRecruteur = new Recruteur();
        partialUpdatedRecruteur.setId(recruteur.getId());

        partialUpdatedRecruteur.nomEntreprise(UPDATED_NOM_ENTREPRISE);

        restRecruteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecruteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRecruteur))
            )
            .andExpect(status().isOk());

        // Validate the Recruteur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecruteurUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRecruteur, recruteur),
            getPersistedRecruteur(recruteur)
        );
    }

    @Test
    @Transactional
    void fullUpdateRecruteurWithPatch() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recruteur using partial update
        Recruteur partialUpdatedRecruteur = new Recruteur();
        partialUpdatedRecruteur.setId(recruteur.getId());

        partialUpdatedRecruteur.nomEntreprise(UPDATED_NOM_ENTREPRISE).secteur(UPDATED_SECTEUR);

        restRecruteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecruteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRecruteur))
            )
            .andExpect(status().isOk());

        // Validate the Recruteur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecruteurUpdatableFieldsEquals(partialUpdatedRecruteur, getPersistedRecruteur(partialUpdatedRecruteur));
    }

    @Test
    @Transactional
    void patchNonExistingRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recruteurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(recruteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(recruteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecruteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recruteur.setId(longCount.incrementAndGet());

        // Create the Recruteur
        RecruteurDTO recruteurDTO = recruteurMapper.toDto(recruteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecruteurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(recruteurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recruteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecruteur() throws Exception {
        // Initialize the database
        insertedRecruteur = recruteurRepository.saveAndFlush(recruteur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the recruteur
        restRecruteurMockMvc
            .perform(delete(ENTITY_API_URL_ID, recruteur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return recruteurRepository.count();
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

    protected Recruteur getPersistedRecruteur(Recruteur recruteur) {
        return recruteurRepository.findById(recruteur.getId()).orElseThrow();
    }

    protected void assertPersistedRecruteurToMatchAllProperties(Recruteur expectedRecruteur) {
        assertRecruteurAllPropertiesEquals(expectedRecruteur, getPersistedRecruteur(expectedRecruteur));
    }

    protected void assertPersistedRecruteurToMatchUpdatableProperties(Recruteur expectedRecruteur) {
        assertRecruteurAllUpdatablePropertiesEquals(expectedRecruteur, getPersistedRecruteur(expectedRecruteur));
    }
}
