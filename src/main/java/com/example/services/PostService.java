package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;

    public PostService(PostRepository postRepository, ThreadRepository threadRepository) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
    }

    public Post createPostAndReturn(String body, int parentThreadId, String imageUrl) {
        Thread parentThread = threadRepository.findById(parentThreadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        Post post = new Post();
        post.setBody(body);
        post.setParentThread(parentThread);
        post.setTimestamp(new Date());
        post.setImageUrl(imageUrl);
        postRepository.save(post);
        return post;
    }

    public Post createPostReplyAndReturn(String body, int parentPostId, String imageUrl) {
        Post parentPost = postRepository.findById(parentPostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post post = new Post();
        post.setBody(body);
        post.setParentThread(parentPost.getParentThread());
        post.setParentPost(parentPost);
        post.setTimestamp(new Date());
        post.setImageUrl(imageUrl);
        postRepository.save(post);
        return post;
    }

    public void deletePostById(int postId) {
        postRepository.deleteById(postId);
    }

    public Optional<Post> getPostById(int postId) {
        return postRepository.findById(postId);
    }

    public List<Post> getAllPostsInThreadById(int threadId) {
        Thread thread = threadRepository.findById(threadId).orElseThrow(()
                -> new RuntimeException("Thread not found."));

        return postRepository.findByParentThread(thread);
    }

    public long getTotalNumberOfPosts() {
        return postRepository.count();
    }

    public String getTimeSinceLastPost() {
        List<Post> latestPosts = postRepository.findLatestPosts(PageRequest.of(0, 1));

        if (latestPosts.isEmpty()) {
            return "No posts found";
        }

        Post latestPost = latestPosts.get(0);
        long diffInMillies = Math.abs(new Date().getTime() - latestPost.getTimestamp().getTime());

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        diffInMillies -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillies);
        diffInMillies -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
        diffInMillies -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillies);

        return days + " days, " + hours + " hours, " + minutes + " minutes, " +
                seconds + " seconds - post " + latestPost.getId() + " on thread " +
                latestPost.getParentThread().getId();
    }

    public ArrayList<Post> findPostByBody(String body) {
        List<Post> posts = postRepository.findByPostBody(body);
        return new ArrayList<>(posts);
    }
}