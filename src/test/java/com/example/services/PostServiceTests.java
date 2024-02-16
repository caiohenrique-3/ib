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
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
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
                .createThreadAndReturn("hello", "hello saars");

        Post createdPost = postService
                .createPostAndReturn("good morning saar!", 1);

        assertTrue(postRepository
                .findById(createdPost.getPostId()).isPresent());

        assertTrue(threadRepository
                .findById(t.getThreadId()).isPresent());
    }

    @Test
    void deletePostById() {
        threadService.createThreadAndReturn("hello", "hello saars");

        Post createdPost = postService
                .createPostAndReturn("good morning saar!", 1);

        postService
                .deletePostById(createdPost.getPostId());

        assertFalse(postRepository
                .findById(createdPost.getPostId()).isPresent());
    }

    @Test
    void getPostById() {
        threadService
                .createThreadAndReturn("hello", "hello saars");

        Post createdPost = postService
                .createPostAndReturn("good morning saar!", 1);

        Optional<Post> foundPost = postService.getPostById(1);

        assertNotEquals(null, postRepository
                .findById(1));

        assertEquals("good morning saar!",
                postRepository
                        .findById(1)
                        .get()
                        .getBody());
    }

    @Test
    void getPostById_returnsNull_ifNotFound() {
        Optional<Post> foundPost = postService.getPostById(1);

        assertEquals(Optional.empty(),
                postService.getPostById(1));

        assertTrue(postService.getPostById(1).isEmpty());

        assertFalse(postService.getPostById(1).isPresent());
    }

    @Test
    void getAllPostsInThreadById() {
        Thread thread = threadService
                .createThreadAndReturn("hello", "hello saars");

        postService.createPostAndReturn("good morning saar!", thread.getThreadId());
        postService.createPostAndReturn("hallo", thread.getThreadId());
        postService.createPostAndReturn("woooow 30", thread.getThreadId());

        List<Post> posts = postService.getAllPostsInThreadById(thread.getThreadId());

        assertFalse(posts.isEmpty());

        assertEquals("good morning saar!",
                posts.get(0).getBody());

        assertEquals("hallo",
                posts.get(1).getBody());

        assertEquals("woooow 30",
                posts.get(2).getBody());
    }

    @Test
    void getAllPostsInThreadById_returnsEmpty_ifNoRepliesToThread() {
        threadService
                .createThreadAndReturn("hello", "hello saars");

        List<Post> posts = postService.getAllPostsInThreadById(1);

        assertTrue(posts.isEmpty());
    }

    @Test
    void getAllPostsInThreadById_throwsException_ifThreadNotFound() {
        assertThrows(RuntimeException.class,
                () -> postService.getAllPostsInThreadById(-1));
    }
}
