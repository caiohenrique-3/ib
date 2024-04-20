package com.example.services;

import com.example.model.Post;
import com.example.model.Thread;
import com.example.repositories.PostRepository;
import com.example.repositories.ThreadRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;

    public PostService(PostRepository postRepository, ThreadRepository threadRepository) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
    }

    public Post createPostAndReturn(String body, int parentThreadId) {
        Thread parentThread = threadRepository.findById(parentThreadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        Post post = new Post();
        post.setBody(body);
        post.setParentThread(parentThread);
        post.setTimestamp(new Date());
        postRepository.save(post);
        return post;
    }

    public Post createPostReplyAndReturn(String body, int parentPostId) {
        Post parentPost = postRepository.findById(parentPostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post post = new Post();
        post.setBody(body);
        post.setParentThread(parentPost.getParentThread());
        post.setParentPost(parentPost);
        post.setTimestamp(new Date());
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
}
