#!/bin/bash
#
# Title: rpi-xfer-wombat.sh
# Description: move local files to wombat host
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
WOMBAT_HOST="gsc@rpi5a"
WORK_DIR="/var/mellow/heeler/fresh"
#
echo "start transfer"
cd $WORK_DIR
rsync -a . $WOMBAT_HOST:$WORK_DIR
echo "end transfer"
