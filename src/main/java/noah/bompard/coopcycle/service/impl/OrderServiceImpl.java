package noah.bompard.coopcycle.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import noah.bompard.coopcycle.domain.Order;
import noah.bompard.coopcycle.repository.OrderRepository;
import noah.bompard.coopcycle.service.OrderService;
import noah.bompard.coopcycle.service.dto.OrderDTO;
import noah.bompard.coopcycle.service.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Mono<OrderDTO> save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        return orderRepository.save(orderMapper.toEntity(orderDTO)).map(orderMapper::toDto);
    }

    @Override
    public Mono<OrderDTO> update(OrderDTO orderDTO) {
        log.debug("Request to update Order : {}", orderDTO);
        return orderRepository.save(orderMapper.toEntity(orderDTO)).map(orderMapper::toDto);
    }

    @Override
    public Mono<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .flatMap(orderRepository::save)
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAllBy(pageable).map(orderMapper::toDto);
    }

    /**
     *  Get all the orders where Basket is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findAllWhereBasketIsNull() {
        log.debug("Request to get all orders where Basket is null");
        return orderRepository.findAllWhereBasketIsNull().map(orderMapper::toDto);
    }

    /**
     *  Get all the orders where Payment is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findAllWherePaymentIsNull() {
        log.debug("Request to get all orders where Payment is null");
        return orderRepository.findAllWherePaymentIsNull().map(orderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        return orderRepository.deleteById(id);
    }
}
