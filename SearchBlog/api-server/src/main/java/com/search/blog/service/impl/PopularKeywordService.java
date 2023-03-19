package com.search.blog.service.impl;

import com.search.blog.model.response.PopularKeywordResponse;
import com.search.blog.model.response.PopularKeywordResponse.KeywordCount;
import com.search.blog.repository.SearchCountRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {

    private final SearchCountRepository searchCountRepository;

    @Cacheable(cacheNames = "popularKeyword") // 캐시 타임 1초
    public PopularKeywordResponse getPopularKeywords() {

        //batch-server에서 집계한 인기 검색어 상위 10개 조회
        List<KeywordCount> popularKeywords =
            searchCountRepository.findTop10ByOrderByKeywordDesc()
                                 .stream()
                                 .map(searchCount
                                          -> KeywordCount.builder()
                                                         .keyword(searchCount.getKeyword())
                                                         .count(searchCount.getCount())
                                                         .build())
                                 .sorted(Comparator.comparing(KeywordCount::getCount).reversed()
                                                   .thenComparing(KeywordCount::getKeyword))
                                 .toList();

        return PopularKeywordResponse.builder()
                                     .count(popularKeywords.size())
                                     .popularKeywords(popularKeywords)
                                     .build();
    }
}
