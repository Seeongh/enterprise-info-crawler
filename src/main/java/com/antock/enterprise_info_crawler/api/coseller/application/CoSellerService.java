package com.antock.enterprise_info_crawler.api.coseller.application;

import com.antock.enterprise_info_crawler.api.coseller.application.client.CorpApiClient;
import com.antock.enterprise_info_crawler.api.coseller.application.client.RegionApiClient;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.BizCsvInfoDto;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.CorpMastCreateDTO;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.request.RegionRequestDto;
import com.antock.enterprise_info_crawler.api.coseller.domain.CorpMast;
import com.antock.enterprise_info_crawler.api.coseller.infrastructure.CorpMastRepository;
import com.antock.enterprise_info_crawler.api.coseller.infrastructure.CorpMastStore;
import com.antock.enterprise_info_crawler.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 법인 판매 데이터 가공을 위한 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoSellerService {

    // csv 조회 서비스
    private final CsvService csvService;
    // 법인 등록번호 조회 api 호출
    private final CorpApiClient corpApiClient;
    // 행정 구역 코드 조회 api 호출
    private final RegionApiClient regionApiClient;
    // Repository
    private final CorpMastStore corpMastStore;

    /**
     * 법인 데이터 저장 로직
     * @param requestDto
     * @return
     */
    public int saveCoSeller(RegionRequestDto requestDto){

        //City와 disctrict로 csv 파일 읽어오기
        List<BizCsvInfoDto> list = csvService.readBizCsv(requestDto.getCity().name(), requestDto.getDistrict().name());

        // 받아온 csv로 API호출하여 법인 등록 코드, 행정 구역 코드 받아오기
        List<CorpMastCreateDTO> corpCreateDtoList = getCorpApiInfo(list);

        //저장
        int savedCnt = saveCorpMastList(corpCreateDtoList);

        return savedCnt;
    }

    //DTO리스트 저장로직
    private int saveCorpMastList(List<CorpMastCreateDTO> corpCreateDtoList) {
        List<CorpMast> entityList = corpCreateDtoList.stream()
                .map(CorpMastCreateDTO::toEntity)
                .toList();

        int savedCount = 0;
        List<String> duplicatedBizNos = Collections.synchronizedList(new ArrayList<>()); //중복되어 저장되지 않은 리스트


        try {
            log.info("saveAll 시작 - 총 {}건", entityList.size());
            corpMastStore.saveAll(entityList);
            savedCount = entityList.size();
            log.info("saveAll 성공 - 저장된 건수: {}", savedCount);
        } catch (DataIntegrityViolationException e) {
            log.warn("saveAll 실패 - 개별 저장으로 fallback");

            for (CorpMast entity : entityList) {
                try {
                    corpMastStore.save(entity);
                    savedCount++;
                } catch (DataIntegrityViolationException dupEx) {
                    duplicatedBizNos.add(entity.getBizNo());
                    log.debug("중복된 bizNo 건 스킵: {}", entity.getBizNo());
                }
            }
        }

        if (!duplicatedBizNos.isEmpty()) {
            log.info("중복으로 저장되지 않은 건수: {}건", duplicatedBizNos.size());
            log.debug("중복된 bizNo 목록: {}", duplicatedBizNos);
        }

        return savedCount;
    }

    /**
     * csv로 부터 api2개를 읽어와 dto를 반환
     * @param csvList
     * @return
     */
    private List<CorpMastCreateDTO> getCorpApiInfo(List<BizCsvInfoDto> csvList){
        List<CompletableFuture< Optional<CorpMastCreateDTO>>> futures = csvList.stream()
                .map(this::processAsync)
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Async
    public CompletableFuture<Optional<CorpMastCreateDTO>> processAsync(BizCsvInfoDto csvInfo) {
        CompletableFuture<String> corpFuture = corpApiClient.getCorpRegNo(csvInfo.getBizNo());
        CompletableFuture<String> regionFuture = regionApiClient.getRegionCode(csvInfo.getBizAddress());

        return corpFuture.thenCombine(regionFuture, (corpRegNo, regionCd)->{
            if(corpRegNo == null && regionCd == null) {
                log.debug("API fail :: bizNo {}, name:{}", csvInfo.getBizNo(), csvInfo.getBizNm());
                return Optional.of(
                        CorpMastCreateDTO.builder()
                                .sellerId(csvInfo.getSellerId())
                                .bizNm(csvInfo.getBizNm())
                                .bizNo(csvInfo.getBizNo())
                                .corpRegNo("")
                                .regionCd("")
                                .build()
                );
            }

            return Optional.of(
                    CorpMastCreateDTO.builder()
                            .sellerId(csvInfo.getSellerId())
                            .bizNm(csvInfo.getBizNm())
                            .bizNo(csvInfo.getBizNo())
                            .corpRegNo(corpRegNo)
                            .regionCd(regionCd)
                            .build()
            );
        });
    }
}
