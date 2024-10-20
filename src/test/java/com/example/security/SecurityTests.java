package com.example.security;

import com.example.model.Thread;
import com.example.services.ThreadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ThreadService threadService;

    @Test
    void testCreateThread_Csrf() throws Exception {
        // without csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with valid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // with invalid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testCreateReply_Csrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        // without csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with valid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // with invalid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testLogin_Csrf() throws Exception {
        // without csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with valid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // with invalid csrf
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void adminPage_Csrf() throws Exception {
        // without role, without csrf
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(user("username")
                                .roles("user")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with role, without csrf
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(user("admin")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view()
                        .name("error"));

        // with role, with csrf
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(user("admin")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // with role, with invalid csrf
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf().useInvalidToken())
                        .with(user("admin")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("error"));

        // Anon
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers
                        .status().is4xxClientError());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf().useInvalidToken())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers
                        .status().is4xxClientError());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers
                        .status().is4xxClientError());
    }
}
