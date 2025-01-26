#!/bin/bash
#
# Title: box_score.sh
# Description: refactor statistics for box_score table
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start box_score"
cd /Users/gsc/Documents/github/mellow-heeler/src/p_and_r1
#cd /home/gsc/github/mellow-heeler/src/p_and_r1
source venv/bin/activate
time python3 ./box_score.py
echo "end box_score"
