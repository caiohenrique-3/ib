package com.example.repositories;

import com.example.model.Post;
import com.example.model.Thread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadRepository extends CrudRepository<Thread, Integer> {
    @Query("SELECT t FROM Thread t ORDER BY t.timestamp DESC")
    List<Thread> findLatestThreads(Pageable pageable);
}
