#!/bin/bash
#
# Title: reporter.sh
# Description: write box scores to md files
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
HOME_DIR="/home/gsc/Documents/github"
#HOME_DIR="/Users/gsc/Documents/github"
#
echo "start reporter"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./reporter.py
echo "end reporter"
#
