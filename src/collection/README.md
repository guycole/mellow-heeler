## rpi_iwlist

1. invoke iwlist(8) to scan for AP beacons
1. add JSON header to iwlist(8) output
1. mv updated scan file to /var/mellow/heeler/fresh
1. parse iwlist(8) output and post to mellow-skunk

The files in /var/mellow/heeler/fresh will be periodically collected.  

If this crate has a housekeeper, then housekeeper will collect, process and then forward to AWS.

In the absence of a housekeeper, another cronjob will forward to AWS.
