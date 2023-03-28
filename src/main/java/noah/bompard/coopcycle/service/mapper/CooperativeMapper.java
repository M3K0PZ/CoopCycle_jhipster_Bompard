package noah.bompard.coopcycle.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import noah.bompard.coopcycle.domain.Cooperative;
import noah.bompard.coopcycle.domain.Restaurant;
import noah.bompard.coopcycle.service.dto.CooperativeDTO;
import noah.bompard.coopcycle.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cooperative} and its DTO {@link CooperativeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CooperativeMapper extends EntityMapper<CooperativeDTO, Cooperative> {
    @Mapping(target = "restaurants", source = "restaurants", qualifiedByName = "restaurantIdSet")
    CooperativeDTO toDto(Cooperative s);

    @Mapping(target = "removeRestaurant", ignore = true)
    Cooperative toEntity(CooperativeDTO cooperativeDTO);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);

    @Named("restaurantIdSet")
    default Set<RestaurantDTO> toDtoRestaurantIdSet(Set<Restaurant> restaurant) {
        return restaurant.stream().map(this::toDtoRestaurantId).collect(Collectors.toSet());
    }
}
