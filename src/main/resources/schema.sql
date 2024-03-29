CREATE SEQUENCE IF NOT EXISTS global_seq;

CREATE TABLE IF NOT EXISTS thread (
    thread_id INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    initial_post_body LONGTEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS post (
    post_id INT PRIMARY KEY,
    body LONGTEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    thread_id INT NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES thread(thread_id)
);
