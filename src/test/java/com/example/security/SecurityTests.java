package com.example.security;

import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import com.example.services.PostService;
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

    @Autowired
    PostService postService;

    @Autowired
    ThreadRepository threadRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    void testCreateThread_withoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testCreateThread_withCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    void testCreateThread_withInvalidCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testCreateReply_withoutCsrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testCreateReply_withInvalidCsrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testLogin_withoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "john doe")
                        .param("password", "TESTPASS"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testLogin_withCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testLogin_withInvalidCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login/")
                        .param("username", "john doe")
                        .param("password", "TESTPASS")
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void adminUser_onAdminPage_withoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void adminUser_onAdminPage_withInvalidCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf().useInvalidToken())
                        .with(user("admin")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void adminUser_onAdminPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(user("admin")
                                .password("password")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void normalUser_onAdminPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(user("user")
                                .password("password")
                                .roles("user")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void unauthenticatedUser_onAdminPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void unauthenticatedUser_onAdminPage_withInvalidCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf().useInvalidToken())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void unauthenticatedUser_onAdminPage_withCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
