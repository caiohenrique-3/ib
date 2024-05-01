package com.example.controllers;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.services.PostService;
import com.example.services.ThreadService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class MainController {
    private final ThreadService threadService;
    private final PostService postService;

    public MainController(ThreadService threadService, PostService postService) {
        this.threadService = threadService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("threads", threadService.getAllThreads());
        return "index";
    }

    @PostMapping("/createThread")
    public String createThread(@RequestParam String title,
                               @RequestParam String body) {
        threadService.createThreadAndReturn(title, body);
        return "redirect:/";
    }

    @GetMapping("/threads/{id}")
    public String showThread(@PathVariable int id, Model model) {
        Optional<Thread> t = threadService.getThreadById(id);

        if (t.isPresent()) {
            model.addAttribute("thread", t.get());
            return "thread";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread Not Found");
    }

    @PostMapping("/replyTo/{threadId}")
    public String createReply(@PathVariable int threadId,
                              @RequestParam(required = false) Integer id, @RequestParam String body) {
        if (id == null) {
            id = threadId;
        }
        Optional<Post> parentPost = postService.getPostById(id);
        if (parentPost.isPresent()) {
            postService.createPostReplyAndReturn(body, id);
        } else {
            postService.createPostAndReturn(body, id);
        }
        return "redirect:/threads/" + threadId;
    }

    @GetMapping("/stats")
    public String showStatsPage(Model model) {
        model.addAttribute("totalThreads",
                threadService.getTotalNumberOfThreads());
        model.addAttribute("totalPosts",
                postService.getTotalNumberOfPosts());
        model.addAttribute("timeSinceLastPost",
                postService.getTimeSinceLastPost());
        model.addAttribute("timeSinceLastThread",
                threadService.getTimeSinceLastThread());

        return "stats";
    }
}