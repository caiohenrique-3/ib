package com.example.controllers;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.services.PostService;
import com.example.services.ThreadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
    public String showMainPage(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Thread> threadPage = threadService.getAllThreads(pageable);
        model.addAttribute("threads", threadPage.getContent());
        model.addAttribute("currentPage", page);
        return "index";
    }

    @PostMapping("/createThread")
    public String createThread(@RequestParam String title,
                               @RequestParam String body,
                               @RequestParam(required = false) String imageUrl) {
        threadService.createThreadAndReturn(title, body, imageUrl);
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

    @PostMapping("/lockThread/{id}")
    public String lockThread(@PathVariable int id, Model model) {
        threadService.lockThreadById(id);

        return "redirect:/threads/" + id;
    }

    @PostMapping("/unlockThread/{id}")
    public String unlockThread(@PathVariable int id, Model model) {
        threadService.unlockThreadById(id);

        return "redirect:/threads/" + id;
    }

    @DeleteMapping("/threads/{id}")
    @ResponseBody
    public String deleteThread(@PathVariable int id, Model model) {
        threadService.deleteThreadById(id);

        if (threadService.getThreadById(id).isPresent())
            return "Error while deleting thread " + id + ".";

        return "Successfully deleted thread " + id + ".";
    }

    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable int id, Model model) {
        Optional<Post> p = postService.getPostById(id);

        if (p.isPresent()) {
            model.addAttribute("post", p.get());
            return "post";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found");
    }

    @PostMapping("/replyTo/{threadId}")
    public String createReply(@PathVariable int threadId,
                              @RequestParam(required = false) Integer id,
                              @RequestParam String body,
                              @RequestParam(required = false) String imageUrl,
                              HttpServletRequest request) {
        if (threadService.getThreadById(threadId).get().isLocked()
                && !request.isUserInRole("admin")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You don't have the right O you don't have the right");
        }

        if (id == null) {
            id = threadId;
        }
        Optional<Post> parentPost = postService.getPostById(id);
        if (parentPost.isPresent()) {
            postService.createPostReplyAndReturn(body, id, imageUrl);
        } else {
            postService.createPostAndReturn(body, id, imageUrl);
        }
        return "redirect:/threads/" + threadId;
    }

    @DeleteMapping("/posts/{id}")
    @ResponseBody
    public String deletePosts(@PathVariable int id, Model model) {
        postService.deletePostById(id);

        if (postService.getPostById(id).isPresent())
            return "Error while deleting post " + id + ".";

        return "Succesfully deleted post " + id + ".";
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

    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        return "admin";
    }

    @PostMapping("/search")
    public String searchTypeInDatabase(@RequestParam("title") String title,
                                       @RequestParam("body") String body,
                                       @RequestParam("type") String type,
                                       Model model) {
        if (title.isBlank() && body.isBlank())
            return "admin";

        if (type.equals("thread")) {
            ArrayList<Thread> threads = threadService
                    .findThreadByTitleAndBody(title, body);
            model.addAttribute("posts", new ArrayList<>());
            model.addAttribute("threads", threads);
            model.addAttribute("resultsType", type);
            return "admin";
        } else if (type.equals("post")) {
            ArrayList<Post> posts = postService
                    .findPostByBody(body);
            model.addAttribute("posts", posts);
            model.addAttribute("threads", new ArrayList<>());
            model.addAttribute("resultsType", type);
            return "admin";
        } else if (type.equals("id")) {
            // TODO: zis
            return "admin";
        } else {
            return "Bad search type";
        }
    }

    @PostMapping("/threads/multiAction")
    public String threadsMultiAction(@RequestParam ArrayList<Integer> itemIds,
                                     @RequestParam String action,
                                     RedirectAttributes redirectAttributes) {
        try {
            if ("delete".equals(action)) {
                for (Integer id : itemIds) {
                    threadService.deleteThreadById(id);
                }
            } else if ("lock".equals(action)) {
                for (Integer id : itemIds) {
                    threadService.lockThreadById(id);
                }
            } else if ("unlock".equals(action)) {
                for (Integer id : itemIds) {
                    threadService.unlockThreadById(id);
                }
            }
            redirectAttributes.addFlashAttribute("message", "Action completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while performing the action.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/posts/multiAction")
    public String postsMultiAction(@RequestParam ArrayList<Integer> itemIds,
                                   @RequestParam String action,
                                   RedirectAttributes redirectAttributes) {
        try {
            if ("delete".equals(action)) {
                for (Integer id : itemIds) {
                    postService.deletePostById(id);
                }
            }

            redirectAttributes.addFlashAttribute("message", "Action completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while performing the action.");
        }
        return "redirect:/admin";
    }
}