package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TypeContratAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.mapper.TypeContratMapper;
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
 * Integration tests for the {@link TypeContratResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeContratResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-contrats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TypeContratRepository typeContratRepository;

    @Autowired
    private TypeContratMapper typeContratMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeContratMockMvc;

    private TypeContrat typeContrat;

    private TypeContrat insertedTypeContrat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeContrat createEntity() {
        return new TypeContrat().nom(DEFAULT_NOM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeContrat createUpdatedEntity() {
        return new TypeContrat().nom(UPDATED_NOM);
    }

    @BeforeEach
    void initTest() {
        typeContrat = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTypeContrat != null) {
            typeContratRepository.delete(insertedTypeContrat);
            insertedTypeContrat = null;
        }
    }

    @Test
    @Transactional
    void createTypeContrat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);
        var returnedTypeContratDTO = om.readValue(
            restTypeContratMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeContratDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TypeContratDTO.class
        );

        // Validate the TypeContrat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTypeContrat = typeContratMapper.toEntity(returnedTypeContratDTO);
        assertTypeContratUpdatableFieldsEquals(returnedTypeContrat, getPersistedTypeContrat(returnedTypeContrat));

        insertedTypeContrat = returnedTypeContrat;
    }

    @Test
    @Transactional
    void createTypeContratWithExistingId() throws Exception {
        // Create the TypeContrat with an existing ID
        typeContrat.setId(1L);
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeContratDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        typeContrat.setNom(null);

        // Create the TypeContrat, which fails.
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        restTypeContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeContratDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypeContrats() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeContrat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getTypeContrat() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get the typeContrat
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL_ID, typeContrat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeContrat.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getTypeContratsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        Long id = typeContrat.getId();

        defaultTypeContratFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTypeContratFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTypeContratFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTypeContratsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList where nom equals to
        defaultTypeContratFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTypeContratsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList where nom in
        defaultTypeContratFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTypeContratsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList where nom is not null
        defaultTypeContratFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllTypeContratsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList where nom contains
        defaultTypeContratFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllTypeContratsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        // Get all the typeContratList where nom does not contain
        defaultTypeContratFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    private void defaultTypeContratFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTypeContratShouldBeFound(shouldBeFound);
        defaultTypeContratShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTypeContratShouldBeFound(String filter) throws Exception {
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeContrat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));

        // Check, that the count call also returns 1
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTypeContratShouldNotBeFound(String filter) throws Exception {
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTypeContratMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTypeContrat() throws Exception {
        // Get the typeContrat
        restTypeContratMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTypeContrat() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat
        TypeContrat updatedTypeContrat = typeContratRepository.findById(typeContrat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTypeContrat are not directly saved in db
        em.detach(updatedTypeContrat);
        updatedTypeContrat.nom(UPDATED_NOM);
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(updatedTypeContrat);

        restTypeContratMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeContratDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeContratDTO))
            )
            .andExpect(status().isOk());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTypeContratToMatchAllProperties(updatedTypeContrat);
    }

    @Test
    @Transactional
    void putNonExistingTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeContratDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeContratDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeContratDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeContratDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeContratWithPatch() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat using partial update
        TypeContrat partialUpdatedTypeContrat = new TypeContrat();
        partialUpdatedTypeContrat.setId(typeContrat.getId());

        partialUpdatedTypeContrat.nom(UPDATED_NOM);

        restTypeContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeContrat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTypeContrat))
            )
            .andExpect(status().isOk());

        // Validate the TypeContrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeContratUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTypeContrat, typeContrat),
            getPersistedTypeContrat(typeContrat)
        );
    }

    @Test
    @Transactional
    void fullUpdateTypeContratWithPatch() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat using partial update
        TypeContrat partialUpdatedTypeContrat = new TypeContrat();
        partialUpdatedTypeContrat.setId(typeContrat.getId());

        partialUpdatedTypeContrat.nom(UPDATED_NOM);

        restTypeContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeContrat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTypeContrat))
            )
            .andExpect(status().isOk());

        // Validate the TypeContrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeContratUpdatableFieldsEquals(partialUpdatedTypeContrat, getPersistedTypeContrat(partialUpdatedTypeContrat));
    }

    @Test
    @Transactional
    void patchNonExistingTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeContratDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(typeContratDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(typeContratDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeContratMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(typeContratDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeContrat() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.saveAndFlush(typeContrat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the typeContrat
        restTypeContratMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeContrat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return typeContratRepository.count();
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

    protected TypeContrat getPersistedTypeContrat(TypeContrat typeContrat) {
        return typeContratRepository.findById(typeContrat.getId()).orElseThrow();
    }

    protected void assertPersistedTypeContratToMatchAllProperties(TypeContrat expectedTypeContrat) {
        assertTypeContratAllPropertiesEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
    }

    protected void assertPersistedTypeContratToMatchUpdatableProperties(TypeContrat expectedTypeContrat) {
        assertTypeContratAllUpdatablePropertiesEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
    }
}
