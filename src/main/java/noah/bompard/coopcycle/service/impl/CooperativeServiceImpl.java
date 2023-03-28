package noah.bompard.coopcycle.service.impl;

import noah.bompard.coopcycle.domain.Cooperative;
import noah.bompard.coopcycle.repository.CooperativeRepository;
import noah.bompard.coopcycle.service.CooperativeService;
import noah.bompard.coopcycle.service.dto.CooperativeDTO;
import noah.bompard.coopcycle.service.mapper.CooperativeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Cooperative}.
 */
@Service
@Transactional
public class CooperativeServiceImpl implements CooperativeService {

    private final Logger log = LoggerFactory.getLogger(CooperativeServiceImpl.class);

    private final CooperativeRepository cooperativeRepository;

    private final CooperativeMapper cooperativeMapper;

    public CooperativeServiceImpl(CooperativeRepository cooperativeRepository, CooperativeMapper cooperativeMapper) {
        this.cooperativeRepository = cooperativeRepository;
        this.cooperativeMapper = cooperativeMapper;
    }

    @Override
    public Mono<CooperativeDTO> save(CooperativeDTO cooperativeDTO) {
        log.debug("Request to save Cooperative : {}", cooperativeDTO);
        return cooperativeRepository.save(cooperativeMapper.toEntity(cooperativeDTO)).map(cooperativeMapper::toDto);
    }

    @Override
    public Mono<CooperativeDTO> update(CooperativeDTO cooperativeDTO) {
        log.debug("Request to update Cooperative : {}", cooperativeDTO);
        return cooperativeRepository.save(cooperativeMapper.toEntity(cooperativeDTO)).map(cooperativeMapper::toDto);
    }

    @Override
    public Mono<CooperativeDTO> partialUpdate(CooperativeDTO cooperativeDTO) {
        log.debug("Request to partially update Cooperative : {}", cooperativeDTO);

        return cooperativeRepository
            .findById(cooperativeDTO.getId())
            .map(existingCooperative -> {
                cooperativeMapper.partialUpdate(existingCooperative, cooperativeDTO);

                return existingCooperative;
            })
            .flatMap(cooperativeRepository::save)
            .map(cooperativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CooperativeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Cooperatives");
        return cooperativeRepository.findAllBy(pageable).map(cooperativeMapper::toDto);
    }

    public Flux<CooperativeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cooperativeRepository.findAllWithEagerRelationships(pageable).map(cooperativeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cooperativeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CooperativeDTO> findOne(Long id) {
        log.debug("Request to get Cooperative : {}", id);
        return cooperativeRepository.findOneWithEagerRelationships(id).map(cooperativeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Cooperative : {}", id);
        return cooperativeRepository.deleteById(id);
    }
}
