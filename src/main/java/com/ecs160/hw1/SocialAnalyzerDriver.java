package com.ecs160.hw1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SocialAnalyzerDriver {
    private static final String sql_name = "socialmedia_db";
    private static final String url = "jdbc:postgresql://localhost:5432/socialmedia_db";
    private static final String user = "postgres";
    private static final String password = "9981"; 
    public static void main(String[] args) {


        boolean weighted = false;
        String filePath = "input.json";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--weighted") && i + 1 < args.length && args[i + 1].equals("true")) {
                weighted = true;
            }
            if (args[i].equals("--file") && i + 1 < args.length) {
                filePath = args[i + 1];
            }
        }

        Database data_base = new Database(sql_name, user, password);
        
        initi_db(data_base, filePath);

        List<Post> post_list = get_posts_db();

        Analyzer analyzer = new Analyzer(post_list);

        System.out.println("Total posts: " + analyzer.count_total_posts());
        System.out.println("Average number of replies: " + analyzer.calc_avg_replies(weighted));
        System.out.println("Average duration between replies: " + analyzer.get_format_duration());
    }

    private static void initi_db(Database data_base, String filePath) {

        //before the new stuff is added, the table is cleared.
        data_base.free_table();
        try{
            JsonParserFile parser = new JsonParserFile();
            List<Post> posts_from_blue = parser.json_parser(filePath);

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
    }

    private static List<Post> get_posts_db() {
        List<Post> post_list = new ArrayList<>();
        try {
            Connection connect = DriverManager.getConnection(url, user, password);
            String sql_query = "SELECT * FROM posts WHERE parent_post_id IS NULL ORDER BY creation_time";
            PreparedStatement statement = connect.prepareStatement(sql_query);
            ResultSet post = statement.executeQuery();

            while(post.next()) {
                int post_Id = post.getInt("post_Id");
                String post_content = post.getString("post_content");
                Timestamp time_created = post.getTimestamp("creation_time");
                int word_count = post.getInt("word_count");
                List<Post> post_replies = get_replies_post(connect, post_Id);

                Post post_object = new Post(post_Id, post_content, time_created, word_count);
                for (Post reply : post_replies) {
                    post_object.add_reply_under_post(reply);
                }
                post_list.add(post_object);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return post_list;
    }


    private static List<Post> get_replies_post(Connection connect, int post_Id) throws Exception {
        List<Post> post_replies = new ArrayList<>();
        try {
            String sql_query = "SELECT * FROM posts WHERE parent_post_id = ? ORDER BY creation_time";
            PreparedStatement statement = connect.prepareStatement(sql_query);
            statement.setInt(1, post_Id);
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                int reply_Id = result.getInt("post_Id");
                String reply_content = result.getString("post_content");
                Timestamp reply_time_created = result.getTimestamp("creation_time");
                int reply_word_count = result.getInt("word_count");

                Post reply = new Post(reply_Id, reply_content, reply_time_created, reply_word_count);
                post_replies.add(reply);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return post_replies;
    }
}
