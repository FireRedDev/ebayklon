package at.jku.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import at.jku.IntegrationTest;
import at.jku.domain.Auction;
import at.jku.repository.AuctionRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AuctionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuctionResourceIT {

    private static final String DEFAULT_AUCTION_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_AUCTION_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/auctions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuctionMockMvc;

    private Auction auction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Auction createEntity(EntityManager em) {
        Auction auction = new Auction().auctionDescription(DEFAULT_AUCTION_DESCRIPTION);
        return auction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Auction createUpdatedEntity(EntityManager em) {
        Auction auction = new Auction().auctionDescription(UPDATED_AUCTION_DESCRIPTION);
        return auction;
    }

    @BeforeEach
    public void initTest() {
        auction = createEntity(em);
    }

    @Test
    @Transactional
    void createAuction() throws Exception {
        int databaseSizeBeforeCreate = auctionRepository.findAll().size();
        // Create the Auction
        restAuctionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auction)))
            .andExpect(status().isCreated());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeCreate + 1);
        Auction testAuction = auctionList.get(auctionList.size() - 1);
        assertThat(testAuction.getAuctionDescription()).isEqualTo(DEFAULT_AUCTION_DESCRIPTION);
    }

    @Test
    @Transactional
    void createAuctionWithExistingId() throws Exception {
        // Create the Auction with an existing ID
        auction.setId(1L);

        int databaseSizeBeforeCreate = auctionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuctionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auction)))
            .andExpect(status().isBadRequest());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAuctions() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        // Get all the auctionList
        restAuctionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auction.getId().intValue())))
            .andExpect(jsonPath("$.[*].auctionDescription").value(hasItem(DEFAULT_AUCTION_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAuction() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        // Get the auction
        restAuctionMockMvc
            .perform(get(ENTITY_API_URL_ID, auction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auction.getId().intValue()))
            .andExpect(jsonPath("$.auctionDescription").value(DEFAULT_AUCTION_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAuction() throws Exception {
        // Get the auction
        restAuctionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuction() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();

        // Update the auction
        Auction updatedAuction = auctionRepository.findById(auction.getId()).get();
        // Disconnect from session so that the updates on updatedAuction are not directly saved in db
        em.detach(updatedAuction);
        updatedAuction.auctionDescription(UPDATED_AUCTION_DESCRIPTION);

        restAuctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAuction))
            )
            .andExpect(status().isOk());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
        Auction testAuction = auctionList.get(auctionList.size() - 1);
        assertThat(testAuction.getAuctionDescription()).isEqualTo(UPDATED_AUCTION_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuctionWithPatch() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();

        // Update the auction using partial update
        Auction partialUpdatedAuction = new Auction();
        partialUpdatedAuction.setId(auction.getId());

        partialUpdatedAuction.auctionDescription(UPDATED_AUCTION_DESCRIPTION);

        restAuctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuction))
            )
            .andExpect(status().isOk());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
        Auction testAuction = auctionList.get(auctionList.size() - 1);
        assertThat(testAuction.getAuctionDescription()).isEqualTo(UPDATED_AUCTION_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAuctionWithPatch() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();

        // Update the auction using partial update
        Auction partialUpdatedAuction = new Auction();
        partialUpdatedAuction.setId(auction.getId());

        partialUpdatedAuction.auctionDescription(UPDATED_AUCTION_DESCRIPTION);

        restAuctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuction))
            )
            .andExpect(status().isOk());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
        Auction testAuction = auctionList.get(auctionList.size() - 1);
        assertThat(testAuction.getAuctionDescription()).isEqualTo(UPDATED_AUCTION_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuction() throws Exception {
        int databaseSizeBeforeUpdate = auctionRepository.findAll().size();
        auction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuctionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(auction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Auction in the database
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuction() throws Exception {
        // Initialize the database
        auctionRepository.saveAndFlush(auction);

        int databaseSizeBeforeDelete = auctionRepository.findAll().size();

        // Delete the auction
        restAuctionMockMvc
            .perform(delete(ENTITY_API_URL_ID, auction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Auction> auctionList = auctionRepository.findAll();
        assertThat(auctionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
