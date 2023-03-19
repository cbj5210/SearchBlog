package com.search.blog.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private API api;

    @Getter
    @Setter
    public static final class API {
        private String host;
        private String restApiKey;
        private String searchBlog;
    }
}
