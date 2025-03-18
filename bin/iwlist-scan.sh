#!/bin/bash
#
# Title: iwlist-scan.sh
# Description: scan for wireless access points
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# must run from root crontab or iwlist will not use wlan1
# * * * * * /home/gsc/Documents/github/mellow-heeler/bin/iwlist-scan.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
#
counter=1
echo "start scan"
unlink $FILENAME
/sbin/iwlist scan > $FILENAME
chmod 666 $FILENAME
#
