package com.search.blog.controller;

import com.search.blog.model._enum.SortOption;
import com.search.blog.model.response.SearchBlogResponse;
import com.search.blog.service.SearchService;
import com.search.blog.service.impl.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/search")
public class SearchController {

    private final SearchService kakaoSearchService;
    private final SearchService naverSearchService;
    private final SearchHistoryService searchHistoryService;

    /*
     * 블로그 검색 API, data src 우선 순위는 kakao > naver
     */
    @GetMapping("/blog")
    public ResponseEntity<SearchBlogResponse> searchBlog(@RequestParam String query,
                                                         @RequestParam(required = false, defaultValue = "ACCURACY") SortOption sort,
                                                         @RequestParam(required = false, defaultValue = "1") int page,
                                                         @RequestParam(required = false, defaultValue = "10") Integer size) {

        // 카카오 Search API 호출 (응답 결과 캐싱)
        SearchBlogResponse kakaoResponse = kakaoSearchService.searchBlog(query, sort, page, size);

        // 카카오 API 결과에 에러가 없으면 정상 리턴
        if (kakaoResponse.getError() == null) {
            searchHistoryService.create(query);
            return new ResponseEntity<>(kakaoResponse, HttpStatus.OK);
        }

        // 카카오 장애시 네이버 API 대체 호출 (응답 결과 캐싱)
        SearchBlogResponse naverResponse = naverSearchService.searchBlog(query, sort, page, size);

        // 네이버 API 결과에 에러가 없으면 리턴
        if (naverResponse.getError() == null) {
            searchHistoryService.create(query);
            return new ResponseEntity<>(naverResponse, HttpStatus.OK);
        }

        // 네이버 응답에도 이슈가 있으면 카카오 에러를 리턴
        return new ResponseEntity<>(kakaoResponse, HttpStatus.valueOf(kakaoResponse.getError().getCode()));
    }
}
