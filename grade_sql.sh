#!/bin/bash

# Ensure PostgreSQL is installed and running
echo ">>>>>>>>>>> CHECKING POSTGRESQL SERVICE"
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL is not installed. Please install it using:"
    echo "  sudo apt update && sudo apt install postgresql postgresql-contrib"
    exit -1
fi

sudo service postgresql start

# Database credentials (Modify if needed)
DB_NAME="socialmedia_db"
DB_USER="postgres"
DB_PASS="9981"

# Check database connection
echo ">>>>>>>>>>> CHECKING DATABASE CONNECTION"
if ! PGPASSWORD=$DB_PASS psql -U $DB_USER -d $DB_NAME -c "SELECT 1;" &>/dev/null; then
    echo "Error: Cannot connect to PostgreSQL. Check your credentials or ensure PostgreSQL is running."
    exit -1
fi

echo ">>>>>>>>>>> CHECKING THE APP COMPILES"

mvn clean install

if [ $? -eq 0 ]; then
    echo "Compilation succeeded"
else
    echo "Compilation failed"
    exit -1
fi

# Default input.json, non-weighted
echo ">>>>>>>>>>> CHECKING THE APP RUNS"

output=$(java -jar target/HW1-solution-1.0-SNAPSHOT.jar --weighted false --file "/mnt/c/EEC 160/ECS160-HW1-skeleton/src/main/resources/input.json")

if [ $? -eq 0 ]; then
    echo "Execution succeeded"
else
    echo "Execution failed"
    exit -1
fi

# Extract relevant values from output
total_posts=$(echo "$output" | grep "Total posts" | awk -F": " '{print $2}')
avg_replies=$(echo "$output" | grep "Average number of replies" | awk -F": " '{print $2}')
duration=$(echo "$output" | grep "Average duration between replies" | awk -F": " '{print $2}')

# Convert time duration to total seconds
IFS=: read -r hours minutes seconds <<< "$duration"
hours=${hours:-0}
minutes=${minutes:-0}
seconds=${seconds:-0}
total_seconds=$((hours * 3600 + minutes * 60 + seconds))

echo ">>>>>>>>>> CHECKING OUTPUT CONDITIONS"
echo "***** Application Output Begins *****"
echo "Total posts = $total_posts"
echo "Avg. replies = $avg_replies"
echo "Duration = $duration"
echo "***** Application Output Ends *****"

if [ "$total_posts" -gt 1992 ] && [ "$total_seconds" -lt 6000 ]; then
    echo "Results meet the conditions!"
else
    echo "Results don't meet the conditions. Please review manually."
fi

# Run tests
echo ">>>>>>>>>>> CHECKING JUNIT TESTS"
test_run_output=$(mvn test)

if [[ "$test_run_output" == *"Failures: 0"* && "$test_run_output" == *"Errors: 0"* && "$test_run_output" == *"Skipped: 0"* ]]; then
    echo "All tests passed!"
else
    echo "Some tests failed!"
    echo "$test_run_output"
fi

tests_run=$(echo "$test_run_output" | grep "Tests run: [0-9]*" | grep -v "elapsed" | awk -F'Tests run: |, ' '{print $2}')
tests_run=$(echo "$tests_run" | tr -d ' \n')

echo "Number of tests run: $tests_run"
if [[ $tests_run -lt 4 ]]; then
    echo "Fewer than 4 tests were run"
else
    echo "More than 4 tests were run"
fi

# Check the number of records entered in PostgreSQL
echo ">>>>>>>>>>> CHECKING DATABASE RECORDS"
num_db_records=$(PGPASSWORD=$DB_PASS psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM posts;" | tr -d ' ')

if [[ $num_db_records -gt 2000 ]]; then
    echo "Successfully entered records in PostgreSQL database."
else
    echo "Something went wrong in record insertion. Must check manually."
fi

# Check the number of compiled classes
if [[ $(find . -name "*.class" | wc -l) -gt 4 ]]; then 
    echo "More than 4 classes created."
else
    echo "Fewer than 4 classes created."
fi
