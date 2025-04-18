package com.antock.enterprise_info_crawler.config;

import com.antock.enterprise_info_crawler.api.coseller.application.CoSellerService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockConfig {

    @Bean
    public CoSellerService coSellerService() {
        return Mockito.mock(CoSellerService.class);
    }

}
