package com.example.services;

import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final PostService postService;

    public ThreadService(ThreadRepository threadRepository, PostService postService) {
        this.threadRepository = threadRepository;
        this.postService = postService;
    }

    public Thread createThreadAndReturn(String title, String body) {
        Thread thread = new Thread();
        thread.setTitle(title);
        thread.setInitialPostBody(body);
        thread.setTimestamp(new Date());
        threadRepository.save(thread);
        return thread;
    }

    public void deleteThreadById(int threadId) {
        threadRepository.deleteById(threadId);
    }

    public Optional<Thread> getThreadById(int threadId) {
        return threadRepository.findById(threadId);
    }

    public List<Thread> getAllThreads() {
        Iterable<Thread> source = threadRepository.findAll();
        List<Thread> threads = new ArrayList<>();
        source.forEach(threads::add);
        return threads;
    }
}
