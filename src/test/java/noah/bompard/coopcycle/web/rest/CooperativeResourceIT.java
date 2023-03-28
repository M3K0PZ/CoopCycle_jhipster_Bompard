package noah.bompard.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import noah.bompard.coopcycle.IntegrationTest;
import noah.bompard.coopcycle.domain.Cooperative;
import noah.bompard.coopcycle.repository.CooperativeRepository;
import noah.bompard.coopcycle.repository.EntityManager;
import noah.bompard.coopcycle.service.CooperativeService;
import noah.bompard.coopcycle.service.dto.CooperativeDTO;
import noah.bompard.coopcycle.service.mapper.CooperativeMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CooperativeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CooperativeResourceIT {

    private static final Long DEFAULT_COOPERATIVE_ID = 1L;
    private static final Long UPDATED_COOPERATIVE_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AREA = "AAAAAAAAAA";
    private static final String UPDATED_AREA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cooperatives";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CooperativeRepository cooperativeRepository;

    @Mock
    private CooperativeRepository cooperativeRepositoryMock;

    @Autowired
    private CooperativeMapper cooperativeMapper;

    @Mock
    private CooperativeService cooperativeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cooperative cooperative;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().cooperativeId(DEFAULT_COOPERATIVE_ID).name(DEFAULT_NAME).area(DEFAULT_AREA);
        return cooperative;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createUpdatedEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().cooperativeId(UPDATED_COOPERATIVE_ID).name(UPDATED_NAME).area(UPDATED_AREA);
        return cooperative;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_cooperative__restaurant").block();
            em.deleteAll(Cooperative.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        cooperative = createEntity(em);
    }

    @Test
    void createCooperative() throws Exception {
        int databaseSizeBeforeCreate = cooperativeRepository.findAll().collectList().block().size();
        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate + 1);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getCooperativeId()).isEqualTo(DEFAULT_COOPERATIVE_ID);
        assertThat(testCooperative.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCooperative.getArea()).isEqualTo(DEFAULT_AREA);
    }

    @Test
    void createCooperativeWithExistingId() throws Exception {
        // Create the Cooperative with an existing ID
        cooperative.setId(1L);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        int databaseSizeBeforeCreate = cooperativeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCooperativeIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = cooperativeRepository.findAll().collectList().block().size();
        // set the field null
        cooperative.setCooperativeId(null);

        // Create the Cooperative, which fails.
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCooperatives() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        // Get all the cooperativeList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(cooperative.getId().intValue()))
            .jsonPath("$.[*].cooperativeId")
            .value(hasItem(DEFAULT_COOPERATIVE_ID.intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].area")
            .value(hasItem(DEFAULT_AREA));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCooperativesWithEagerRelationshipsIsEnabled() {
        when(cooperativeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(cooperativeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCooperativesWithEagerRelationshipsIsNotEnabled() {
        when(cooperativeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(cooperativeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCooperative() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        // Get the cooperative
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cooperative.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cooperative.getId().intValue()))
            .jsonPath("$.cooperativeId")
            .value(is(DEFAULT_COOPERATIVE_ID.intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.area")
            .value(is(DEFAULT_AREA));
    }

    @Test
    void getNonExistingCooperative() {
        // Get the cooperative
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCooperative() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative
        Cooperative updatedCooperative = cooperativeRepository.findById(cooperative.getId()).block();
        updatedCooperative.cooperativeId(UPDATED_COOPERATIVE_ID).name(UPDATED_NAME).area(UPDATED_AREA);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(updatedCooperative);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getCooperativeId()).isEqualTo(UPDATED_COOPERATIVE_ID);
        assertThat(testCooperative.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCooperative.getArea()).isEqualTo(UPDATED_AREA);
    }

    @Test
    void putNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        partialUpdatedCooperative.name(UPDATED_NAME).area(UPDATED_AREA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getCooperativeId()).isEqualTo(DEFAULT_COOPERATIVE_ID);
        assertThat(testCooperative.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCooperative.getArea()).isEqualTo(UPDATED_AREA);
    }

    @Test
    void fullUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        partialUpdatedCooperative.cooperativeId(UPDATED_COOPERATIVE_ID).name(UPDATED_NAME).area(UPDATED_AREA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getCooperativeId()).isEqualTo(UPDATED_COOPERATIVE_ID);
        assertThat(testCooperative.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCooperative.getArea()).isEqualTo(UPDATED_AREA);
    }

    @Test
    void patchNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCooperative() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeDelete = cooperativeRepository.findAll().collectList().block().size();

        // Delete the cooperative
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cooperative.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
