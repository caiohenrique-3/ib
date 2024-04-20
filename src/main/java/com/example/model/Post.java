package com.example.model;

import jakarta.persistence.*;

@Entity
public class Post extends BaseEntity {
    @Column(nullable = false)
    private String body;

    @ManyToOne(optional = false)
    @JoinColumn(name = "thread_id")
    private Thread parentThread;

    @ManyToOne
    @JoinColumn(name = "parent_post_id")
    private Post parentPost;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Thread getParentThread() {
        return parentThread;
    }

    public void setParentThread(Thread parentThread) {
        this.parentThread = parentThread;
    }

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }
}