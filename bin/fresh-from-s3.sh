#!/bin/bash
#
# Title: fresh-from-s3.sh
# Description: move fresh s3 files to local for database import
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# 29 * * * * /home/gsc/github/mellow-heeler/bin/rpi-xfer-vallejo.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
SRC_BUCKET=s3://mellow-heeler-uw2-k2718.braingang.net/fresh/
#SRC_BUCKET=s3://mellow-heeler.braingang.net/vallejo1/
WORK_DIR="/var/mellow/heeler/fresh"
#WORK_DIR="/mnt/pp1/gsc/mellow/heeler/fresh"
#
echo "start move"
cd $WORK_DIR
aws s3 mv $SRC_BUCKET . --recursive --profile=wombat03rw
gunzip *
echo "end move"
#
