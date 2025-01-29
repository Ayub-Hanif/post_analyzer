package com.ecs160.hw1;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class SocialAnalyzerDriver {
    private static final String sql_name = "socialmedia_db";
    private static final String url = "jdbc:postgresql://localhost:5432/socialmedia_db";
    private static final String user = "postgres";
    private static final String password = "9981"; 
    public static void main(String[] args) {

        Database data_base = new Database(sql_name, user, password);
        
        initi_db(data_base);

        List<Post> post_list = get_posts_db();

        Analyzer analyzer = new Analyzer(post_list);

        file_output(post_list, analyzer);
    }

    private static void initi_db(Database data_base) {
        //before the new stuff is added, the table is cleared.
        data_base.free_table();

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

    private static void file_output(List<Post> post_list, Analyzer analyzer) {
        try (PrintWriter file_out = new PrintWriter(new FileWriter("output.txt"))) {

            file_out.println("Total num of Posts: " + analyzer.count_total_posts());
            file_out.println("AVG  Replies per post: " + analyzer.calc_avg_replies(false));
            file_out.println("AVG  Replies per post weighted: " + analyzer.calc_avg_replies(true));
            file_out.println("AVG Interval Between Replies: " + analyzer.get_format_duration());

            file_out.println("\n#######################################################################################################\n");

            for (Post post : post_list) {
                file_out.println("#######################################################################################################");
                file_out.println("Post ID: " + post.get_post_Id() + "Post Content: " + post.get_post_content());
                file_out.println("Post create Time: " + post.get_creation_time() + "Post Word Count: " + post.get_word_count());

                List<String> sql_replies = new ArrayList<>();

                for (Post reply : post.get_post_replies()) {
                    sql_replies.add("Reply ID: " + reply.get_post_Id() + "Reply Content: " + reply.get_post_content());
                }

                file_out.println("Number of Replies: " + sql_replies.size());
                file_out.println("List of Replies: " + sql_replies);
            }
            System.out.println("Data from sql file created and filled fully into output.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
