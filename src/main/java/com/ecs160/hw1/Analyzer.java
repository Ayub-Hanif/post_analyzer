package com.ecs160.hw1;

public interface Analyzer {
    static int longest_post_word_count = 0;
    static int num_posts = 0;

    int count_total_posts();
    int calc_avg_replies();
    int calc_avg_duration();
}