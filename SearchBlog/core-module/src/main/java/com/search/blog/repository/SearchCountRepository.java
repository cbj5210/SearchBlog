package com.search.blog.repository;

import com.search.blog.entity.SearchCount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchCountRepository extends JpaRepository<SearchCount, String> {

    List<SearchCount> findTop10ByOrderByKeywordDesc();
}
