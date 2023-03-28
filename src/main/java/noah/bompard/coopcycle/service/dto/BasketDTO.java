package noah.bompard.coopcycle.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import noah.bompard.coopcycle.domain.enumeration.BasketState;

/**
 * A DTO for the {@link noah.bompard.coopcycle.domain.Basket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BasketDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Long basketId;

    @NotNull(message = "must not be null")
    private BasketState basketState;

    private UserDTO user;

    private OrderDTO order;

    private UserDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBasketId() {
        return basketId;
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
    }

    public BasketState getBasketState() {
        return basketState;
    }

    public void setBasketState(BasketState basketState) {
        this.basketState = basketState;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public UserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(UserDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasketDTO)) {
            return false;
        }

        BasketDTO basketDTO = (BasketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, basketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BasketDTO{" +
            "id=" + getId() +
            ", basketId=" + getBasketId() +
            ", basketState='" + getBasketState() + "'" +
            ", user=" + getUser() +
            ", order=" + getOrder() +
            ", customer=" + getCustomer() +
            "}";
    }
}
