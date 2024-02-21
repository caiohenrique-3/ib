package com.example.controllers;

import com.example.repositories.ThreadRepository;
import com.example.services.ThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class MainControllerUnitTests {
    @Mock
    private ThreadService threadService;

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
}
