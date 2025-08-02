package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CandidatAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Candidat;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CandidatRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.CandidatDTO;
import com.mycompany.myapp.service.mapper.CandidatMapper;
import jakarta.persistence.EntityManager;
import java.util.Base64;
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
 * Integration tests for the {@link CandidatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CandidatResourceIT {

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CV = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CV = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CV_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CV_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/candidats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidatMapper candidatMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCandidatMockMvc;

    private Candidat candidat;

    private Candidat insertedCandidat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createEntity(EntityManager em) {
        Candidat candidat = new Candidat()
            .telephone(DEFAULT_TELEPHONE)
            .adresse(DEFAULT_ADRESSE)
            .cv(DEFAULT_CV)
            .cvContentType(DEFAULT_CV_CONTENT_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        candidat.setUser(user);
        return candidat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createUpdatedEntity(EntityManager em) {
        Candidat updatedCandidat = new Candidat()
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCandidat.setUser(user);
        return updatedCandidat;
    }

    @BeforeEach
    void initTest() {
        candidat = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCandidat != null) {
            candidatRepository.delete(insertedCandidat);
            insertedCandidat = null;
        }
    }

    @Test
    @Transactional
    void createCandidat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);
        var returnedCandidatDTO = om.readValue(
            restCandidatMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CandidatDTO.class
        );

        // Validate the Candidat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCandidat = candidatMapper.toEntity(returnedCandidatDTO);
        assertCandidatUpdatableFieldsEquals(returnedCandidat, getPersistedCandidat(returnedCandidat));

        insertedCandidat = returnedCandidat;
    }

    @Test
    @Transactional
    void createCandidatWithExistingId() throws Exception {
        // Create the Candidat with an existing ID
        candidat.setId(1L);
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandidatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCandidats() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidat.getId().intValue())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].cvContentType").value(hasItem(DEFAULT_CV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cv").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CV))));
    }

    @Test
    @Transactional
    void getCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get the candidat
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL_ID, candidat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(candidat.getId().intValue()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.cvContentType").value(DEFAULT_CV_CONTENT_TYPE))
            .andExpect(jsonPath("$.cv").value(Base64.getEncoder().encodeToString(DEFAULT_CV)));
    }

    @Test
    @Transactional
    void getCandidatsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        Long id = candidat.getId();

        defaultCandidatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCandidatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCandidatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCandidatsByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where telephone equals to
        defaultCandidatFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllCandidatsByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where telephone in
        defaultCandidatFiltering("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE, "telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllCandidatsByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where telephone is not null
        defaultCandidatFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidatsByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where telephone contains
        defaultCandidatFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllCandidatsByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where telephone does not contain
        defaultCandidatFiltering("telephone.doesNotContain=" + UPDATED_TELEPHONE, "telephone.doesNotContain=" + DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllCandidatsByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where adresse equals to
        defaultCandidatFiltering("adresse.equals=" + DEFAULT_ADRESSE, "adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllCandidatsByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where adresse in
        defaultCandidatFiltering("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE, "adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllCandidatsByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where adresse is not null
        defaultCandidatFiltering("adresse.specified=true", "adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidatsByAdresseContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where adresse contains
        defaultCandidatFiltering("adresse.contains=" + DEFAULT_ADRESSE, "adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllCandidatsByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where adresse does not contain
        defaultCandidatFiltering("adresse.doesNotContain=" + UPDATED_ADRESSE, "adresse.doesNotContain=" + DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void getAllCandidatsByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = candidat.getUser();
        candidatRepository.saveAndFlush(candidat);
        Long userId = user.getId();
        // Get all the candidatList where user equals to userId
        defaultCandidatShouldBeFound("userId.equals=" + userId);

        // Get all the candidatList where user equals to (userId + 1)
        defaultCandidatShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultCandidatFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCandidatShouldBeFound(shouldBeFound);
        defaultCandidatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCandidatShouldBeFound(String filter) throws Exception {
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidat.getId().intValue())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].cvContentType").value(hasItem(DEFAULT_CV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cv").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CV))));

        // Check, that the count call also returns 1
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCandidatShouldNotBeFound(String filter) throws Exception {
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCandidat() throws Exception {
        // Get the candidat
        restCandidatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat
        Candidat updatedCandidat = candidatRepository.findById(candidat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCandidat are not directly saved in db
        em.detach(updatedCandidat);
        updatedCandidat.telephone(UPDATED_TELEPHONE).adresse(UPDATED_ADRESSE).cv(UPDATED_CV).cvContentType(UPDATED_CV_CONTENT_TYPE);
        CandidatDTO candidatDTO = candidatMapper.toDto(updatedCandidat);

        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCandidatToMatchAllProperties(updatedCandidat);
    }

    @Test
    @Transactional
    void putNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        partialUpdatedCandidat.cv(UPDATED_CV).cvContentType(UPDATED_CV_CONTENT_TYPE);

        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidat))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCandidat, candidat), getPersistedCandidat(candidat));
    }

    @Test
    @Transactional
    void fullUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        partialUpdatedCandidat.telephone(UPDATED_TELEPHONE).adresse(UPDATED_ADRESSE).cv(UPDATED_CV).cvContentType(UPDATED_CV_CONTENT_TYPE);

        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidat))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(partialUpdatedCandidat, getPersistedCandidat(partialUpdatedCandidat));
    }

    @Test
    @Transactional
    void patchNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, candidatDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the candidat
        restCandidatMockMvc
            .perform(delete(ENTITY_API_URL_ID, candidat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return candidatRepository.count();
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

    protected Candidat getPersistedCandidat(Candidat candidat) {
        return candidatRepository.findById(candidat.getId()).orElseThrow();
    }

    protected void assertPersistedCandidatToMatchAllProperties(Candidat expectedCandidat) {
        assertCandidatAllPropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }

    protected void assertPersistedCandidatToMatchUpdatableProperties(Candidat expectedCandidat) {
        assertCandidatAllUpdatablePropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }
}
