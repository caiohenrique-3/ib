package com.example.model;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1)
    private int postId;

    @Column(nullable = false)
    private String body;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "thread_id")
    private Thread parentThread;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Thread getParentThread() {
        return parentThread;
    }

    public void setParentThread(Thread parentThread) {
        this.parentThread = parentThread;
    }

    public String getFormattedInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy (EEE) HH:mm:ss 'Id '" + postId, Locale.US);
        return sdf.format(timestamp);
    }
}
