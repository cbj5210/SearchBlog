package com.search.blog.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class NaverSearchBlogResponse {

    private int total;
    private int start;
    private int display;
    private List<Item> items;
    private HttpStatus errorStatus;
    private String errorMessage;
    private String errorCode;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        private String title;
        private String link;
        private String description;
        @JsonProperty("bloggername")
        private String bloggerName;
        @JsonProperty("bloggerlink")
        private String bloggerLink;
        @JsonProperty("postdate")
        private String postDate;
    }
}
