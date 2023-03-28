package noah.bompard.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Order.
 */
@Table("jhi_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("order_id")
    private Long orderId;

    @Transient
    private User client;

    @Transient
    @JsonIgnoreProperties(value = { "cooperatives" }, allowSetters = true)
    private Restaurant restaurant;

    @Transient
    private Basket basket;

    @Transient
    private Payment payment;

    @Column("client_id")
    private Long clientId;

    @Column("restaurant_id")
    private Long restaurantId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Order orderId(Long orderId) {
        this.setOrderId(orderId);
        return this;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getClient() {
        return this.client;
    }

    public void setClient(User user) {
        this.client = user;
        this.clientId = user != null ? user.getId() : null;
    }

    public Order client(User user) {
        this.setClient(user);
        return this;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurantId = restaurant != null ? restaurant.getId() : null;
    }

    public Order restaurant(Restaurant restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    public Basket getBasket() {
        return this.basket;
    }

    public void setBasket(Basket basket) {
        if (this.basket != null) {
            this.basket.setOrder(null);
        }
        if (basket != null) {
            basket.setOrder(this);
        }
        this.basket = basket;
    }

    public Order basket(Basket basket) {
        this.setBasket(basket);
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        if (this.payment != null) {
            this.payment.setOrder(null);
        }
        if (payment != null) {
            payment.setOrder(this);
        }
        this.payment = payment;
    }

    public Order payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long user) {
        this.clientId = user;
    }

    public Long getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(Long restaurant) {
        this.restaurantId = restaurant;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", orderId=" + getOrderId() +
            "}";
    }
}
