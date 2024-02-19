package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.Optional;

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

    @Test
    void testCreatePostAndReturn() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello guys");

        Post createdPost = postService
                .createPostAndReturn("good morning", 1);

        assertTrue(postService.getPostById(
                createdPost.getPostId()).isPresent());
    }

    @Test
    void deletePostById() {
        threadService.createThreadAndReturn("hello", "hello guys");

        Post createdPost = postService
                .createPostAndReturn("good morning sir!", 1);

        postService
                .deletePostById(createdPost.getPostId());

        assertFalse(postService.getPostById(
                createdPost.getPostId()).isPresent());
    }

    @Test
    void getPostById() {
        Thread t = threadService
                .createThreadAndReturn("hello", "hello test");

        Post p = postService.createPostAndReturn(
                "good morning test!", t.getThreadId());

        assertNotEquals(null, postService
                .getPostById(p.getPostId()));

        assertEquals("good morning test!",
                postService
                        .getPostById(p.getPostId())
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

        postService.createPostAndReturn("good morning!", thread.getThreadId());
        postService.createPostAndReturn("hallo", thread.getThreadId());
        postService.createPostAndReturn("woooow 30", thread.getThreadId());

        List<Post> posts = postService.getAllPostsInThreadById(thread.getThreadId());

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
                .getAllPostsInThreadById(t.getThreadId());

        assertTrue(posts.isEmpty());
    }

    @Test
    void getAllPostsInThreadById_throwsException_ifThreadNotFound() {
        assertThrows(RuntimeException.class,
                () -> postService.getAllPostsInThreadById(-1));
    }
}
