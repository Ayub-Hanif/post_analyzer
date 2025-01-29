package com.ecs160.hw1;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SocialAnalyzerDriver {
    public static void main(String[] args) {

        Database data_base = new Database("socialmedia_db", "postgres", "9981");


        try{
            JsonParserFile parser = new JsonParserFile();
            List<Post> posts_from_blue = parser.json_parser("input.json");

            for (Post post : posts_from_blue) {

                data_base.insert_post(post, null);
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                data_base.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        try (PrintWriter file_out = new PrintWriter(new FileWriter("output.txt"))) {

            Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialmedia_db", "postgres", "9981");

            String sql_query = "SELECT * FROM posts WHERE parent_post_id IS NULL ORDER BY creation_time";
            PreparedStatement statement = connect.prepareStatement(sql_query);
            ResultSet post = statement.executeQuery();

            while(post.next()) {
                file_out.println("Post ID: " + post.getInt("post_Id") + " Content: " + post.getString("post_content"));
                file_out.println("create time: " + post.getTimestamp("creation_time") + " Word count: " + post.getInt("word_count"));

                List<String> sql_query_replies = new ArrayList<>(); 
                String reply = "SELECT * FROM posts WHERE parent_post_id = ? ORDER BY creation_time";
                PreparedStatement statement_replies = connect.prepareStatement(reply);
                statement_replies.setInt(1, post.getInt("post_Id"));
                ResultSet result_replies = statement_replies.executeQuery();

                while(result_replies.next()) {
                    int reply_Id = result_replies.getInt("post_Id");
                    String reply_content = result_replies.getString("post_content");
                    sql_query_replies.add("Reply ID: " + reply_Id + " Content: " + reply_content);
                }
                file_out.println("Number of Replies: " + sql_query_replies.size());
                file_out.println("List of Replies: " + sql_query_replies);
                file_out.println("#######################################################################################################");
            }

            System.out.println("Data from sql file created and filled fully into output.txt");

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                data_base.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }
}
