package com.ecs160.hw1;
import java.util.List;


class Weight_analyzers implements Analyzer {
    private final List<Post> posts;
    Weight_analyzers(List<Post> posts)  {
       this.posts = posts;
    }
    public int count_total_posts() {
        return posts.size();
    }
    public int calc_avg_replies() {
        int total_post_replies = 0;
        for (Post p : posts) {
            total_post_replies += p.get_post_replies().size();
        }
        return total_post_replies / this.count_total_posts();
    }

    public int calc_avg_duration() {
        int total_duration = 0;
        /* for (Post p : posts) {
            total_duration;
        }*/
        return 1;
    }


}
