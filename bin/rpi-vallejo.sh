#!/bin/bash
#
# Title:wifi-scan.sh
# Description: scan for wireless access points (must run as root)
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start scan"
/sbin/iwlist > /tmp/iwlist.scan
echo "end scan"
