#!/bin/bash
#
# Title: rpi-xfer-wombat.sh
# Description: wombat pulls files from collector
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
COLLECTOR_HOST="gsc@rpi3a"
WORK_DIR="/var/mellow"
#
echo "start transfer"
cd $WORK_DIR
rsync --remove-source-files -avhze ssh $COLLECTOR_HOST:$WORK_DIR/heeler .
echo "end transfer"
#