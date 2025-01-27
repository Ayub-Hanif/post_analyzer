package com.ecs160.hw1;

import java.util.List;

public interface Analyzer {
    static int longest_post_word_count = 0;
    static int num_posts = 0;

    public int count_total_posts();
    public int calc_avg_replies();
    public int calc_avg_duration();
}

class WeightedAnalyzer implements Analyzer {
    private final List<Post> posts;
    WeightedAnalyzer(List<Post> posts)  {
       this.posts = posts;
    }
    public int count_total_posts() {
        return posts.size();
    }
    public int calc_avg_replies() {
        int total_post_replies = 0;
        for (Post p : posts) {
            total_post_replies += p.getReplies().size();
        }
        return total_post_replies / this.count_total_posts();
    }

    public int calc_avg_duration() {
        int total_duration = 0;
        for (Post p : posts) {
            total_duration +=  
        }
    }


}
