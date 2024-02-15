package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

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
    void createThread() {
        threadService.createThread("good morning sirs");

        verify(threadRepository, times(1))
                .save(any(Thread.class));
    }

    @Test
    void addInitialPostToThread() {
        Thread thread = new Thread();

        when(threadRepository.findById(anyInt()))
                .thenReturn(Optional.of(thread));

        threadService.addInitialPostToThread(1, "gachi");

        verify(threadRepository, times(1))
                .save(eq(thread));

        verify(postService, times(1))
                .createPostAndReturn(eq("gachi"), eq(thread));
    }

    @Test
    void addInitialPostToThread_throwsException_whenThreadNotFound() {
        assertThrows(RuntimeException.class, () ->
                threadService
                        .addInitialPostToThread(anyInt(), eq("gachi")));
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
