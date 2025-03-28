package com.antock.enterprise_info_crawler.api.coseller.application;

import com.antock.enterprise_info_crawler.api.coseller.application.dto.BizCsvInfoDto;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.CorpMastCreateDTO;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.request.RegionRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CoSellerService {

    // csv 조회 서비스
    private final CsvService csvService;
    // 법인 등록번호 조회 api 호출
    private final CorpApiService corpApiService;
    // 행정 구역 코드 조회 api 호출
    private final RegionApiService regionApiService;

    /**
     * 법인 데이터 저장 로직
     * @param requestDto
     * @return
     */
    public String saveCoSeller(RegionRequestDto requestDto){

        //City와 disctrict로 csv 파일 읽어오기
        List<BizCsvInfoDto> list = csvService.readBizCsv(requestDto.getCity().name(), requestDto.getDistrict().name());

        return "";
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
        CompletableFuture<String> corpFuture = corpApiService.getCorpRegNo(csvInfo.getBizNo());
        CompletableFuture<String> regionFuture = regionApiService.getRegionCode(csvInfo.getBizAddress());

        return corpFuture.thenCombine(regionFuture, (corpRegNo, regionCd)->{
            if(corpRegNo == null || regionCd == null) {
                log.debug("API fail :: bizNo {}, name:{}", csvInfo.getBizNo(), csvInfo.getBizNm());
                return Optional.empty();
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
