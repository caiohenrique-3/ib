package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void cleanDatabase() {
        threadRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void testCreateThreadWithInitialPost() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!", null);

        assertTrue(threadService.getThreadById(
                t.getId()).isPresent());

        assertFalse(threadService.getThreadById(
                t.getId()).isEmpty());
    }

    @Test
    void threadStartsEmpty() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!", null);

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getId());

        assertTrue(posts.isEmpty());
    }

    @Test
    void deleteThreadById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "this is a test!", null);

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
                .createThreadAndReturn("hello", "this is a test!", null);

        assertTrue(threadService.getThreadById(
                t.getId()).isPresent());

        Post p1 = postService.createPostAndReturn("hi 1", t.getId(), null);
        Post p2 = postService.createPostAndReturn("hi 2", t.getId(), null);
        Post p3 = postService.createPostAndReturn("hi 3", t.getId(), null);

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
                .createThreadAndReturn("hello", "this is a test!", null);

        assertTrue(threadService
                .getThreadById(t.getId()).isPresent());

        assertFalse(threadService
                .getThreadById(t.getId()).isEmpty());
    }

    @Test
    void getAllThreads_isNotEmpty_ifTheresExistingThreads() {
        threadService.createThreadAndReturn("hello", "this is a test!", null);
        threadService.createThreadAndReturn("hello", "this is a test!", null);
        threadService.createThreadAndReturn("hello", "this is a test!", null);
        threadService.createThreadAndReturn("hello", "this is a test!", null);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Thread> threads = threadService.getAllThreads(pageable);

        assertFalse(threadService.getAllThreads(pageable).isEmpty());
        assertEquals(4L, threads.getTotalElements());
    }

    @Test
    void getAllThreads_isEmpty_ifTheresNoExistingThreads() {
        Pageable pageable = PageRequest.of(0, 20);
        assertTrue(threadService.getAllThreads(pageable).isEmpty());
    }

    @Test
    void getTotalNumberOfThreads_1() {
        threadService.createThreadAndReturn("test", "test", null);
        threadService.createThreadAndReturn("test", "test", null);
        threadService.createThreadAndReturn("test", "test", null);
        threadService.createThreadAndReturn("test", "test", null);
        assertEquals(4L, threadService.getTotalNumberOfThreads());
    }

    @Test
    void getTotalNumberOfThreads_2() {
        assertEquals(0L, threadService.getTotalNumberOfThreads());
    }

    @Test
    @Disabled
        // Broken test
    void getTimeSinceLastThread_1() throws Exception {
        Thread t = threadService.createThreadAndReturn("Test", "test", null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

        // May 1, 2024, at 15:44:32
        Date specificDate = sdf.parse("2024.05.01.15.44.32");
        Date now = new Date();

        t.setTimestamp(specificDate);

        // Calculate the expected time difference
        long diffInMillies = Math.abs(now.getTime() - specificDate.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        diffInMillies -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillies);
        diffInMillies -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
        diffInMillies -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillies);

        // Format the expected output
        String expectedOutput = days + " days, " + hours +
                " hours, " + minutes + " minutes, " +
                seconds + " seconds - thread " + t.getId();

        // Call the method and get the actual output
        String actualOutput = threadService.getTimeSinceLastThread();

        // Compare the actual output with the expected output
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getTimeSinceLastThread_2() {
        assertEquals("No threads found",
                threadService.getTimeSinceLastThread());
    }

    @Test
    void lockThreadById() {
        Thread t = threadService
                .createThreadAndReturn("test03", "this is a test!", null);

        threadService.lockThreadById(t.getId());

        assertTrue(threadRepository
                .findById(t.getId()).get().isLocked());
    }

    @Test
    void unlockThreadById() {
        Thread t = threadService
                .createThreadAndReturn("test03", "this is a test!", null);

        threadService.lockThreadById(t.getId());

        assertTrue(threadRepository
                .findById(t.getId()).get().isLocked());

        threadService.unlockThreadById(t.getId());

        assertFalse(threadRepository
                .findById(t.getId()).get().isLocked());
    }
}