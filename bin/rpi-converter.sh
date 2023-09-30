#!/bin/bash
#
# Title: rpi-converter.sh
# Description: prepare a iwlist scan file for mellow heeler
# Development Environment: Debian 10 (buster)/raspian
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start conversion"
cd /home/gsc/github/mellow-heeler/src/converter_rpi
source venv/bin/activate
python3 ./driver.py
echo "end conversion"
