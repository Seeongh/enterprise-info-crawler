package com.antock.enterprise_info_crawler.service;

import com.antock.enterprise_info_crawler.api.coseller.application.RegionApiService;
import com.antock.enterprise_info_crawler.api.coseller.application.dto.properties.RegionApiProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 행정코드조회 API MOCK TEST
 */
@ExtendWith(MockitoExtension.class)
public class RegionApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RegionApiProperties regionApiProperties;

    @InjectMocks
    private RegionApiService apiService;

    private String address = "";
    private URI mockUri;
    private String mockResponseBody ="";

    @BeforeEach
    void setUp() throws Exception {
        address = "서울특별시 강남구 삼성동 130-8 ";
        mockUri = new URI("https://test.api");

        mockResponseBody = """
        {
            "results" : 
            { 
                "common" : 
                {
                    "errorCode":"0",
                    "errorMessage":"정상"
                },
                "juso" : [
                    {
                        "admCd" : "1168010500"
                    }
                ]
            }       
        }        
        """;
    }

    @Test
    @DisplayName("API에서 정상 값을 받았을 경우 동작 확인")
    public void get_RegionCd_from_API() throws Exception {
        //given
        when(regionApiProperties.buildRequestUrlWithAddress(address)).thenReturn(mockUri);
        when(restTemplate.getForEntity(mockUri,String.class)).thenReturn(
                new ResponseEntity<>(mockResponseBody, HttpStatus.OK)
        );
        //when
        CompletableFuture<String> future = apiService.getRegionCode(address);
        String result = future.get(); //1168010500

        //then
        assertThat(result).isEqualTo("1168010500");

     }

     @Test
     @DisplayName("API 타임아웃 예외 발생 시 동작 확인")
     public void exception_RegionCd_from_API() throws Exception {
         //given
         when(regionApiProperties.buildRequestUrlWithAddress(address)).thenReturn(mockUri);
         when(restTemplate.getForEntity(mockUri,String.class))
                 .thenThrow(new ResourceAccessException("Timeout"));

         //when
         String result = apiService.getRegionCode(address).get();
         //then
         assertThat(result).isEqualTo("");

      }
}
