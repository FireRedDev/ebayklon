package at.jku.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Offer.
 */
@Entity
@Table(name = "offer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "offer_value")
    private Double offerValue;

    @ManyToOne
    @JsonIgnoreProperties(value = { "auctionNames" }, allowSetters = true)
    private Auction offerName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Offer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getOfferValue() {
        return this.offerValue;
    }

    public Offer offerValue(Double offerValue) {
        this.setOfferValue(offerValue);
        return this;
    }

    public void setOfferValue(Double offerValue) {
        this.offerValue = offerValue;
    }

    public Auction getOfferName() {
        return this.offerName;
    }

    public void setOfferName(Auction auction) {
        this.offerName = auction;
    }

    public Offer offerName(Auction auction) {
        this.setOfferName(auction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offer)) {
            return false;
        }
        return id != null && id.equals(((Offer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Offer{" +
            "id=" + getId() +
            ", offerValue=" + getOfferValue() +
            "}";
    }
}
