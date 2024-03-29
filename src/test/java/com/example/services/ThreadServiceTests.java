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

        assertTrue(threadService.getThreadById(
                t.getId()).isPresent());

        assertFalse(threadService.getThreadById(
                t.getId()).isEmpty());
    }

    @Test
    void threadStartsEmpty() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getId());

        assertTrue(posts.isEmpty());
    }

    @Test
    void deleteThreadById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadService.getThreadById(
                t.getId()).isPresent());

        assertFalse(threadService.getThreadById(
                t.getId()).isEmpty());

        threadService.deleteThreadById(t.getId());

        assertFalse(threadService.getThreadById(
                t.getId()).isPresent());
    }

    @Test
    void deletingThreadDeletesRepliesToo() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadService.getThreadById(
                t.getId()).isPresent());

        Post p1 = postService.createPostAndReturn("hi 1", t.getId());
        Post p2 = postService.createPostAndReturn("hi 2", t.getId());
        Post p3 = postService.createPostAndReturn("hi 3", t.getId());

        assertTrue(postService.getPostById(
                p1.getId()).isPresent());
        assertTrue(postService.getPostById(
                p2.getId()).isPresent());
        assertTrue(postService.getPostById(
                p3.getId()).isPresent());

        assertFalse(postService.getPostById(
                p1.getId()).isEmpty());
        assertFalse(postService.getPostById(
                p2.getId()).isEmpty());
        assertFalse(postService.getPostById(
                p3.getId()).isEmpty());

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getId());

        assertFalse(posts.isEmpty());

        threadService.deleteThreadById(t.getId());

        assertFalse(threadService.getThreadById(t.getId()).isPresent());
        assertFalse(postService.getPostById(p1.getId()).isPresent());
        assertFalse(postService.getPostById(p2.getId()).isPresent());
        assertFalse(postService.getPostById(p3.getId()).isPresent());
    }

    @Test
    void getThreadById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!");

        assertTrue(threadService
                .getThreadById(t.getId()).isPresent());

        assertFalse(threadService
                .getThreadById(t.getId()).isEmpty());
    }

    @Test
    void getAllThreads_isNotEmpty_ifTheresExistingThreads() {
        threadService.createThreadAndReturn("hello", "this is a test!");
        threadService.createThreadAndReturn("hello", "this is a test!");
        threadService.createThreadAndReturn("hello", "this is a test!");
        threadService.createThreadAndReturn("hello", "this is a test!");
        List<Thread> threads = threadService.getAllThreads();

        assertFalse(threadService.getAllThreads().isEmpty());
        assertEquals(4, threads.size());
    }

    @Test
    void getAllThreads_isEmpty_ifTheresNoExistingThreads() {
        // cleaning before test
        List<Thread> threads = threadService.getAllThreads();
        for (Thread thread : threads) {
            threadService
                    .deleteThreadById(thread.getId());
        }

        assertTrue(threadService.getAllThreads().isEmpty());
    }
}