package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO: Change all direct calls to repository classes
//  to use the methods available in the service classes instead.

@SpringBootTest
public class ThreadServiceTests {
    @Autowired
    private ThreadService threadService;

    @Autowired
    private PostService postService;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void testCreateThreadWithInitialPost() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadRepository
                .findById(t.getThreadId()).isPresent());

        assertFalse(threadRepository
                .findById(t.getThreadId()).isEmpty());
    }

    @Test
    void threadStartsEmpty() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getThreadId());

        assertTrue(posts.isEmpty());
    }

    @Test
    public void deleteThreadById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadRepository
                .findById(t.getThreadId()).isPresent());

        assertFalse(threadRepository
                .findById(t.getThreadId()).isEmpty());

        threadService.deleteThreadById(t.getThreadId());

        assertFalse(threadRepository
                .findById(t.getThreadId()).isPresent());
    }

    @Test
    public void deletingThreadDeletesRepliesToo() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadRepository
                .findById(t.getThreadId()).isPresent());

        Post p1 = postService.createPostAndReturn("hi 1", t.getThreadId());
        Post p2 = postService.createPostAndReturn("hi 2", t.getThreadId());
        Post p3 = postService.createPostAndReturn("hi 3", t.getThreadId());

        assertTrue(postRepository
                .findById(p1.getPostId()).isPresent());
        assertTrue(postRepository
                .findById(p2.getPostId()).isPresent());
        assertTrue(postRepository
                .findById(p3.getPostId()).isPresent());

        assertFalse(postRepository
                .findById(p1.getPostId()).isEmpty());
        assertFalse(postRepository
                .findById(p2.getPostId()).isEmpty());
        assertFalse(postRepository
                .findById(p3.getPostId()).isEmpty());

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getThreadId());

        assertFalse(posts.isEmpty());

        threadService.deleteThreadById(t.getThreadId());

        assertFalse(threadRepository.findById(t.getThreadId()).isPresent());
        assertFalse(postRepository.findById(p1.getPostId()).isPresent());
        assertFalse(postRepository.findById(p2.getPostId()).isPresent());
        assertFalse(postRepository.findById(p3.getPostId()).isPresent());
    }

    @Test
    public void getThreadById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadService
                .getThreadById(t.getThreadId()).isPresent());

        assertFalse(threadService
                .getThreadById(t.getThreadId()).isEmpty());
    }
}