#!/bin/bash
#
# Title: rpi-xfer-anderson.sh
# Description: move local files to s3
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start transfer"
cd /home/gsc/Documents/github/mellow-heeler
aws s3 mv aws-export s3://mellow-heeler.braingang.net/anderson1 --profile=wombat01 --recursive
echo "end transfer"
