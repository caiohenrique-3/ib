package com.example.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int threadId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String initialPostBody;

    @OneToMany(mappedBy = "parentThread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitialPostBody() {
        return initialPostBody;
    }

    public void setInitialPostBody(String initialPostBody) {
        this.initialPostBody = initialPostBody;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}