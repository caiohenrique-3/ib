package com.example.model;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1)
    private int threadId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy (EEE) HH:mm:ss 'Id '" + threadId, Locale.US);
        return sdf.format(timestamp);
    }

}