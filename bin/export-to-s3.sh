#!/bin/bash
#
# Title: export-to-s3.sh
# Description: move exported files to s3
# Development Environment: macOS Monterey 12.6.9
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
TODAY=$(date '+%Y-%m-%d')
FILE_NAME="heeler-${TODAY}.tgz"
#
DEST_BUCKET=s3://mellow-heeler-uw2-k2718.braingang.net/exporter/
#
SOURCE_DIR="export"
WORK_DIR="/var/mellow/heeler"
#
echo "start export"
#
cd ${WORK_DIR}
tar -cvzf ${FILE_NAME} ${SOURCE_DIR}
#
echo "start s3 transfer" 
aws s3 mv ${FILE_NAME} $DEST_BUCKET --profile=wombat03rw
#
echo "cleanup"
rm -rf ${SOURCE_DIR}
mkdir ${SOURCE_DIR}
#
echo "end export"
#
