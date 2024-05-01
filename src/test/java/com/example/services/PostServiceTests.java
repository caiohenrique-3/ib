package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostServiceTests {
    @Autowired
    private PostService postService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadRepository threadRepository;

    @BeforeEach
    void cleanDatabase() {
        threadRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void testCreatePostAndReturn() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello guys");

        Post createdPost = postService
                .createPostAndReturn("good morning", t.getId());

        assertTrue(postService.getPostById(
                createdPost.getId()).isPresent());
    }

    @Test
    void testCreatePostReplyAndReturn() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello guys");

        Post createdPost = postService
                .createPostAndReturn("good morning", t.getId());

        Post createdReply = postService
                .createPostReplyAndReturn("hi", createdPost.getId());

        assertTrue(postService.getPostById(
                createdPost.getId()).isPresent());

        assertTrue(postService.getPostById(
                createdReply.getId()).isPresent());

        assertEquals(createdPost.getId(),
                createdReply.getParentPost().getId());
    }

    @Test
    void deletePostById() {
        Thread t = threadService.createThreadAndReturn("hello", "hello guys");

        Post createdPost = postService
                .createPostAndReturn("good morning sir!", t.getId());

        postService
                .deletePostById(createdPost.getId());

        assertFalse(postService.getPostById(
                createdPost.getId()).isPresent());
    }

    @Test
    void getPostById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello test");

        Post p = postService.createPostAndReturn(
                "good morning test!", t.getId());

        assertNotEquals(null, postService
                .getPostById(p.getId()));

        assertEquals("good morning test!",
                postService
                        .getPostById(p.getId())
                        .get()
                        .getBody());
    }

    @Test
    void getPostById_returnsNull_ifNotFound() {
        assertEquals(Optional.empty(),
                postService.getPostById(-1));

        assertTrue(postService.getPostById(-1).isEmpty());

        assertFalse(postService.getPostById(-1).isPresent());
    }

    @Test
    void getAllPostsInThreadById() {
        Thread thread = threadService
                .createThreadAndReturn("hello", "hello b");

        postService.createPostAndReturn("good morning!", thread.getId());
        postService.createPostAndReturn("hallo", thread.getId());
        postService.createPostAndReturn("woooow 30", thread.getId());

        List<Post> posts = postService.getAllPostsInThreadById(thread.getId());

        assertFalse(posts.isEmpty());

        assertEquals("good morning!",
                posts.get(0).getBody());

        assertEquals("hallo",
                posts.get(1).getBody());

        assertEquals("woooow 30",
                posts.get(2).getBody());
    }

    @Test
    void getAllPostsInThreadById_returnsEmpty_ifNoRepliesToThread() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello saars");

        List<Post> posts = postService
                .getAllPostsInThreadById(t.getId());

        assertTrue(posts.isEmpty());
    }

    @Test
    void getAllPostsInThreadById_throwsException_ifThreadNotFound() {
        assertThrows(RuntimeException.class,
                () -> postService.getAllPostsInThreadById(-1));
    }

    @Test
    void getTotalNumberOfPosts_1() {
        Thread t = threadService.createThreadAndReturn("test", "test");
        Post p1 = postService.createPostAndReturn("test", t.getId());
        Post p2 = postService.createPostReplyAndReturn("test", p1.getId());
        assertEquals(2L, postService.getTotalNumberOfPosts());
    }

    @Test
    void getTotalNumberOfPosts_2() {
        assertEquals(0L, threadService.getTotalNumberOfThreads());
    }

    @Test
    @Disabled
    void getTimeSinceLastPost_1() throws Exception {
        Thread t = threadService.createThreadAndReturn("test", "test");

        Post p = postService.createPostAndReturn("Test", t.getId());

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
                seconds + " seconds - post + " + p.getId() +
                " on thread " + t.getId();

        // Call the method and get the actual output
        String actualOutput = postService.getTimeSinceLastPost();

        // Compare the actual output with the expected output
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getTimeSinceLastPost_2() {
        assertEquals("No posts found",
                postService.getTimeSinceLastPost());
    }
}
