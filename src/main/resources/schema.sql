CREATE SEQUENCE IF NOT EXISTS global_seq;

CREATE TABLE IF NOT EXISTS thread (
    id INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    initial_post_body TEXT NOT NULL,
    image_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS post (
    id INT PRIMARY KEY,
    body TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    image_url VARCHAR(255),
    thread_id INT NOT NULL,
    parent_post_id INT,
    FOREIGN KEY (thread_id) REFERENCES thread(id),
    FOREIGN KEY (parent_post_id) REFERENCES post(id)
);
