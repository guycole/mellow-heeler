#!/bin/bash
#
# Title: xfer-to-rpi5a.sh
# Description: move local files to rpi5a, needs ssh authorized_keys for $DEST
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin:~/.local/bin; export PATH
#
DEST="gsc@rpi5a:/mnt/pp1/gsc/mellow/heeler/fresh"
DEST="gsc@rpi5a:/var/mellow/heeler/fresh"
SRC="/var/mellow/heeler/fresh"
#
echo "start transfer"
cd $SRC
rsync --ignore-existing --remove-source-files -avhe ssh . $DEST
echo "end transfer"
#
