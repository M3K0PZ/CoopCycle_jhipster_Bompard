package noah.bompard.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import noah.bompard.coopcycle.IntegrationTest;
import noah.bompard.coopcycle.domain.Basket;
import noah.bompard.coopcycle.domain.enumeration.BasketState;
import noah.bompard.coopcycle.repository.BasketRepository;
import noah.bompard.coopcycle.repository.EntityManager;
import noah.bompard.coopcycle.service.dto.BasketDTO;
import noah.bompard.coopcycle.service.mapper.BasketMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link BasketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BasketResourceIT {

    private static final Long DEFAULT_BASKET_ID = 1L;
    private static final Long UPDATED_BASKET_ID = 2L;

    private static final BasketState DEFAULT_BASKET_STATE = BasketState.NOTFINISHED;
    private static final BasketState UPDATED_BASKET_STATE = BasketState.VALIDATED;

    private static final String ENTITY_API_URL = "/api/baskets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketMapper basketMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Basket basket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Basket createEntity(EntityManager em) {
        Basket basket = new Basket().basketId(DEFAULT_BASKET_ID).basketState(DEFAULT_BASKET_STATE);
        return basket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Basket createUpdatedEntity(EntityManager em) {
        Basket basket = new Basket().basketId(UPDATED_BASKET_ID).basketState(UPDATED_BASKET_STATE);
        return basket;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Basket.class).block();
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
        basket = createEntity(em);
    }

    @Test
    void createBasket() throws Exception {
        int databaseSizeBeforeCreate = basketRepository.findAll().collectList().block().size();
        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeCreate + 1);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getBasketId()).isEqualTo(DEFAULT_BASKET_ID);
        assertThat(testBasket.getBasketState()).isEqualTo(DEFAULT_BASKET_STATE);
    }

    @Test
    void createBasketWithExistingId() throws Exception {
        // Create the Basket with an existing ID
        basket.setId(1L);
        BasketDTO basketDTO = basketMapper.toDto(basket);

        int databaseSizeBeforeCreate = basketRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkBasketIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketRepository.findAll().collectList().block().size();
        // set the field null
        basket.setBasketId(null);

        // Create the Basket, which fails.
        BasketDTO basketDTO = basketMapper.toDto(basket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkBasketStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketRepository.findAll().collectList().block().size();
        // set the field null
        basket.setBasketState(null);

        // Create the Basket, which fails.
        BasketDTO basketDTO = basketMapper.toDto(basket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBaskets() {
        // Initialize the database
        basketRepository.save(basket).block();

        // Get all the basketList
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
            .value(hasItem(basket.getId().intValue()))
            .jsonPath("$.[*].basketId")
            .value(hasItem(DEFAULT_BASKET_ID.intValue()))
            .jsonPath("$.[*].basketState")
            .value(hasItem(DEFAULT_BASKET_STATE.toString()));
    }

    @Test
    void getBasket() {
        // Initialize the database
        basketRepository.save(basket).block();

        // Get the basket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, basket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(basket.getId().intValue()))
            .jsonPath("$.basketId")
            .value(is(DEFAULT_BASKET_ID.intValue()))
            .jsonPath("$.basketState")
            .value(is(DEFAULT_BASKET_STATE.toString()));
    }

    @Test
    void getNonExistingBasket() {
        // Get the basket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBasket() throws Exception {
        // Initialize the database
        basketRepository.save(basket).block();

        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();

        // Update the basket
        Basket updatedBasket = basketRepository.findById(basket.getId()).block();
        updatedBasket.basketId(UPDATED_BASKET_ID).basketState(UPDATED_BASKET_STATE);
        BasketDTO basketDTO = basketMapper.toDto(updatedBasket);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, basketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getBasketId()).isEqualTo(UPDATED_BASKET_ID);
        assertThat(testBasket.getBasketState()).isEqualTo(UPDATED_BASKET_STATE);
    }

    @Test
    void putNonExistingBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, basketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBasketWithPatch() throws Exception {
        // Initialize the database
        basketRepository.save(basket).block();

        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();

        // Update the basket using partial update
        Basket partialUpdatedBasket = new Basket();
        partialUpdatedBasket.setId(basket.getId());

        partialUpdatedBasket.basketState(UPDATED_BASKET_STATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getBasketId()).isEqualTo(DEFAULT_BASKET_ID);
        assertThat(testBasket.getBasketState()).isEqualTo(UPDATED_BASKET_STATE);
    }

    @Test
    void fullUpdateBasketWithPatch() throws Exception {
        // Initialize the database
        basketRepository.save(basket).block();

        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();

        // Update the basket using partial update
        Basket partialUpdatedBasket = new Basket();
        partialUpdatedBasket.setId(basket.getId());

        partialUpdatedBasket.basketId(UPDATED_BASKET_ID).basketState(UPDATED_BASKET_STATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getBasketId()).isEqualTo(UPDATED_BASKET_ID);
        assertThat(testBasket.getBasketState()).isEqualTo(UPDATED_BASKET_STATE);
    }

    @Test
    void patchNonExistingBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, basketDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().collectList().block().size();
        basket.setId(count.incrementAndGet());

        // Create the Basket
        BasketDTO basketDTO = basketMapper.toDto(basket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBasket() {
        // Initialize the database
        basketRepository.save(basket).block();

        int databaseSizeBeforeDelete = basketRepository.findAll().collectList().block().size();

        // Delete the basket
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, basket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Basket> basketList = basketRepository.findAll().collectList().block();
        assertThat(basketList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
