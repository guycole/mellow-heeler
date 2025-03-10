#!/bin/bash
#
# Title: archiver.sh
# Description: move loaded files to archive
# Development Environment: macOS Monterey 12.6.9
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
TODAY=$(date '+%Y-%m-%d')
FILE_NAME="heeler-${TODAY}.tgz"
#
DEST_BUCKET=s3://mellow-heeler-uw2-k2718.braingang.net/archive/
#
SOURCE_DIR="archive"
WORK_DIR="/var/mellow/heeler"
#
echo "start archive"
#
cd ${WORK_DIR}
tar -cvzf ${FILE_NAME} ${SOURCE_DIR}
#
echo "start s3 transfer" 
aws s3 mv ${FILE_NAME} $DEST_BUCKET --profile=cli_braingang
#
echo "cleanup"
rm -rf ${SOURCE_DIR}
mkdir ${SOURCE_DIR}
#
echo "end archive"
#
