#!/bin/bash
#
# Title:load_log_query2.sh
# Description: get all vallejo files for a day
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler
export PGHOST=localhost
export PGPASSWORD=woofwoof
export PGUSER=heeler_admin
#
psql $PGDATABASE -c "select file_name from heeler_v1.load_log where file_date = '2024-2-21' and site = 'vallejo1';" > load_log_file1.txt
psql $PGDATABASE -c "select file_name from heeler_v1.load_log where file_date = '2024-2-22' and site = 'vallejo1';" > load_log_file2.txt
psql $PGDATABASE -c "select file_name from heeler_v1.load_log where file_date = '2024-2-23' and site = 'vallejo1';" > load_log_file3.txt
#
