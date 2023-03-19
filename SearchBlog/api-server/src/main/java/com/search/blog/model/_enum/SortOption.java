package com.search.blog.model._enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortOption {
    ACCURACY("accuracy", "sim"),
    RECENCY("recency", "date");

    private final String kakao;
    private final String naver;
}
