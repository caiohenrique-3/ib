package com.example.model;

public class Thread {
    private int id;
    private String title;
    private Post initialPost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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