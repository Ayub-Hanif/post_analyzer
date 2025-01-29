[![Maven Build and Test](https://github.com/Ayub-Hanif/post_analyzer/actions/workflows/maven.yml/badge.svg)](https://github.com/Ayub-Hanif/post_analyzer/actions/workflows/maven.yml)



# ECS160 HW1: The Social Media Analysis

A Java application to store and analyze social media posts from Blue sky.
- Parses `input.json`
- Stores data in SQL
- We need to calculate statistics (total posts, avg replies, etc.)
- Supports weighted analysis (true/false)

## Getting Started
1. Clone this repo.
2. Run `mvn clean install` in IntelliJ or terminal.
3. Run the app with:
   ```bash
   java -jar target/HW1-solution-1.0-SNAPSHOT.jar
## Database Setup
1. **Install PostgreSQL** (if you haven't already).
2. Make sure you have the ```socialmedia_db``` Data base create.
3. If you haven't or it is filled with other stuff you can do:
   1. ```DROP DATABASE socialmedia_db;```
   2. ```CREATE DATABASE socialmedia_db;```
   3. ```\q```
4. Then you can run the code perfectly fine.
