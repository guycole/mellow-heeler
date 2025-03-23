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
$HOME_DIR/mellow-heeler/bin/fresh-from-s3.sh
$HOME_DIR/mellow-heeler/bin/loader.sh
$HOME_DIR/mellow-heeler/bin/cooker.sh
$HOME_DIR/mellow-heeler/bin/box-score.sh
$HOME_DIR/mellow-heeler/bin/reporter.sh
#
