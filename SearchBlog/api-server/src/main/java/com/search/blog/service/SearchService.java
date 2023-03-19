package com.search.blog.service;

import com.search.blog.model._enum.SortOption;
import com.search.blog.model.response.SearchBlogResponse;

public interface SearchService {

    SearchBlogResponse searchBlog(String query, SortOption sort, int page, int size);
}
