package noah.bompard.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Restaurant.
 */
@Table("restaurant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("restaurant_id")
    private Long restaurantId;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 100)
    @Column("name")
    private String name;

    @Size(max = 1000)
    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "restaurants" }, allowSetters = true)
    private Set<Cooperative> cooperatives = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Restaurant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return this.restaurantId;
    }

    public Restaurant restaurantId(Long restaurantId) {
        this.setRestaurantId(restaurantId);
        return this;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return this.name;
    }

    public Restaurant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Restaurant description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Cooperative> getCooperatives() {
        return this.cooperatives;
    }

    public void setCooperatives(Set<Cooperative> cooperatives) {
        if (this.cooperatives != null) {
            this.cooperatives.forEach(i -> i.removeRestaurant(this));
        }
        if (cooperatives != null) {
            cooperatives.forEach(i -> i.addRestaurant(this));
        }
        this.cooperatives = cooperatives;
    }

    public Restaurant cooperatives(Set<Cooperative> cooperatives) {
        this.setCooperatives(cooperatives);
        return this;
    }

    public Restaurant addCooperative(Cooperative cooperative) {
        this.cooperatives.add(cooperative);
        cooperative.getRestaurants().add(this);
        return this;
    }

    public Restaurant removeCooperative(Cooperative cooperative) {
        this.cooperatives.remove(cooperative);
        cooperative.getRestaurants().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }
        return id != null && id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + getId() +
            ", restaurantId=" + getRestaurantId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
