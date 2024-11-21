package com.example.repositories;

import com.example.model.Thread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadRepository extends PagingAndSortingRepository<Thread, Integer>, CrudRepository<Thread, Integer> {
    @Query("SELECT t FROM Thread t ORDER BY t.timestamp DESC")
    List<Thread> findLatestThreads(Pageable pageable);

    @Query("SELECT t FROM Thread t WHERE " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:body IS NULL OR LOWER(t.initialPostBody) LIKE LOWER(CONCAT('%', :body, '%')))")
    List<Thread> findByTitleAndBody(@Param("title") String title,
                                    @Param("body") String body);
}
