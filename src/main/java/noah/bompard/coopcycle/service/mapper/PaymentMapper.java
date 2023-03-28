package noah.bompard.coopcycle.service.mapper;

import noah.bompard.coopcycle.domain.Order;
import noah.bompard.coopcycle.domain.Payment;
import noah.bompard.coopcycle.service.dto.OrderDTO;
import noah.bompard.coopcycle.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    PaymentDTO toDto(Payment s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);
}
