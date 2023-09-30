#!/bin/bash
#
# Title: rpi-xfer-vallejo.sh
# Description: move local files to s3
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start transfer"
cd /home/gsc/github/mellow-heeler
aws s3 mv aws_export s3://mellow-heeler.braingang.net/vallejo1 --profile=heeler-rpi --recursive
echo "end transfer"
