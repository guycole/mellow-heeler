mellow-heeler
=============

[Wireless Access Point](https://en.wikipedia.org/wiki/Wireless_access_point) collection, processing and reporting applications.

## Introduction

A Mellow Heeler collector observes [wireless beacons](https://en.wikipedia.org/wiki/Beacon_frame) and shares the observation w/a Mellow Heeler backend for parsing, storage and reporting.

Fixed site collectors assets use a dedicated [Raspberry Pi](https://www.raspberrypi.org/) which periodically write WAP observations to AWS S3 for storage.  Mobile collection is performed using a [Raspberry Pi](https://www.raspberrypi.org/) augmented w/a [GPS](https://en.wikipedia.org/wiki/Global_Positioning_System) [HAT](https://www.raspberrypi.com/news/introducing-raspberry-pi-hats/) for location and time reporting, and also writes observations to AWS S3.

Back end processing reads collected observations from AWS S3 and loads the results into [postgresql](https://www.postgresql.org/) for analysis and reporting.  [Here](https://github.com/guycole/mellow-heeler/tree/master/reports) are some report examples.

## Deployment
![deployment](https://github.com/guycole/mellow-heeler/blob/master/md-uml/heeler-deployment.png)

## Features
1. Autonomous collection of wireless beacons. 
1. Beacon database.
1. Markdown based summary of observations.

## Notes
1.  All times UTC
1.  Location are +North, +East and truncated to fit a PostGreSQL float.
1.  Collectors typically have two WLAN devices: wlan0 is organic to rPi and wlan1 is a TP-Link AC-1300  wlan0 is configured for local WAP, and wlan1 is used for collection.

## Collection Applications
1.  bin/iwlist-header.sh reads the output of iwlist-scan.sh and produces a file with timestamp, location and other configuration details.
1.  bin/iwlist-scan.sh invokes iwlist(8) to scan for WAP and write to file
1.  bin/wombat*-to-s3.sh copy collected observations to AWS S3
1.  bin/skunk-post.sh updates local mellow skunk instance.

## Backend Applications
1.  bin/fresh-from-s3.sh copies from S3 to local file system
1.  bin/full_cycle.sh performs full downlowd, parse/load and report cycle
1.  bin/archiver.sh collects processed files into a tar(1) and save to AWS S3
 
## History

| Date       | Device | Site      | Version |
| ---------- | ------ | --------- | ------- |
| 2025-01-05 | rpi3c  | anderson1 | xxx     |
| 2025-02-14 | rpi3d  | mobile1   | xxx     |
| 2025-02-19 | rpi3a  | vallejo1  | xxx     |

