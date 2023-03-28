package noah.bompard.coopcycle.service.mapper;

import noah.bompard.coopcycle.domain.Product;
import noah.bompard.coopcycle.domain.Restaurant;
import noah.bompard.coopcycle.service.dto.ProductDTO;
import noah.bompard.coopcycle.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    ProductDTO toDto(Product s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
