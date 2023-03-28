package noah.bompard.coopcycle.service.impl;

import noah.bompard.coopcycle.domain.Basket;
import noah.bompard.coopcycle.repository.BasketRepository;
import noah.bompard.coopcycle.service.BasketService;
import noah.bompard.coopcycle.service.dto.BasketDTO;
import noah.bompard.coopcycle.service.mapper.BasketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Basket}.
 */
@Service
@Transactional
public class BasketServiceImpl implements BasketService {

    private final Logger log = LoggerFactory.getLogger(BasketServiceImpl.class);

    private final BasketRepository basketRepository;

    private final BasketMapper basketMapper;

    public BasketServiceImpl(BasketRepository basketRepository, BasketMapper basketMapper) {
        this.basketRepository = basketRepository;
        this.basketMapper = basketMapper;
    }

    @Override
    public Mono<BasketDTO> save(BasketDTO basketDTO) {
        log.debug("Request to save Basket : {}", basketDTO);
        return basketRepository.save(basketMapper.toEntity(basketDTO)).map(basketMapper::toDto);
    }

    @Override
    public Mono<BasketDTO> update(BasketDTO basketDTO) {
        log.debug("Request to update Basket : {}", basketDTO);
        return basketRepository.save(basketMapper.toEntity(basketDTO)).map(basketMapper::toDto);
    }

    @Override
    public Mono<BasketDTO> partialUpdate(BasketDTO basketDTO) {
        log.debug("Request to partially update Basket : {}", basketDTO);

        return basketRepository
            .findById(basketDTO.getId())
            .map(existingBasket -> {
                basketMapper.partialUpdate(existingBasket, basketDTO);

                return existingBasket;
            })
            .flatMap(basketRepository::save)
            .map(basketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BasketDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Baskets");
        return basketRepository.findAllBy(pageable).map(basketMapper::toDto);
    }

    public Mono<Long> countAll() {
        return basketRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BasketDTO> findOne(Long id) {
        log.debug("Request to get Basket : {}", id);
        return basketRepository.findById(id).map(basketMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Basket : {}", id);
        return basketRepository.deleteById(id);
    }
}
