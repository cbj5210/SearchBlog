package com.search.blog.tasklet;

import com.search.blog.entity.SearchCount;
import com.search.blog.entity.SearchHistory;
import com.search.blog.repository.SearchCountRepository;
import com.search.blog.repository.SearchHistoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordTasklet {

    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchCountRepository searchCountRepository;

    // 주기적으로 search_history에 적재된 검색 기록 데이터를 집계하여 search_count 테이블에 반영
    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void PopularKeywordCollect() {

        List<SearchHistory> searchHistoryList = searchHistoryRepository.findAll();

        searchHistoryList
            .stream()
            .collect(Collectors.groupingBy(SearchHistory::getKeyword, Collectors.counting()))
            .forEach((keyword, count) -> {
                SearchCount searchCount
                    = searchCountRepository.findById(keyword)
                                           .map(data -> {
                                               // 기존에 집계된 데이터가 있으면 조회수를 더함
                                               data.setCount(data.getCount() + count.intValue());

                                               return data;
                                           })
                                           .orElse(new SearchCount(keyword, count.intValue()));

                searchCountRepository.save(searchCount);
            });

        //처리가 된 History는 재처리 되지 않도록 제거
        searchHistoryRepository.deleteAll(searchHistoryList);
    }
}
