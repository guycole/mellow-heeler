#!/bin/bash
#
# Title: box-score.sh
# Description: create collection statistics
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
HOME_DIR="/home/gsc/Documents/github"
#HOME_DIR="/Users/gsc/Documents/github"
#
echo "start box-score"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./box_score.py
echo "end box-score"
#
