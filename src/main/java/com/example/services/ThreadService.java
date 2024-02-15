package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final PostService postService;

    public ThreadService(ThreadRepository threadRepository, PostService postService) {
        this.threadRepository = threadRepository;
        this.postService = postService;
    }

    public void createThreadWithInitialPost(String title, String body) {
        Thread thread = new Thread();
        thread.setTitle(title);
        threadRepository.save(thread);

        Post initialPost = postService.createPostAndReturn(body, thread);

        thread.setInitialPost(initialPost);
        threadRepository.save(thread);
    }

    public void deleteThreadById(int threadId) {
        threadRepository.deleteById(threadId);
    }

    public Optional<Thread> getThreadById(int threadId) {
        return threadRepository.findById(threadId);
    }
}
