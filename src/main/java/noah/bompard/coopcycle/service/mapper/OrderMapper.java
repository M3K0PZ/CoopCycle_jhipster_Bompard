package noah.bompard.coopcycle.service.mapper;

import noah.bompard.coopcycle.domain.Order;
import noah.bompard.coopcycle.domain.Restaurant;
import noah.bompard.coopcycle.domain.User;
import noah.bompard.coopcycle.service.dto.OrderDTO;
import noah.bompard.coopcycle.service.dto.RestaurantDTO;
import noah.bompard.coopcycle.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "client", source = "client", qualifiedByName = "userId")
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    OrderDTO toDto(Order s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
