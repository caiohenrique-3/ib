package com.example.model;

import jakarta.persistence.*;

@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int threadId;

    @Column(nullable = false)
    private String title;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "initial_post_id")
    private Post initialPost;

    public int getId() {
        return threadId;
    }

    public void setId(int id) {
        this.threadId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Post getInitialPost() {
        return initialPost;
    }

    public void setInitialPost(Post initialPost) {
        this.initialPost = initialPost;
    }
}