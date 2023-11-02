#!/bin/bash
#
# Title:drop_schema.sh
# Description: remove schema
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=heeler_v1
export PGHOST=localhost
export PGPASSWORD=bogus
export PGUSER=heeler_py
#
#psql $PGDATABASE -c "drop table sample_cooked"
#psql $PGDATABASE -c "drop table sample_raw"
psql $PGDATABASE -c "drop table observation"
psql $PGDATABASE -c "drop table box_score"
psql $PGDATABASE -c "drop table geoloc"
psql $PGDATABASE -c "drop table wap"
#
