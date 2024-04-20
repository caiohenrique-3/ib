package com.example.services;


import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class PostServiceUnitTests {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ThreadRepository threadRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void createPost() {
        doReturn(Optional.of(new Thread()))
                .when(threadRepository)
                .findById(anyInt());

        postService.createPostAndReturn("T35T!", 1);

        verify(postRepository, times(1))
                .save(any(Post.class));
    }

    @Test
    void createPostReplyAndReturn() {
        doReturn(Optional.of(new Post()))
                .when(postRepository)
                .findById(anyInt());

        postService.createPostReplyAndReturn("T35T!", 1);

        verify(postRepository, times(1))
                .save(any(Post.class));
    }

    @Test
    void deletePostById() {
        postService.deletePostById(1);

        verify(postRepository, times(1))
                .deleteById(1);
    }

    @Test
    void getPostById() {
        postService.getPostById(1);

        verify(postRepository, times(1))
                .findById(1);
    }

    @Test
    void getPostById_returnsNull_IfPostNotFound() {
        assertTrue(postService.getPostById(1).isEmpty());
        assertFalse(postService.getPostById(1).isPresent());
    }

    @Test
    void getAllPostsInThreadById() {
        Thread thread = new Thread();

        when(threadRepository.findById(anyInt()))
                .thenReturn(Optional.of(thread));

        postService.getAllPostsInThreadById(1);

        verify(threadRepository, times(1))
                .findById(1);

        verify(postRepository, times(1))
                .findByParentThread(any(Thread.class));
    }

    @Test
    void getAllPostsInThreadById_throwsException_ifThreadNotFound() {
        assertThrows(RuntimeException.class,
                () -> postService.getAllPostsInThreadById(1));
    }
}