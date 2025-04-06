#!/bin/bash
#
# Title:add_schema.sh
# Description:
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
# psql -U heeler_py -d heeler
#
export PGDATABASE=heeler
export PGHOST=localhost
export PGPASSWORD=woofwoof
export PGUSER=heeler_admin
#
psql < daily_score.psql
psql < geo_loc.psql
psql < load_log.psql
psql < wap.psql
psql < weekly_rank.psql
psql < weekly_rank_detail.psql
psql < cooked.psql
psql < observation.psql
#
