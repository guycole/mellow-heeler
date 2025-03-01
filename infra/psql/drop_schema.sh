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
psql $PGDATABASE -c "drop table heeler_v1.observation"
psql $PGDATABASE -c "drop table heeler_v1.load_log"
#psql $PGDATABASE -c "drop table box_score"
psql $PGDATABASE -c "drop table heeler_v1.geoloc"
#psql $PGDATABASE -c "drop table cooked"
psql $PGDATABASE -c "drop table heeler_v1.wap"
#
