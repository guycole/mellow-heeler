mellow-heeler
=============

[Wireless Access Point](https://en.wikipedia.org/wiki/Wireless_access_point) collection, processing and reporting applications.

## Introduction
A Mellow Heeler client observes [wireless beacons](https://en.wikipedia.org/wiki/Beacon_frame) and shares the observation w/a Mellow Heeler backend for parsing, storage and reporting.

Fixed site Mellow Heeler collection assets use a dedicated [Raspberry Pi 4](https://www.raspberrypi.org/).  Mobile collection is performed using a [BeagleBone Black](https://www.beagleboard.org/boards/beaglebone-black) augmented w/a USB WiFi adapter such as TP Link AC1300.

## Features
1. Autonomous collection of wireless beacons for 2.4 and 5 GHz.  

2. Beacon database.

3. HTML based summary of observations.

## Applications
1.  bin/parser.sh to parse collection files and load into postgres
1.  bin/s3-move.sh to move collection files from AWS S3 to local 
 

## History
1. Mellow Hound original WAP collector.  Based on Android, the output report is json formatted.  Sadly, modern Android makes it difficult to run background applications and this project is abandoned.  There are many legacy files, and the format is still supported as "hound_1".  Most (if not all) of these observations are 2.4 GHz and will include a GPS location and timestamp.

2. Mellow Heeler "heeler_1" has fixed location collection stations based on rPi4 using iwlist(8) scan output.  There is a json header to identify observation time and site and the raw iwlist(8) output.  Since these are static location collection stations, there is not much variety in observations.  iwlist(8) does not report on P2P, WPS or IBSS capabilities.

3. Mellow Heeler "heeler_2" has fixed location and mobile collection stations.  Based on BeagleBone Black, the output report is json formatted.
