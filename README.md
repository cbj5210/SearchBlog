# 🖥️ 블로그 검색 & 인기 검색어 서비스
* BootJar
  * Executable Jar (api-server.jar, batch-server.jar)
* SearchBlog
  * Source Code (Multi module)
### 1. 블로그 검색 서비스 다운로드 링크 📌
* [api-server.jar](https://github.com/cbj5210/SearchBlog/raw/main/BootJar/api-server.jar)
* [batch-server.jar](https://github.com/cbj5210/SearchBlog/raw/main/BootJar/batch-server.jar)

### 2. 기동 방법
1. (테이블 생성을 위해 반드시!) api-server.jar를 먼저 실행해야 합니다.
   * java -jar api-server.jar
   * Port : 8080
   * h2 테이블을 생성 합니다.
2. batch-server.jar
   * java -jar batch-server.jar
   * Port : 8081
   * 주기적으로 h2 테이블을 읽어 데이터를 집계 합니다.

### 3. 호출 예시
* 블로그 검색 서비스
  * http://localhost:8080/search/blog
    * 호출 파라미터
      * query = {검색어}
      * sort = {ACCURACY 또는 REGENCY, 기본값: ACCURACY}
      * page = 결과 페이지 번호
      * size = 결과 페이지 크기
    * 예시 : http://localhost:8080/search/blog?query=게임&sort=RECENCY&page=5&size=10


* 인기 검색어 목록 서비스
  * http://localhost:8080/popular/keyword
    * 별도의 파라미터는 없습니다.
    * 상위 10개의 인기 검색어 목록을 검색 횟수와 함께 제공 합니다.

### 4. 멀티 모듈 구성 및 주요 기능
* core-module
  * api-server, batch-server에서 공통적으로 사용되는 entity와 repository
* api-server
  * 블로그 검색 서비스, 인기 검색어 목록 서비스 제공
* batch-server
  * 인기 검색어 목록 서비스를 제공하기 위한 데이터 집계 배치

### 5. 서비스 상세 명세
* 블로그 검색 서비스
  * [카카오 블로그 검색 API](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog)로 블로그를 검색 합니다. 
  * 만약 카카오 블로그 검색 API에 장애가 있으면 [네이버 블로그 검색 API](https://developers.naver.com/docs/serviceapi/search/blog/blog.md#블로그-검색-api-레퍼런스) 데이터를 사용 합니다.
  * 대용량 트래픽을 수용하기 위해 응답 데이터를 <u>3초 동안 캐싱</u>하고 있습니다.
  * api-server에서 검색 후 검색 기록을 DB에 insert 하면 batch-server에서 주기적으로 집계하여 인기 검색어 목록으로 활용 합니다.
  * 응답 형태 (카카오, 네이버 공통)
```java
public class SearchBlogResponse {
    
    Meta meta; // 전체 응답 메타
    List<Blog> blogs; // 검색 결과에 해당하는 블로그 정보 리스트
    Error error; // 에러인 경우에만 데이터 셋팅

    class Meta {
        private String dataSource; // KAKAO, NAVER
        private int requestPageNumber;
        private int requestPageSize;
        private int totalBlogCount; // 검색 조건에 해당하는 전체 블로그 수
        private Boolean isEnd; // 현재 페이지가 마지막 결과 페이지인지 여부
    }

    class Blog {
        private String name; // 블로그 이름
        private String contentTitle; // 블로그 제목
        private String contents; // 블로그 내용
        private String contentUrl; // 블로그 URL
        private String thumbnail; // 컨텐츠 썸네일
        private ZonedDateTime dateTime; // 컨텐츠 시각
    }

    class Error {
        private int code;
        private String message;
    }
}
```

* 인기 검색어 목록 서비스
  * batch-server에서 1초 주기로 검색 이력 데이터를 조회하여 검색어별 조회수를 집계합니다.
  * api-server에서 상위 10개 인기 검색어를 제공합니다.
    * 대용량 트래픽을 수용하기 위해 응답 데이터를 <u>1초 동안 캐싱</u>하고 있습니다.
  * 응답 형태

```java
public class PopularKeywordResponse {

    int count; // 인기 검색어 수 (최대 10개)
    List<KeywordCount> popularKeywords; // 인기 검색어 및 검색 횟수 리스트
    
    class KeywordCount {
        private String keyword; // 검색어
        private int count; // 검색 횟수
    }
}
```

# 🌱 Database (h2)
******
## Table 명세
* 검색 이력 테이블
  * api-server에서 블로그를 검색할 때마다 search_history 테이블에 검색 이력을 저장 합니다.
  * batch-server에서 주기적으로 search_history 테이블 데이터를 읽어 search_count 테이블에 집계하고 집계된 데이터는 제거 합니다.

### search_history

| Name   | Type   | comment |
|--------|--------|--------|
| id     | int    | auto increment key |
| keyword | varchar | 검색어 |
| datetime | datetime | 검색 시각 |

### search_count
| Name   | Type   | comment |
|--------|--------|--------|
| keyword | varchar | 검색어 |
| count | int | 검색횟수 |


# ⚙️ 개발 환경
******
* JAVA 17
* Spring Boot 2.7.10-SNAPSHOT
* 외부 라이브러리 & 오픈 소스, 사용 목적
  * Lombok : 어노테이션
  * Apache Commons-lang3 : String 처리

# 💬 Maintainers
******
* Byungjun Choi
* cbj5210@nate.com
