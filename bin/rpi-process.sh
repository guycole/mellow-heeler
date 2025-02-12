#!/bin/bash
#
# Title: rpi-process.sh
# Description: process output from iwlist(8) scan
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# 1,11,21,31,41,51 * * * * /home/gsc/Documents/github/mellow-heeler/bin/rpi-process.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
HOME_DIR="/home/gsc/Documents/github"
#
echo "start conversion"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./rpi_iwlist.py
echo "end conversion"
#
echo "start skunk post"
cd $HOME_DIR/mellow-heeler/src
source venv/bin/activate
python3 ./skunk_post.py
echo "end skunk post"
#
