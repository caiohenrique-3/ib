package com.example.services;

import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.ArrayList;

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
        threadService
                .createThreadAndReturn("i can take this anymore",
                        "my tests all faill!!!!", null);

        verify(threadRepository, times(1))
                .save(any(Thread.class));
    }

    @Test
    void deleteThread() {
        threadService.deleteThreadById(1);

        verify(threadRepository, times(1))
                .deleteById(1);
    }

    @Test
    void getThreadById() {
        threadService.getThreadById(1);

        verify(threadRepository, times(1))
                .findById(1);
    }

    @Test
    void getThreadById_returnsNull_ifThreadNotFound() {
        assertTrue(threadService.getThreadById(anyInt()).isEmpty());
        assertFalse(threadService.getThreadById(anyInt()).isPresent());
    }

    @Test
    void getAllThreads() {
        Pageable pageable = PageRequest.of(0, 20);

        threadService.getAllThreads(pageable);

        verify(threadRepository, times(1))
                .findAll(pageable);
    }

    @Test
    void getAllThreads_returnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 20);

        when(threadService.getAllThreads(any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        assertTrue(threadService.getAllThreads(pageable).isEmpty());
    }

    @Test
    void getNumberOfTotalThreads_callsCorrectMethod() {
        threadService.getTotalNumberOfThreads();
        verify(threadRepository, times(1)).count();
    }

    @Test
    void getNumberOfTotalThreads_returnsCorrectValue() {
        when(threadRepository.count()).thenReturn(35L);
        assertEquals(35L, threadService.getTotalNumberOfThreads());
    }

    @Test
    void getNumberOfTotalThreads_returnsZero() {
        assertEquals(0L, threadService.getTotalNumberOfThreads());
    }
}
