#!/bin/bash
#
# Title: s3-move.sh
# Description: move files from s3 to local file system
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
DEST_BUCKET=s3://mellow-heeler.braingang.net/anderson1
#
echo "start move"
cd /var/mellow/heeler/fresh; gzip *
aws s3 mv $DEST_BUCKET . --recursive --profile=cli_braingang
echo "end move"
