package com.example.repositories;

import com.example.model.Post;
import com.example.model.Thread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findByParentThread(Thread thread);

    @Query("SELECT p FROM Post p ORDER BY p.timestamp DESC")
    List<Post> findLatestPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(:body IS NULL OR LOWER(p.body) LIKE LOWER(CONCAT('%', :body, '%')))")
    List<Post> findByPostBody(@Param("body") String body);
}
