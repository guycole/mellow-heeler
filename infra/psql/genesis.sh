#!/bin/bash
#
# Title:genesis.sh
# Description:
# Development Environment: OS X 12.7.6/postgres 15.8
#
psql -U postgres template1 (or psql -U gsc template1)

# (mac) user
createuser -U gsc -d -e -l -P -r -s heeler_admin
woofwoof
createuser -U gsc -e -l -P heeler_client
batabat

# (linux) su - postgres
createuser -U postgres -d -e -l -P -r -s heeler_admin
woofwoof
createuser -U postgres -e -l -P heeler_client
batabat

# as pg superuser
# create tablespace heeler location '/mnt/pp1/postgres/heeler';
# create tablespace heeler location '/Library/PostgreSQL/pg_tablespace/heeler';
# create tablespace heeler location '/usr/local/opt/postgresql@15/pg_tablespace/heeler';
#
createdb heeler -O heeler_admin -D heeler -E UTF8 -T template0 -l C
createdb heeler -O heeler_admin -E UTF8 -T template0 -l C

# psql -h localhost -p 5432 -U heeler_admin -d heeler
# psql -h localhost -p 5432 -U heeler_client -d heeler

# as heeler_admin
create schema heeler_v1;
grant usage on schema heeler_v1 to heeler_client;

##
## old stuff
##
create user heeler_client with encrypted password 'batabat';
create database heeler owner heeler_admin tablespace heeler locale 'C.utf8' template template0;
create database heeler owner heeler_admin locale 'C.utf8' template template0;

create role heeler_py with login;
alter role heeler_py with password 'bogus';

psql -U heeler_py -d heeler_v1