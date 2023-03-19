package com.search.blog.model._enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    KAKAO_API_RESPONSE("kakaoApiResponse", 3, 100),
    NAVER_API_RESPONSE("naverApiResponse", 3, 100),
    POPULAR_KEYWORD("popularKeyword", 1, 1);

    private final String cacheName; // 캐시 이름
    private final int expireAfterWrite; // 만료 시간(초)
    private final int maximumSize; // 최대 갯수
}