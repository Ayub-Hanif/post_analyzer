package com.ecs160.hw1;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyzerTest {
    static Post generateTestingPost(String timestampString, int word_count) {
        
        return new Post(0, "", Timestamp.from(Instant.parse(timestampString)), word_count);
    }

    @Test
    void testSinglePost() {
       Post p = generateTestingPost("2024-12-10T06:26:59.479Z", 100 );
       List<Post> posts = new ArrayList<>();
       posts.add(p);
       Analyzer analyzer = new Analyzer(posts);
       assertEquals(0, analyzer.calc_avg_duration());
       assertEquals(0, analyzer.calc_avg_replies(false));
       assertEquals(0, analyzer.calc_avg_replies(true));
        assertEquals(1, analyzer.count_total_posts());
    }

    @Test
    void testSinglePostWithOneReply() {
        int count_1 = 200;
        Post post = generateTestingPost("2023-11-10T06:26:59.479Z", count_1 * 2);
        Post reply = generateTestingPost("2024-12-10T06:26:59.479Z", count_1 );
        post.add_reply_under_post(reply);
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        Analyzer analyzer = new Analyzer(posts);
        assertEquals(1, analyzer.count_total_posts());
        assertEquals(34214400, analyzer.calc_avg_duration());
        assertEquals(1, analyzer.calc_avg_replies(false));
        // weight of the one reply is = 1 + (200 / 400) = 2.
        // this is only post, so the average weight is 2 / 1post = 2.
        assertEquals(1.5, analyzer.calc_avg_replies(true));
    }

    @Test
    void testMultiplePosts() {

//        Post post1 = generateTestingPost("2028-01-04T13:03:15.508356", 291);
//        Post post2 = generateTestingPost("2025-07-07T17:35:39.235291", 202);
//        Post post3 = generateTestingPost("2026-10-19T09:57:47.154925", 0);
//
//        Post reply11 = generateTestingPost("2030-06-15T08:30:45.123456", 482);
//        Post reply12 = generateTestingPost("2032-01-20T14:50:10.654321", 123);
//        Post reply13 = generateTestingPost("2035-09-05T19:15:30.789012", 789);
//
//        Post reply21 = generateTestingPost("2028-12-25T11:10:20.876543", 345);
//        Post reply22 = generateTestingPost("2030-08-14T16:40:55.543210", 678);
//
//        Post reply31 = generateTestingPost("2032-07-30T10:25:50.111222", 111);

//        post1.add_reply_under_post(reply11);
//        post1.add_reply_under_post(reply12);
//        post1.add_reply_under_post(reply13);
//        post2.add_reply_under_post(reply21);
//        post2.add_reply_under_post(reply22);
//        post3.add_reply_under_post(reply31);
//
//        List<Post> posts = new ArrayList<>();
//        posts.add(post1);
//        posts.add(post2);
//        posts.add(post3);
//        Analyzer analyzer = new Analyzer(posts);
//        assertEquals(3, analyzer.count_total_posts());
//        assertEquals(195154511.3, analyzer.calc_avg_duration());

    }

}
