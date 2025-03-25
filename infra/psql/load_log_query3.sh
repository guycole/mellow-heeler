#!/bin/bash
#
# Title:load_log_query3.sh
# Description: 
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler
export PGHOST=localhost
export PGPASSWORD=woofwoof
export PGUSER=heeler_admin
#
psql $PGDATABASE -c "select file_name from heeler_v1.load_log where platform = 'rpi';" > fnamez.txt
#
