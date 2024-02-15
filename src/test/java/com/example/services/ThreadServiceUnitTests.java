package com.example.services;

import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ThreadServiceUnitTests {
    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private ThreadService threadService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void createThreadWithInitialPost() {
        threadService.createThreadWithInitialPost("i can take this anymore",
                "my tests all faill!!!!");

        verify(threadRepository, times(2))
                .save(any(Thread.class));

        verify(postService, times(1))
                .createPostAndReturn(anyString(), anyInt());
    }

    @Test
    void deleteThread() {
        threadService.deleteThreadById(anyInt());

        verify(threadRepository, times(1))
                .deleteById(anyInt());
    }

    @Test
    void getThreadById() {
        threadService.getThreadById(anyInt());

        verify(threadRepository, times(1))
                .findById(anyInt());
    }

    @Test
    void getThreadById_returnsNull_ifThreadNotFound() {
        assertTrue(threadService.getThreadById(anyInt()).isEmpty());
        assertFalse(threadService.getThreadById(anyInt()).isPresent());
    }
}
