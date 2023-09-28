#!/bin/bash
#
# Title:add_schema.sh
# Description:
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
export PGDATABASE=turtle_v1
export PGHOST=localhost
export PGPASSWORD=goboy
export PGUSER=turtle_go
#
psql < cognito_user.psql
#
psql -c "insert into cognito_user(cognito_key, user_email, user_name) values('cb6925a5-ab93-4558-bed6-a0d2b53c2221', 'anon@zapanote.com', 'anonymous')"
psql -c "insert into cognito_user(cognito_key, user_email, user_name) values('b105fe8b-1505-45a7-8e53-f98ec45f5479', 'zapabot@zapanote.com', 'zapabot')"
#
psql < group_header.psql
#
psql -c "insert into group_header(group_key, group_name, owner_id) values('f041876a-0afb-4042-bea4-e4fa0af27021', 'test group', 1)"
#
psql < group_member.psql
#
psql -c "insert into group_member(group_id, member_id) values(1, 1)"
#
psql < note.psql
#
psql -c "insert into note(mask, note, note_key, private_flag, group_id) values(0, 'public note', 'f041876a-0afb-4042-bea4-e4fa0af27021', false, 1)"
psql -c "insert into note(mask, note, note_key, private_flag, group_id) values(0, 'private note', 'f041876a-0afb-4042-bea4-e4fa0af27022', true, 1)"
#
psql < suspend.psql
#
psql < visit_event.psql
psql < visit_tracker.psql
#
