#!/bin/bash
#
# Title: rpi-collection.sh
# Description: scan for wireless access points
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
# */10 * * * * /home/gsc/Documents/github/mellow-heeler/bin/rpi-collection.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
HOME_DIR="/home/gsc/Documents/github"
#
echo "start scan"
unlink $FILENAME
/sbin/iwlist scan > $FILENAME
chmod 666 $FILENAME
echo "end scan"
#
echo "start conversion"
cd $HOME_DIR/mellow-heeler/src/collection
source venv/bin/activate
python3 ./rpi_iwlist.py
echo "end conversion"
#
echo "skunk load"
echo "skunk load"
#