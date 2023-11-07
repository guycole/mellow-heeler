#!/bin/bash
#
# Title:genesis.sh
# Description:
# Development Environment: OS X 10.15.2/postgres 12.12
# Author: G.S. Cole (guy at shastrax dot com)
#
# psql -h localhost -p 5432 -U heeler_py -d heeler_v1
#
psql -U postgres template1 (or psql -U gsc template1)
create database heeler_v1;

createuser -U gsc -d -e -E -l -P -r -s heeler_admin
woofwoof

create role heeler_py with login;
alter role heeler_py with password 'bogus';

psql -U heeler_py -d heeler_v1
