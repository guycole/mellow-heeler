#!/bin/bash
#
# Title:drop_schema.sh
# Description: remove schema
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler
export PGHOST=localhost
export PGPASSWORD=woofwoof
export PGUSER=heeler_admin
#
psql $PGDATABASE -c "drop table heeler_v1.daily_score"
psql $PGDATABASE -c "drop table heeler_v1.weekly_rank_detail"
psql $PGDATABASE -c "drop table heeler_v1.weekly_rank"
psql $PGDATABASE -c "drop table heeler_v1.observation"
psql $PGDATABASE -c "drop table heeler_v1.cooked"
psql $PGDATABASE -c "drop table heeler_v1.wap"
psql $PGDATABASE -c "drop table heeler_v1.load_log"
psql $PGDATABASE -c "drop table heeler_v1.geo_loc"
#
