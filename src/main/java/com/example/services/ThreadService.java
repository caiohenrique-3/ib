package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThreadService {
    private final ThreadRepository threadRepository;

    public ThreadService(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public void createThread(String title, Post initialPost) {
        Thread thread = new Thread();
        thread.setTitle(title);
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
