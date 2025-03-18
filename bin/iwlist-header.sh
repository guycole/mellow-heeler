#!/bin/bash
#
# Title: iwlist-header.sh
# Description: process output from iwlist(8) scan (not with gps)
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# * * * * * /home/gsc/Documents/github/mellow-heeler/bin/iwlist-header.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
HOME_DIR="/home/gsc/Documents/github"
#
echo "sleeping for iwlist"
sleep 10
#
echo "start conversion"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./header.py
echo "end conversion"
#
echo "start skunk post"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./skunk_post.py
echo "end skunk post"
#
echo "start wombat post"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./wombat_post.py
echo "end wombat post"
#
