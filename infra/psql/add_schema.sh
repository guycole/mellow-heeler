#!/bin/bash
#
# Title:add_schema.sh
# Description:
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler_v1
export PGHOST=localhost
export PGPASSWORD=bogus
export PGUSER=heeler_py
#
psql < box_score.psql
psql < geoloc.psql
psql < wap.psql
psql < cooked.psql
psql < observation.psql
#
