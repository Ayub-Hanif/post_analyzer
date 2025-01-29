package com.ecs160.hw1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {
    private final String sql_name;
    private final String sql_user;
    private final String sql_password;
    private Connection connect;

    public Database(String sql_name, String sql_user, String sql_password) {
        this.sql_name = sql_name;
        this.sql_user = sql_user;
        this.sql_password = sql_password;
        connection();
        create_db_table();
    }

    public void connection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/" + sql_name;
            connect = DriverManager.getConnection(url, sql_user, sql_password);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("Connection failed to the data base " + sql_name);
        }
    }

    public void create_db_table() {
        try {
            String sql_table = """
            CREATE TABLE IF NOT EXISTS
            posts(post_Id SERIAL PRIMARY KEY,
                post_content TEXT NOT NULL,
                creation_time TIMESTAMP,
                word_count INT NOT NULL,
                parent_post_id INT REFERENCES posts(post_id) ON DELETE CASCADE
            );""";

            connect.createStatement().executeUpdate(sql_table);
            System.out.println("Table created or/and checked successfully");

        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Table creation or checks failed");
        }
    }

    public String get_db_name() {
        return sql_name;
    }

    public String get_db_user() {
        return sql_user;
    }

    public String get_db_password() {
        return sql_password;
    }

    private boolean post_table_exists(int input_post_Id) {
        try {
            String sql_table = "SELECT COUNT(*) FROM posts WHERE post_Id = ?";
            PreparedStatement info = connect.prepareStatement(sql_table);
            info.setInt(1,input_post_Id);
            ResultSet result = info.executeQuery();
            if(result.next()){ return result.getInt(1) > 0; }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void free_table(){
        try {
            String sql_table = "DELETE FROM posts;";
            connect.createStatement().executeUpdate(sql_table);
            System.out.println("cleanned the table from any Data");
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Table still have values after deletion");
        }
    }

    public void insert_post(Post post, Integer parent_post_Id) {

        if (post_table_exists(post.get_post_Id())) {
            System.out.println("Post with post_Id: " + post.get_post_Id() + " already exists, skipping.");
            return;
        }
        try {
            String sql_query = """
            INSERT INTO posts (post_Id, post_content, creation_time, word_count, parent_post_Id)
            VALUES (?, ?, ?, ?, ?)
            """;
            PreparedStatement info = connect.prepareStatement(sql_query);
            info.setInt(1, post.get_post_Id());
            info.setString(2, post.get_post_content());
            info.setTimestamp(3, post.get_creation_time());
            info.setInt(4, post.get_word_count());
            if (parent_post_Id != null) {
                info.setInt(5, parent_post_Id);
            } else {
                info.setNull(5, java.sql.Types.INTEGER);
            }
            info.executeUpdate();

            System.out.println("added with post_Id: " + post.get_post_Id());

            for(Post replies : post.get_post_replies()) {
                insert_post(replies, post.get_post_Id());
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Post insertion failed");
        }
    }

    public void close() {
        try {
            connect.close();
            System.out.println("Connection of database is closed");
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Connection didn't close correctly");
        }
    }
}
