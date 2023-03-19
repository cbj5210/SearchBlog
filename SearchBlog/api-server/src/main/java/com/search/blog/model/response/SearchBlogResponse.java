package com.search.blog.model.response;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchBlogResponse {

    private Meta meta;
    private List<Blog> blogs;
    private Error error;

    @Getter
    @Builder
    public static class Meta {

        private String dataSource;
        private int requestPageNumber;
        private int requestPageSize;
        private int totalBlogCount;
        private Boolean isEnd;
    }

    @Getter
    @Builder
    public static class Blog {

        private String name;
        private String contentTitle;
        private String contents;
        private String contentUrl;
        private String thumbnail;
        private ZonedDateTime dateTime;
    }

    @Getter
    @Builder
    public static class Error {

        private int code;
        private String message;
    }
}
