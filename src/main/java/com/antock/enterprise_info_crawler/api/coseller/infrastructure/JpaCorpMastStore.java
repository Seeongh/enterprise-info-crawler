package com.antock.enterprise_info_crawler.api.coseller.infrastructure;

import com.antock.enterprise_info_crawler.api.coseller.application.dto.CorpMastCreateDTO;
import com.antock.enterprise_info_crawler.api.coseller.domain.CorpMast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 저장소 확장성을 고려한 JPA 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JpaCorpMastStore implements CorpMastStore{

    private final CorpMastRepository corpMastRepository;

    @Override
    public void save(CorpMast corpMast) {
        corpMastRepository.save(corpMast);
    }

    @Override
    public void saveAll(List<CorpMast> entityList) {

        corpMastRepository.saveAll(entityList);
    }

    @Override
    public boolean existsByBizNo(String bizNo) {
        return corpMastRepository.existsByBizNo(bizNo);
    }

    @Override
    public Optional<CorpMast> findByBizNo(String bizNo) {
        return corpMastRepository.findByBizNo(bizNo);
    }
}
