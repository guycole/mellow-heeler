#!/bin/bash
#
# Title:load_log_query1.sh
# Description:
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler
export PGHOST=localhost
export PGPASSWORD=woofwoof
export PGUSER=heeler_admin
#
psql $PGDATABASE -c "select * from heeler_v1.load_log where file_date = '2024-2-20' and site = 'vallejo1';" > load_log.txt
#
