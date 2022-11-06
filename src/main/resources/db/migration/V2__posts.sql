CREATE TABLE posts
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title     TEXT                                    NOT NULL,
    content   TEXT                                    NOT NULL,
    author_id BIGSERIAL,
    CONSTRAINT pk_posts PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (author_id) REFERENCES users (id)
);