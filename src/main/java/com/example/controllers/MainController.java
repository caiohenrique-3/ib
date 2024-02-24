package com.example.controllers;

import com.example.services.ThreadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final ThreadService threadService;

    public MainController(ThreadService threadService) {
        this.threadService = threadService;
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
}
