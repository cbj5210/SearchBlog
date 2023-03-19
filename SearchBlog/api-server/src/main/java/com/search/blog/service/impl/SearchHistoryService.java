package com.search.blog.service.impl;

import com.search.blog.entity.SearchHistory;
import com.search.blog.repository.SearchHistoryRepository;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public void create(String query) {

        // 검색 히스토리 생성
        SearchHistory searchHistory = SearchHistory.builder()
                                                   .keyword(query)
                                                   .datetime(ZonedDateTime.now())
                                                   .build();

        searchHistoryRepository.save(searchHistory);
    }
}
