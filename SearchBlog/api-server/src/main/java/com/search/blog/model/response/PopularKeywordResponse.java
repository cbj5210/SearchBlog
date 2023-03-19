package com.search.blog.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularKeywordResponse {

    private int count;
    private List<KeywordCount> popularKeywords;

    @Getter
    @Builder
    public static class KeywordCount {

        private String keyword;
        private int count;
    }
}
