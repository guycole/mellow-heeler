#!/bin/bash
#
# Title: s3-move.sh
# Description: move files local file system to s3
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
DEST_BUCKET=s3://mellow-heeler-uw2-k2718.braingang.net/fresh/
#
echo "start move"
cd /var/mellow/heeler/fresh; gzip *
aws s3 mv . $DEST_BUCKET --recursive --profile=wombat01
echo "end move"
