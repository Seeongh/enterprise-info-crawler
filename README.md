# 통신판매사업자 정보 수집 서비스

##  프로젝트 개요

공정거래위원회에서 제공하는 통신판매사업자 CSV 정보를 기반으로,  **파일을 읽고**, 공공 API를 활용해 추가 정보를 수집 후  **DB에 저장**하는 백엔드 서비스입니다.


##  기술 스택

| 구분 | 내용 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.x |
| DB | H2 (in-memory) |
| 빌드 | Gradle |
| 로깅 | Logback (`logback-spring.xml`) |
| 병렬 처리 | `@Async` + `CompletableFuture` |
| API 통신 | `RestTemplate` |
| 테스트 | JUnit5, AssertJ


---
 해당 프로젝트는 제시된 모든 기능 요구사항을 충족하며,
비즈니스 고려사항 또한 구조적 설계를 통해 유연하게 대응할 수 있도록 구현하였습니다.

## 기능 요구사항 
- 다섯개의 기능 상세 구현 완료
## 비즈니스 고려사항
### API 출처 변경시 확장성 고려
- yml 파일에서 API 설정을 읽어오도록 구현
- API 변경시 yml 파일에서 `url`,`endpoint`,`apiKey`,`queryParams` 변경하여 적용 가능

### 저장소 변경시 확장성 고려 
- 추상화 클래스 `CorpMastStore`를 생성하여 JpaRepository를 래핑한 `JpaCorpMastStore`로 JpaRepository 를 사용하도록 구현

###  병렬 처리
- '@Async'를 활용한 비동기 메서드를 통해 'CompletableFuture' 기반 병렬 API 호출 구현
- 실패한 항목은 로깅 후 skip 처리하여 유연성 확보

### 로깅 전략
- `logback-spring.xml` 설정
  - `INFO` 이상 로그는 콘솔 및 파일에 출력
  - 로그 파일은 날짜별로 분리 & 7일 보관


###  예외 처리

- `CustomException` 기반 공통 예외 클래스 정의
- 상황별 커스텀 예외:
- `CsvParsingException`: CSV 파싱 실패
- `ExternalApiException`: 공공 API 호출 실패
- 모든 예외는 `GlobalExceptionHandler`에서 처리
- Client 요청값은 'Enum'으로 받아 '@ValidEnum' 어노테이션을 추가하여 검증 처리
- 일관된 응답 포맷 반환: `ApiResponse.class` 사용

### 테스트 코드 
- 각 레이어 별 기능 단위 테스트 작성
- 정상 케이스 및 예외 케이스 테스트 포함하여 안정성 검증

---

## 추가 참고 사항
- CSV 를 다운 API가 없어 임의로 생성한 csv파일로 작업 진행(서울특별시_강남구.csv)
- 공통 응답 포맷(`resultCode`,`resultMsg`,`data`)생성
- 데이터 저장 로직은 전체 롤백이 아닌, 일부 실패 허용 구조로 설계되어 트랜잭션은 미사용 및 실패한 건에 대해서는 로깅 후 skip 처리( saveAll실패시 개별 저장됨)

---
## 결과 공유
- 정상 요청 (data: 저장성공한 )
![image](https://github.com/user-attachments/assets/3d7d439c-96c7-4b71-ba35-89378aac5852)
- 비정상 요청
 ![image](https://github.com/user-attachments/assets/6e0ba8d4-a98b-492b-8427-71ccba8363bd)
- 중복 요청시 (추후 업서트 변경 고민)
  ![image](https://github.com/user-attachments/assets/fbf31a89-42ff-44d2-9514-8f04dd1dbc41)



  
