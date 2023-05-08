package at.jku.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Auction.
 */
@Entity
@Table(name = "auction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Auction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "auction_description")
    private String auctionDescription;

    @OneToMany(mappedBy = "offerName")
    @JsonIgnoreProperties(value = { "offerName" }, allowSetters = true)
    private Set<Offer> auctionNames = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Auction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuctionDescription() {
        return this.auctionDescription;
    }

    public Auction auctionDescription(String auctionDescription) {
        this.setAuctionDescription(auctionDescription);
        return this;
    }

    public void setAuctionDescription(String auctionDescription) {
        this.auctionDescription = auctionDescription;
    }

    public Set<Offer> getAuctionNames() {
        return this.auctionNames;
    }

    public void setAuctionNames(Set<Offer> offers) {
        if (this.auctionNames != null) {
            this.auctionNames.forEach(i -> i.setOfferName(null));
        }
        if (offers != null) {
            offers.forEach(i -> i.setOfferName(this));
        }
        this.auctionNames = offers;
    }

    public Auction auctionNames(Set<Offer> offers) {
        this.setAuctionNames(offers);
        return this;
    }

    public Auction addAuctionName(Offer offer) {
        this.auctionNames.add(offer);
        offer.setOfferName(this);
        return this;
    }

    public Auction removeAuctionName(Offer offer) {
        this.auctionNames.remove(offer);
        offer.setOfferName(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Auction)) {
            return false;
        }
        return id != null && id.equals(((Auction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Auction{" +
            "id=" + getId() +
            ", auctionDescription='" + getAuctionDescription() + "'" +
            "}";
    }
}
