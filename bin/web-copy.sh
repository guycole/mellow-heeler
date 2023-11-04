#!/bin/bash
#
# Title: web-copy.sh
# Description: update web contents
# Development Environment: macOS Monterey 12.6.9
# Author: Guy Cole (guycole at gmail dot com)
#
PATH=/bin:/usr/bin:/etc:/usr/local/bin; export PATH
#
echo "start web copy" 
cd /Users/gsc/Documents/github/mellow-heeler/src
aws s3 cp web1/* s3://braingang.net/mellow-heeler/ --profile=cli_braingang
echo "end web copy" 
