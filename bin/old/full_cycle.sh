#!/bin/bash
#
# Title: full_cycle.sh
# Description: download collection files, parse and load to DB and generate report
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start full_cycle"
/Users/gsc/Documents/github/mellow-heeler/bin/s3-move.sh
/Users/gsc/Documents/github/mellow-heeler/bin/parser.sh
/Users/gsc/Documents/github/mellow-heeler/bin/box_score.sh
/Users/gsc/Documents/github/mellow-heeler/bin/reporter.sh
/Users/gsc/Documents/github/mellow-heeler/bin/web-copy.sh
echo "end full_cycle"
