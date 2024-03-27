package com.example.controllers;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.services.PostService;
import com.example.services.ThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class MainControllerUnitTests {
    @Mock
    private ThreadService threadService;

    @Mock
    private PostService postService;

    @InjectMocks
    private MainController mainController;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void showMainPage() {
        Model model = Mockito.mock(Model.class);
        String returnValue = mainController.showMainPage(model);

        verify(threadService, times(1))
                .getAllThreads();

        verify(model, times(1))
                .addAttribute(anyString(), any(Object.class));

        assertEquals("index", returnValue);
    }

    @Test
    void createThread() {
        String text = "test text plac";
        String returnValue = mainController.createThread(text, text);

        verify(threadService, times(1))
                .createThreadAndReturn(text, text);
        assertEquals("redirect:/", returnValue);
    }

    @Test
    void showThread() {
        Thread t = mock(Thread.class);
        doReturn(Optional.of(t)).when(threadService).getThreadById(anyInt());

        String returnValue = mainController
                .showThread(1, mock(Model.class));

        verify(threadService, times(1))
                .getThreadById(1);
        assertEquals("thread", returnValue);
    }

    @Test
    void createReply() {
        Post p = mock(Post.class);
        doReturn(p).when(postService).createPostAndReturn(anyString(), anyInt());

        String returnValue = mainController
                .createReply(0, "test");

        assertEquals("redirect:/threads/0", returnValue);
    }
}
