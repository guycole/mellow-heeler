#!/bin/bash
#
# Title: rpi-iwlist.sh
# Description: scan for wireless access points (must run as root)
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
#
echo "start scan"
unlink $FILENAME
/sbin/iwlist scan > $FILENAME
chmod 666 $FILENAME
echo "end scan"
