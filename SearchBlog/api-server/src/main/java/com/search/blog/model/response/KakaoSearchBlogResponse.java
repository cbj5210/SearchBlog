package com.search.blog.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.http.HttpStatus;

@Getter
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoSearchBlogResponse {

    private Meta meta;
    private List<Document> documents;
    private HttpStatus errorCode;
    private String errorType;
    private String message;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {

        @JsonProperty("total_count")
        private int totalCount;
        @JsonProperty("pageable_count")
        private int pageableCount;
        private boolean isEnd;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {

        @JsonProperty("blogname")
        private String blogName;
        private String title;
        private String contents;
        private String url;
        private String thumbnail;
        @JsonProperty("datetime")
        private ZonedDateTime dateTime;
    }
}
