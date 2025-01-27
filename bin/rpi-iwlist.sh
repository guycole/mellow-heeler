#!/bin/bash
#
# Title: rpi-iwlist.sh
# Description: scan for wireless access points
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# must run from root crontab or iwlist will not use wlan1
# */10 * * * * /home/gsc/Documents/github/mellow-heeler/bin/rpi-iwlist.sh > /dev/null 2>&1
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
#
