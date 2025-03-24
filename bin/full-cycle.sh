#!/bin/bash
#
# Title: full-cycle.sh
# Description: download collection files, parse and load to DB and generate report
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
HOME_DIR="/home/gsc/Documents/github"
#HOME_DIR="/Users/gsc/Documents/github"
#
time $HOME_DIR/mellow-heeler/bin/fresh-from-s3.sh
time $HOME_DIR/mellow-heeler/bin/loader.sh
time $HOME_DIR/mellow-heeler/bin/cooker.sh
time $HOME_DIR/mellow-heeler/bin/scorer.sh
time $HOME_DIR/mellow-heeler/bin/reporter.sh
#
