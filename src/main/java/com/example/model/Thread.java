package com.example.model;

import jakarta.persistence.*;

@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int threadId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String initialPostBody;

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
}