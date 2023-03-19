package com.search.blog.service.impl;

import com.search.blog.model._enum.DataSource;
import com.search.blog.model._enum.SortOption;
import com.search.blog.model.response.NaverSearchBlogResponse;
import com.search.blog.model.response.SearchBlogResponse;
import com.search.blog.model.response.SearchBlogResponse.Blog;
import com.search.blog.model.response.SearchBlogResponse.Meta;
import com.search.blog.property.NaverProperties;
import com.search.blog.service.SearchService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverSearchService implements SearchService {

    private final WebClient webClient;
    private final NaverProperties naverProperties;

    @Override
    @Cacheable(cacheNames = "naverApiResponse") // 캐시 타임 3초
    public SearchBlogResponse searchBlog(String query, SortOption sort, int page, int size) {

        // 네이버는 전체 결과의 몇번째 항목(start)부터 몇 개(size)를 가져올지 파라미터로 설정
        int start = (page - 1) * size + 1;

        // 네이버 블로그 검색 API 호출
        NaverSearchBlogResponse response
            = webClient.mutate()
                       .baseUrl(String.format("https://%s", naverProperties.getApi().getHost()))
                       .build()
                       .get()
                       .uri(uri -> uri.path(naverProperties.getApi().getSearchBlog())
                                      .queryParam("query", query)
                                      .queryParam("sort", sort.getNaver())
                                      .queryParam("start", start)
                                      .queryParam("display", size)
                                      .build())
                       .headers(httpHeaders -> {
                           httpHeaders.set("X-Naver-Client-Id",
                                           naverProperties.getApi().getClientId());
                           httpHeaders.set("X-Naver-Client-Secret",
                                           naverProperties.getApi().getClientKey());
                       })
                       .exchangeToMono(apiResponse -> {

                           // 에러 응답이 내려온 경우에만 errorStatus 값 셋팅
                           if (apiResponse.statusCode().isError()) {
                               return apiResponse.bodyToMono(NaverSearchBlogResponse.class)
                                                 .map(
                                                     responseBody -> responseBody.withErrorStatus(
                                                         apiResponse.statusCode()));
                           }

                           return apiResponse.bodyToMono(NaverSearchBlogResponse.class);
                       })
                       .onErrorResume(
                           throwable -> {
                               log.error("[NaverSearchService] API Error : ", throwable);

                               return Mono.just(
                                   NaverSearchBlogResponse.builder()
                                                          .errorStatus(
                                                              HttpStatus.INTERNAL_SERVER_ERROR)
                                                          .errorMessage(throwable.getMessage())
                                                          .build());
                           }
                       )
                       .block();

        if (response == null) {

            return SearchBlogResponse.builder()
                                     .error(makeError(HttpStatus.INTERNAL_SERVER_ERROR,
                                                      StringUtils.EMPTY,
                                                      "response is null"))
                                     .build();
        }

        // 에러 응답인 경우
        if (response.getErrorStatus() != null) {

            return SearchBlogResponse.builder()
                                     .error(makeError(response.getErrorStatus(),
                                                      response.getErrorCode(),
                                                      response.getErrorMessage()))
                                     .build();
        }

        // 정상 응답 처리 (API Response)

        // 요청한 것보다 작은 사이즈의 응답이 오거나,
        // 마지막 페이지를 요청한 경우 ex : start 42, display 10, total 51
        boolean isEnd = (response.getDisplay() < size) ||
            (response.getStart() + response.getDisplay() - 1 == response.getTotal());

        Meta meta = Meta.builder()
                        .dataSource(DataSource.NAVER.name())
                        .requestPageNumber(page)
                        .requestPageSize(size)
                        .totalBlogCount(response.getTotal())
                        .isEnd(isEnd)
                        .build();

        List<Blog> blogs = response.getItems()
                                   .stream()
                                   .map(item -> Blog.builder()
                                                    .name(item.getBloggerName())
                                                    .contentTitle(item.getTitle())
                                                    .contents(item.getDescription())
                                                    .contentUrl(item.getLink())
                                                    .dateTime(
                                                        LocalDate.parse(item.getPostDate(),
                                                                        DateTimeFormatter.ofPattern(
                                                                            "yyyyMMdd"))
                                                                 .atStartOfDay(
                                                                     ZoneId.systemDefault())
                                                    )
                                                    .build())
                                   .toList();

        return SearchBlogResponse.builder()
                                 .meta(meta)
                                 .blogs(blogs)
                                 .build();
    }

    private SearchBlogResponse.Error makeError(HttpStatus status, String errorCode,
                                               String errorMessage) {

        return SearchBlogResponse.Error.builder()
                                       .code(status.value())
                                       .message(
                                           String.format("errorCode : %s, message : %s",
                                                         errorCode, errorMessage))
                                       .build();
    }
}
