package com.example.security;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.services.PostService;
import com.example.services.ThreadService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
    PostRepository postRepository;

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

    @Test
    void testLockThread_Csrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/lockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId())))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with valid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/lockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // with valid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders.post("/lockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf())
                        .with(user("admin")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // with invalid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders.post("/lockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()
                                .useInvalidToken())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with invalid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/lockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testUnlockThread_Csrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        threadService.lockThreadById(t.getId());

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/unlockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId())))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with valid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/unlockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // with valid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders.post("/unlockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf())
                        .with(user("admin")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // with invalid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders.post("/unlockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()
                                .useInvalidToken())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // with invalid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/unlockThread/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf().useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testDeleteThread_csrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        String successMessage = "Successfully deleted thread " + t.getId() + ".";

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/threads/" + t.getId())
                        .param("id", String.valueOf(t.getId())))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with valid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/threads/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // with valid csrf, with role
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/threads/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        Assertions.assertEquals(successMessage, response);

        // with invalid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/threads/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()
                                .useInvalidToken())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with invalid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/threads/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .with(csrf()
                                .useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testDeletePost_csrf() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);
        Post p = postService
                .createPostAndReturn("test", t.getId(), null);

        String successMessage = "Succesfully deleted post " + p.getId() + ".";

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/" + p.getId())
                        .param("id", String.valueOf(p.getId())))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with valid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/" + p.getId())
                        .param("id", String.valueOf(p.getId()))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // with valid csrf, with role
        MvcResult firstMvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/" + p.getId())
                        .param("id", String.valueOf(p.getId()))
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = firstMvcResult.getResponse().getContentAsString();

        Assertions.assertEquals(successMessage, response);

        // with invalid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/" + p.getId())
                        .param("id", String.valueOf(p.getId()))
                        .with(csrf()
                                .useInvalidToken())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with invalid csrf, without role
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/" + p.getId())
                        .param("id", String.valueOf(p.getId()))
                        .with(csrf()
                                .useInvalidToken()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testSearch_csrf() throws Exception {
        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Post p1 = postService
                .createPostAndReturn("test", t1.getId(), null);

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                        .param("title", "test")
                        .param("body", "test")
                        .param("type", "thread"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with valid csrf, with role
        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                        .param("title", "test")
                        .param("body", "test")
                        .param("type", "thread")
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("threads"));
    }

    @Test
    void testThreadsMultiAction_csrf() throws Exception {
        String action = "lock";

        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Thread t2 = threadService
                .createThreadAndReturn("test", "test", null);

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/threads/multiAction")
                        .param("itemIds", String.valueOf(t1.getId()))
                        .param("itemIds", String.valueOf(t2.getId()))
                        .param("action", action))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testPostsMultiAction_csrf() throws Exception {
        String action = "delete";

        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Post p1 = postService
                .createPostAndReturn("test", t1.getId(), null);
        Post p2 = postService
                .createPostAndReturn("test", t1.getId(), null);

        // without csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/multiAction")
                        .param("itemIds", String.valueOf(p1.getId()))
                        .param("itemIds", String.valueOf(p2.getId()))
                        .param("action", action))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // with csrf, without role
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/multiAction")
                        .param("itemIds", String.valueOf(p1.getId()))
                        .param("itemIds", String.valueOf(p2.getId()))
                        .param("action", action)
                        .with(csrf())
                        .with(user("user")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
