package com.ecs160.hw1;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class SocialAnalyzerDriver {
    public static void main(String[] args) {

        try (PrintWriter file_out = new PrintWriter(new FileWriter("output.txt"))) {
            // We will output it first to see if we are correctly parsing the file.
            JsonParserFile parser = new JsonParserFile();
            List<Post> social_posts = parser.json_parser("test_input.json");

            // we iterate over the posts and write them to the file
            for (Post post : social_posts) {
                String post_content = "Post: " + post.get_post_content();
                file_out.println(post_content);

                String post_rep_con = "reply: " + post.get_post_replies();
                file_out.println(post_rep_con);

                String reply_count = "Replies count: " + post.get_post_replies().size();
                file_out.println(reply_count);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
