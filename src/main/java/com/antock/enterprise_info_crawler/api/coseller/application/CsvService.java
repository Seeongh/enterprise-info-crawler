package com.antock.enterprise_info_crawler.api.coseller.application;

import com.antock.enterprise_info_crawler.api.coseller.application.dto.BizCsvInfoDto;
import com.antock.enterprise_info_crawler.common.constants.CsvConstants;
import com.antock.enterprise_info_crawler.common.exception.CsvParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@Service
public class CsvService {

    @Value("${csv.file-template}")
    private String fileTemplate; // application.yml에서 설정한 파일 경로 템플릿
    public List<BizCsvInfoDto> readBizCsv(String city, String district) {
        String fileName = String.format(fileTemplate, city, district); //city_district.csv
        ClassPathResource resource = new ClassPathResource("csvFiles/" + fileName);

        try (
                BufferedReader br = getReaderFromDefault(resource);
        ) {
            return br.lines()
                    .skip(1)
                    .map(line -> line.split(",", -1)) //csv라인을 읽어서 String으로 넘김
                    .filter(this::isBiz)
                    .map(this::parseCsvData)
                    .toList();


        } catch (IOException e) {
            throw new CsvParsingException(HttpStatus.INTERNAL_SERVER_ERROR, "CSV파일 읽어오기에 실패했습니다.");
        }

    }
    /**
     * 요청받은 city, district 로 데이터가 없는경우 "서울특별시_강남구 csv동작"
     * @param resource
     * @return
     * @throws IOException
     */
    private BufferedReader getReaderFromDefault(ClassPathResource resource) throws IOException {
        try {
            return new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR")));
        } catch (IOException e) {
            log.debug("요청한 CSV 파일 없음. 기본 파일로 대체. 요청 파일: {}", resource.getFilename());
            log.info("요청한 CSV 파일 없음. 기본 파일로 대체.");
            String defaultFileName = String.format(fileTemplate, CsvConstants.DEFAULT_CITY, CsvConstants.DEFAULT_DISTRICT);
            ClassPathResource defaultResource = new ClassPathResource("csvFiles/" + defaultFileName);
            return new BufferedReader(new InputStreamReader(defaultResource.getInputStream(), Charset.forName("EUC-KR")));
        }
    }


    /**
     * "법인여부" 가 "법인" 인지 필터링
     * @param tokens
     * @return
     */
    private boolean isBiz(String[] tokens) {
        return tokens.length > 4 && CsvConstants.CORP_TYPE_BIZ.equals(tokens[4].trim());
    }

    /**
     *
     * @param tokens
     * @return
     */
    private BizCsvInfoDto parseCsvData(String[] tokens) {

        return BizCsvInfoDto.builder()
                .sellerId(tokens[0].trim())
                .bizNm(tokens[2].trim())
                .bizNo(tokens[3].trim())
                .bizType(tokens[4].trim())
                .bizAddress(tokens[9].trim())
                .bizNesAddress(tokens[10].trim())
                .build();
    }

}
