package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class ThreadServiceUnitTests {
    @Mock
    private ThreadRepository threadRepository;

    @InjectMocks
    private ThreadService threadService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void createThread() {
        threadService.createThread("good morning sirs", new Post());

        verify(threadRepository, times(1))
                .save(any(Thread.class));
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
