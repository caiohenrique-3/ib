package com.example.controllers;


import com.example.model.Thread;
import com.example.services.ThreadService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ThreadService threadService;

    @Test
    void testShowMainPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    void testShowMainPage_notAllowed_ifPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/"))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void testCreateThread() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    void testCreateThread_notAllowed_ifGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createThread"))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void testShowThread() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test");

        mockMvc.perform(MockMvcRequestBuilders.get("/threads/" + t.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("thread"));
    }

    @Test
    void testShowThread_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/threads/0"))
                .andExpect(MockMvcResultMatchers.view().name("404"));
    }

    @Test
    void testCreateReply() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test");

        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/threads/" + t.getId()));
    }

    @Test
    void testCreateReply_throwsError_ifNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/-1")
                        .param("id", String.valueOf(-1))
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }
}
