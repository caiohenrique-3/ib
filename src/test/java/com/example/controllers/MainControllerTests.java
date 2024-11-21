package com.example.controllers;


import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import com.example.services.PostService;
import com.example.services.ThreadService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTests {
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

    @BeforeEach
    void clearDatabase() {
        postRepository.deleteAll();
        threadRepository.deleteAll();
    }

    @Test
    void testShowMainPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    void testShowMainPage_notAllowed_ifPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/").with(csrf()))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void testCreateThread() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createThread")
                        .param("title", "Test Title")
                        .param("body", "Test Body")
                        .with(csrf()))
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
                .createThreadAndReturn("test", "test", null);

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
                .createThreadAndReturn("test", "test", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/" + t.getId())
                        .param("id", String.valueOf(t.getId()))
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/threads/" + t.getId()));
    }

    @Test
    void testCreateReply_throwsError_ifNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/replyTo/-1")
                        .param("id", String.valueOf(-1))
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void testCreateReply_throwsError_ifLocked() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        threadService.lockThreadById(t.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/replyTo/" + t.getId())
                        .param("body", "Test Body")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testCreateReply_adminReplyToLockedThread() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        threadService.lockThreadById(t.getId());

        List<Post> postList = postService
                .getAllPostsInThreadById(t.getId());

        Assertions.assertTrue(postList.isEmpty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/replyTo/" + t.getId())
                        .param("body", "Test Body")
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        postList = postService
                .getAllPostsInThreadById(t.getId());

        Assertions.assertFalse(postList.isEmpty());
    }

    @Test
    void showStatsPage_returnsCorrectValues() throws Exception {
        Thread t = threadService.createThreadAndReturn("test", "test", null);
        Post p1 = postService.createPostAndReturn("test", t.getId(), null);
        Post p2 = postService.createPostAndReturn("test", t.getId(), null);
        Post p3 = postService.createPostAndReturn("test", t.getId(), null);

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.view().name("stats"))
                .andExpect(MockMvcResultMatchers.model().attribute("totalThreads", 1L))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPosts", 3L))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timeSinceLastPost"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timeSinceLastThread"));
    }

    @Test
    void showStatsPage_returnsNull_ifDatabaseEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/stats"))
                .andExpect(MockMvcResultMatchers.view().name("stats"))
                .andExpect(MockMvcResultMatchers.model().attribute("totalThreads", 0L))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPosts", 0L))
                .andExpect(MockMvcResultMatchers.model().attribute("timeSinceLastPost", "No posts found"))
                .andExpect(MockMvcResultMatchers.model().attribute("timeSinceLastThread", "No threads found"));
    }

    void testShowPost_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/0"))
                .andExpect(MockMvcResultMatchers.view().name("404"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testShowPost() throws Exception {
        Thread t = threadService
                .createThreadAndReturn("test", "test", null);

        Post p1 = postService
                .createPostAndReturn("test", t.getId(), null);

        Post p2 = postService
                .createPostReplyAndReturn("test", p1.getId(), null);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + p1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("post"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + p2.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("post"));
    }

    @Test
    void testSearch() throws Exception {
        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Post p1 = postService
                .createPostAndReturn("test", t1.getId(), null);

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

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                        .param("title", "")
                        .param("body", "test")
                        .param("type", "post")
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("posts"));
    }

    @Test
    void testMultiAction_threads() throws Exception {
        String action = "lock";

        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Thread t2 = threadService
                .createThreadAndReturn("test", "test", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/threads/multiAction")
                        .param("itemIds", String.valueOf(t1.getId()))
                        .param("itemIds", String.valueOf(t2.getId()))
                        .param("action", action)
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/admin"));
    }

    @Test
    void testMultiAction_posts() throws Exception {
        String action = "delete";

        Thread t1 = threadService
                .createThreadAndReturn("test", "test", null);
        Post p1 = postService
                .createPostAndReturn("test", t1.getId(), null);
        Post p2 = postService
                .createPostAndReturn("test", t1.getId(), null);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/multiAction")
                        .param("itemIds", String.valueOf(p1.getId()))
                        .param("itemIds", String.valueOf(p2.getId()))
                        .param("action", action)
                        .with(csrf())
                        .with(user("user")
                                .roles("admin")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/admin"));
    }
}
