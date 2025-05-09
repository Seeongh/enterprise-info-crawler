package com.antock.enterprise_info_crawler.api.coseller.presentation;

import com.antock.enterprise_info_crawler.api.coseller.application.CoSellerService;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.request.RegionRequestDto;
import com.antock.enterprise_info_crawler.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coseller")
public class CoSellerRestController {

    private final CoSellerService cosellerService;

    @PostMapping("/save")
    public ApiResponse<Integer> saveCoSeller(@Valid @RequestBody RegionRequestDto regionRequestDto) {
        log.debug("지역 요청 수신 : {}" , regionRequestDto.toString());

        return ApiResponse.of(HttpStatus.OK, cosellerService.saveCoSeller(regionRequestDto));
    }
}
