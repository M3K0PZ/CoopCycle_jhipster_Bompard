package noah.bompard.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import noah.bompard.coopcycle.domain.enumeration.BasketState;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Basket.
 */
@Table("basket")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("basket_id")
    private Long basketId;

    @NotNull(message = "must not be null")
    @Column("basket_state")
    private BasketState basketState;

    @Transient
    private User user;

    @Transient
    private Order order;

    @Transient
    private User customer;

    @Column("user_id")
    private Long userId;

    @Column("order_id")
    private Long orderId;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Basket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBasketId() {
        return this.basketId;
    }

    public Basket basketId(Long basketId) {
        this.setBasketId(basketId);
        return this;
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
    }

    public BasketState getBasketState() {
        return this.basketState;
    }

    public Basket basketState(BasketState basketState) {
        this.setBasketState(basketState);
        return this;
    }

    public void setBasketState(BasketState basketState) {
        this.basketState = basketState;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Basket user(User user) {
        this.setUser(user);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Basket order(Order order) {
        this.setOrder(order);
        return this;
    }

    public User getCustomer() {
        return this.customer;
    }

    public void setCustomer(User user) {
        this.customer = user;
        this.customerId = user != null ? user.getId() : null;
    }

    public Basket customer(User user) {
        this.setCustomer(user);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long user) {
        this.customerId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Basket)) {
            return false;
        }
        return id != null && id.equals(((Basket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Basket{" +
            "id=" + getId() +
            ", basketId=" + getBasketId() +
            ", basketState='" + getBasketState() + "'" +
            "}";
    }
}
