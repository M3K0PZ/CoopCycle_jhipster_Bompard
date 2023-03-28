package noah.bompard.coopcycle.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;
import noah.bompard.coopcycle.domain.enumeration.Disponibility;

/**
 * A DTO for the {@link noah.bompard.coopcycle.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @DecimalMax(value = "999999999")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    private Disponibility disponibility;

    @Size(max = 1000)
    private String description;

    private RestaurantDTO restaurant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Disponibility getDisponibility() {
        return disponibility;
    }

    public void setDisponibility(Disponibility disponibility) {
        this.disponibility = disponibility;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", disponibility='" + getDisponibility() + "'" +
            ", description='" + getDescription() + "'" +
            ", restaurant=" + getRestaurant() +
            "}";
    }
}
