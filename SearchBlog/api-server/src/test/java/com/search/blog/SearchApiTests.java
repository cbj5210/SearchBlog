package com.search.blog;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.search.blog.model.response.KakaoSearchBlogResponse;
import com.search.blog.model.response.NaverSearchBlogResponse;
import com.search.blog.property.KakaoProperties;
import com.search.blog.property.NaverProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class SearchApiTests {

    @Autowired
    private WebClient webClient;

    @Autowired
    private KakaoProperties kakaoProperties;

    @Autowired
    private NaverProperties naverProperties;

    @Test
    public void kakao() {

        // 카카오 블로그 검색 API 호출 테스트
        KakaoSearchBlogResponse response
            = webClient.mutate()
                       .baseUrl(String.format("https://%s", kakaoProperties.getApi().getHost()))
                       .build()
                       .get()
                       .uri(uri -> uri.path(kakaoProperties.getApi().getSearchBlog())
                                      .queryParam("query", "게임")
                                      .queryParam("sort", "regency")
                                      .queryParam("page", 50)
                                      .queryParam("size", 10)
                                      .build())
                       .header("Authorization",
                               "KakaoAK " + kakaoProperties.getApi().getRestApiKey())
                       .retrieve()
                       .bodyToMono(KakaoSearchBlogResponse.class)
                       .block();

        assertNotNull(response);
    }

    @Test
    public void naver() {

        // 네이버 블로그 검색 API 호출 테스트
        NaverSearchBlogResponse response
            = webClient.mutate()
                       .baseUrl(String.format("https://%s", naverProperties.getApi().getHost()))
                       .build()
                       .get()
                       .uri(uri -> uri.path(naverProperties.getApi().getSearchBlog())
                                      .queryParam("query", "게임")
                                      .queryParam("sort", "sim")
                                      .queryParam("start", 10)
                                      .queryParam("display", 10)
                                      .build())
                       .headers(httpHeaders -> {
                           httpHeaders.set("X-Naver-Client-Id",
                                           naverProperties.getApi().getClientId());
                           httpHeaders.set("X-Naver-Client-Secret",
                                           naverProperties.getApi().getClientKey());
                       })
                       .retrieve()
                       .bodyToMono(NaverSearchBlogResponse.class)
                       .block();

        assertNotNull(response);
    }
}
