package com.ecs160.hw1;

import java.util.Date;
import java.util.List;

public class Post {
    private final int post_id;
    private final int word_count;
    private final Date creation_date;
    private final List<Post> replies;

    public Post(int post_id, int word_count, Date creation_date, List<Post> replies) {
        this.post_id = post_id;
        this.word_count = word_count;
        this.creation_date = creation_date;
        this.replies = replies;
    }

    public int getPost_id() {
        return post_id;
    }

    public int getWord_count() {
        return word_count;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public List<Post> getReplies() {
        return replies;
    }
}
