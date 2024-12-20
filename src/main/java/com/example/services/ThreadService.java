package com.example.services;

import com.example.model.Thread;
import com.example.repositories.ThreadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final PostService postService;

    public ThreadService(ThreadRepository threadRepository, PostService postService) {
        this.threadRepository = threadRepository;
        this.postService = postService;
    }

    public Thread createThreadAndReturn(String title, String body, String imageUrl) {
        Thread thread = new Thread();
        thread.setTitle(title);
        thread.setInitialPostBody(body);
        thread.setTimestamp(new Date());
        thread.setImageUrl(imageUrl);
        threadRepository.save(thread);
        return thread;
    }

    public void deleteThreadById(int threadId) {
        threadRepository.deleteById(threadId);
    }

    public Optional<Thread> getThreadById(int threadId) {
        return threadRepository.findById(threadId);
    }

    public Page<Thread> getAllThreads(Pageable pageable) {
        return threadRepository.findAll(pageable);
    }

    public long getTotalNumberOfThreads() {
        return threadRepository.count();
    }

    // TODO: Refactor this function to make it more testable
    public String getTimeSinceLastThread() {
        List<Thread> latestThreads = threadRepository
                .findLatestThreads(PageRequest.of(0, 1));

        if (latestThreads.isEmpty()) {
            return "No threads found";
        }

        Thread latestThread = latestThreads.get(0);
        long diffInMillies = Math.abs(new Date().getTime() - latestThread.getTimestamp().getTime());

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        diffInMillies -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillies);
        diffInMillies -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
        diffInMillies -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillies);

        return days + " days, " + hours + " hours, " + minutes + " minutes, " +
                seconds + " seconds - thread " + latestThread.getId();
    }

    // TODO: Combine these functions somehow
    public void lockThreadById(int threadId) {
        Optional<Thread> t = threadRepository.findById(threadId);
        t.get().setLocked(true);
        threadRepository.save(t.get());
    }

    public void unlockThreadById(int threadId) {
        Optional<Thread> t = threadRepository.findById(threadId);
        t.get().setLocked(false);
        threadRepository.save(t.get());
    }

    public ArrayList<Thread> findThreadByTitleAndBody(String title, String body) {
        List<Thread> threads = threadRepository
                .findByTitleAndBody(title, body);
        return new ArrayList<>(threads);
    }
}
