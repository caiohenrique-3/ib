package com.example.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Thread extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String initialPostBody;

    @OneToMany(mappedBy = "parentThread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @Column(nullable = false)
    private boolean isLocked;

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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}