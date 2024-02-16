CREATE TABLE IF NOT EXISTS thread (
    thread_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    initial_post_body TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS post (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    body TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    thread_id INT NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES thread(thread_id)
);