#!/bin/bash
#
# Title:drop_schema.sh
# Description: remove schema
# Development Environment: OS X 10.15.2/postgres 9.6.16
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=turtle_v1
export PGHOST=localhost
export PGPASSWORD=goboy
export PGUSER=turtle_go
#
psql $PGDATABASE -c "drop table note"
psql $PGDATABASE -c "drop table suspend"
psql $PGDATABASE -c "drop table group_member"
psql $PGDATABASE -c "drop table group_header"
psql $PGDATABASE -c "drop table cognito_user"
#
psql $PGDATABASE -c "drop table visit_tracker"
psql $PGDATABASE -c "drop table visit_event"
#
