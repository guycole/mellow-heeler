#!/bin/bash
#
# Title: iwlist-header-gps.sh
# Description: process output from iwlist(8) scan
# Development Environment: Ubuntu 22.04.05 LTS
# Author: Guy Cole (guycole at gmail dot com)
#
# * * * * * /home/gsc/Documents/github/mellow-heeler/bin/iwlist-header.sh > /dev/null 2>&1
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
FILENAME="/tmp/iwlist.scan"
HOME_DIR="/home/gsc/Documents/github"
#
echo "sleeping for iwlist"
sleep 5
#
counter=1
while [ $counter -le 4 ]
do
    echo "start conversion"
    cd $HOME_DIR/mellow-heeler/src
    source venv/bin/activate
    python3 ./header.py
    echo "end conversion"

    sleep 15
    ((counter++))
done
#