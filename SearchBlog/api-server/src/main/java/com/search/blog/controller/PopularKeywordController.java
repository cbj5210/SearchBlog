package com.search.blog.controller;

import com.search.blog.model.response.PopularKeywordResponse;
import com.search.blog.service.impl.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/popular")
public class PopularKeywordController {

    private final PopularKeywordService popularKeywordService;

    /*
    * (최대) 10개 상위 인기 검색어 노출
    */
    @GetMapping("/keyword")
    public ResponseEntity<PopularKeywordResponse> popularKeyword() {

        return new ResponseEntity<>(popularKeywordService.getPopularKeywords(), HttpStatus.OK);
    }
}
