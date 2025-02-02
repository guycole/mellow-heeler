#!/bin/bash
#
# Title: reporter.sh
# Description: product web reports
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start reporter"
cd /Users/gsc/Documents/github/mellow-heeler/src/p_and_r1
#cd /home/gsc/github/mellow-heeler/src/p_and_r1
source venv/bin/activate
time python3 ./report1.py
echo "end reporter"
