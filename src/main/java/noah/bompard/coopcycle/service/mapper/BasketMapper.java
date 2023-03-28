package noah.bompard.coopcycle.service.mapper;

import noah.bompard.coopcycle.domain.Basket;
import noah.bompard.coopcycle.domain.Order;
import noah.bompard.coopcycle.domain.User;
import noah.bompard.coopcycle.service.dto.BasketDTO;
import noah.bompard.coopcycle.service.dto.OrderDTO;
import noah.bompard.coopcycle.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Basket} and its DTO {@link BasketDTO}.
 */
@Mapper(componentModel = "spring")
public interface BasketMapper extends EntityMapper<BasketDTO, Basket> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "userId")
    BasketDTO toDto(Basket s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);
}
