package com.search.blog.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "naver")
public class NaverProperties {

    private API api;

    @Getter
    @Setter
    public static final class API {
        private String host;
        private String clientId;
        private String clientKey;
        private String searchBlog;
    }
}
