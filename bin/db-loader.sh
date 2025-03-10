#!/bin/bash
#
# Title: db-loader.sh
# Description: parse and load iwlist files to postgres
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# * * * * * /home/gsc/Documents/github/mellow-heeler/bin/iwlist-header.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
HOME_DIR="/home/gsc/Documents/github"
HOME_DIR="/Users/gsc/Documents/github"
#
echo "start load"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./loader.py
echo "end load"
#
