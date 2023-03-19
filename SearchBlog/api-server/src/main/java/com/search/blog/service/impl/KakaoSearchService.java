package com.search.blog.service.impl;

import com.search.blog.model._enum.DataSource;
import com.search.blog.model._enum.SortOption;
import com.search.blog.model.response.KakaoSearchBlogResponse;
import com.search.blog.model.response.SearchBlogResponse;
import com.search.blog.model.response.SearchBlogResponse.Blog;
import com.search.blog.model.response.SearchBlogResponse.Meta;
import com.search.blog.property.KakaoProperties;
import com.search.blog.service.SearchService;
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
public class KakaoSearchService implements SearchService {

    private final WebClient webClient;
    private final KakaoProperties kakaoProperties;

    @Override
    @Cacheable(cacheNames = "kakaoApiResponse") // 캐시 타임 3초
    public SearchBlogResponse searchBlog(String query, SortOption sort, int page, int size) {

        // 카카오 블로그 검색 API 호출
        KakaoSearchBlogResponse response
            = webClient.mutate()
                       .baseUrl(String.format("https://%s", kakaoProperties.getApi().getHost()))
                       .build()
                       .get()
                       .uri(uri -> uri.path(kakaoProperties.getApi().getSearchBlog())
                                      .queryParam("query", query)
                                      .queryParam("sort", sort.getKakao())
                                      .queryParam("page", page)
                                      .queryParam("size", size)
                                      .build())
                       .header("Authorization",
                               "KakaoAK " + kakaoProperties.getApi().getRestApiKey())
                       .exchangeToMono(apiResponse -> {

                           // 에러 응답이 내려온 경우에만 errorCode 값 셋팅
                           if (apiResponse.statusCode().isError()) {
                               return apiResponse.bodyToMono(KakaoSearchBlogResponse.class)
                                                 .map(
                                                     responseBody -> responseBody.withErrorCode(
                                                         apiResponse.statusCode()));
                           }

                           return apiResponse.bodyToMono(KakaoSearchBlogResponse.class);
                       })
                       .onErrorResume(
                           throwable -> {
                               log.error("[KakaoSearchService] API Error : ", throwable);

                               return Mono.just(
                                   KakaoSearchBlogResponse.builder()
                                                          .errorCode(
                                                              HttpStatus.INTERNAL_SERVER_ERROR)
                                                          .message(throwable.getMessage())
                                                          .build());
                           })
                       .block();

        if (response == null) {

            return SearchBlogResponse.builder()
                                     .error(makeError(HttpStatus.INTERNAL_SERVER_ERROR,
                                                      StringUtils.EMPTY,
                                                      "response is null"))
                                     .build();
        }

        // 에러 응답인 경우
        if (response.getErrorCode() != null) {

            return SearchBlogResponse.builder()
                                     .error(makeError(response.getErrorCode(),
                                                      response.getErrorType(),
                                                      response.getMessage()))
                                     .build();
        }

        // 정상 응답 처리 (API Response)
        Meta meta = Meta.builder()
                        .dataSource(DataSource.KAKAO.name())
                        .requestPageNumber(page)
                        .requestPageSize(size)
                        .totalBlogCount(response.getMeta().getPageableCount())
                        .isEnd(response.getMeta().isEnd())
                        .build();

        List<Blog> blogs = response.getDocuments()
                                   .stream()
                                   .map(document -> Blog.builder()
                                                        .name(document.getBlogName())
                                                        .contentTitle(document.getTitle())
                                                        .contents(document.getContents())
                                                        .contentUrl(document.getUrl())
                                                        .thumbnail(document.getThumbnail())
                                                        .dateTime(document.getDateTime())
                                                        .build())
                                   .toList();

        return SearchBlogResponse.builder()
                                 .meta(meta)
                                 .blogs(blogs)
                                 .build();
    }

    private SearchBlogResponse.Error makeError(HttpStatus status, String errorType,
                                               String message) {

        return SearchBlogResponse.Error.builder()
                                       .code(status.value())
                                       .message(
                                           String.format("errorType : %s, message : %s",
                                                         errorType, message))
                                       .build();
    }
}
