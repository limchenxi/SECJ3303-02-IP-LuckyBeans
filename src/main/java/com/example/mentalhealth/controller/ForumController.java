package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.ForumPost;
import com.example.mentalhealth.model.ForumComment;
import com.example.mentalhealth.service.ForumService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService service;

    public ForumController(ForumService service) {
        this.service = service;
    }

    @GetMapping
    public String viewForum(Model model) {
        model.addAttribute("posts", service.getAllPosts());
        return "forum/list";
    }

    @GetMapping("/new")
    public String newPost(Model model) {
        model.addAttribute("post", new ForumPost());
        return "forum/new";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute ForumPost post) {
        post.setUserId(1L);
        service.createPost(post);
        return "redirect:/forum";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        // Use mock data for demo/testing if id == 999
        if (id == 999) {
            ForumPost mockPost = new ForumPost();
            mockPost.setId(999L);
            mockPost.setTitle("Mock Title");
            mockPost.setContent("This is mock content for testing.");
            mockPost.setCreatedAt(java.time.LocalDateTime.now());

            ForumComment c1 = new ForumComment();
            c1.setUserId(1L);
            c1.setContent("Nice post!");
            c1.setCreatedAt(java.time.LocalDateTime.now());

            ForumComment c2 = new ForumComment();
            c2.setUserId(2L);
            c2.setContent("Thanks for sharing.");
            c2.setCreatedAt(java.time.LocalDateTime.now());

            java.util.List<ForumComment> mockComments = java.util.List.of(c1, c2);

            model.addAttribute("post", mockPost);
            model.addAttribute("comments", mockComments);
            model.addAttribute("comment", new ForumComment());
            return "forum/post";
        }
        // ...existing code...
        model.addAttribute("post", service.getPost(id));
        model.addAttribute("comments", service.getComments(id));
        model.addAttribute("comment", new ForumComment());
        return "forum/post";
    }

    @PostMapping("/comment")
    public String addComment(@ModelAttribute ForumComment comment) {
        comment.setUserId(1L);
        service.addComment(comment);
        return "redirect:/forum/post/" + comment.getPostId();
    }
}
