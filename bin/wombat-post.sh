#!/bin/bash
#
# Title: wombat-post.sh
# Description: process fresh heeler files to wombat django 
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# 17 18 * * * /home/gsc/Documents/github/mellow-heeler/bin/wombat-post.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
HOME_DIR="/home/gsc/Documents/github"
#
echo "start wombat post"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./wombat_post.py
echo "end wombat post"
#
