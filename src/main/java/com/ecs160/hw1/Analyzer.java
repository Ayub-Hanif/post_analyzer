package com.ecs160.hw1;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SortByTimestamp implements Comparator<Post> {
    public int compare(Post o1, Post o2) {
        return o1.get_creation_time().compareTo(o2.get_creation_time());
    }
}

class SortByWordCount implements Comparator<Post> {
    public int compare(Post o1, Post o2) {
        return o1.get_word_count() - o2.get_word_count();
    }
}

class Analyzer {
    private final List<Post> posts;
    private int longest_post_word_count;

    Analyzer(List<Post> posts)  {
        this.posts = posts;
        this.longest_post_word_count = -1; // will be overwritten later in calc_avg_replies
    }
    public int count_total_posts() {
        return posts.size();
    }
    public double calc_avg_replies(boolean weighted) {
        if (!weighted) {
            int total_post_replies = 0;
            for (Post p : posts) {
                total_post_replies += p.get_post_replies().size();
            }
            return (double) total_post_replies / this.count_total_posts();
        } else {
            // calculate length of longest post
            if (this.longest_post_word_count == -1) {
                // if we haven't already calculated the longest word count, then use it
                this.longest_post_word_count = Collections.max(posts, new SortByWordCount()).get_word_count();
            }
            double total_post_weights = 0;
            for (Post p : posts) {
                double current_post_weight = 0;
                for (Post reply : p.get_post_replies()) {
                    current_post_weight += 1 + ((((double)p.get_word_count()) / this.longest_post_word_count));
                }
                total_post_weights += current_post_weight;
            }
            return total_post_weights / this.count_total_posts();
        }
    }

    public double calc_avg_duration() {
        long total_duration = 0;
        for (Post p : posts) {
            List<Post> replies_and_post = p.get_post_replies();
            replies_and_post.addFirst(p); // add the post, it should be considered a comment for purposes of calculation
            replies_and_post.sort(new SortByTimestamp());
            for (int i = 0; i < replies_and_post.size() - 1; i++) {
                Instant currentInstant = replies_and_post.get(i + 1).get_creation_time().toInstant();
                Instant previousInstant = replies_and_post.get(i).get_creation_time().toInstant();
                total_duration += ChronoUnit.SECONDS.between(currentInstant, previousInstant);
            }
        }
        return (double) total_duration / this.count_total_posts();
    }
}
