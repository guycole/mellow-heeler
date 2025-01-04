# mellow-heeler postgres
PostGreSQL 12.16 (heeler_v1 database)

## BOX_SCORE
+ one row daily and device
+ simple collection stats

## COOKED
+ estimated location
+ observed population
+ observed first and last time
+ and wap

## GEOLOC
+ for fixed sites, geographic location only
+ for mobile observations, geographic location and time stamp

## OBSERVATION
+ map geoloc, wap, time stamp and signal level

## WAP
+ each wireless access point beacon has at least one row 
+ bssid is primary key, version is monotonic secondary key
+ if the capability, frequency or SSID changes, then a new version row is created
+ it is not unusual for a WAP to change frequencies or SSID
+ frequency is always a 4 digit integer, implied to be 2.4 GHz or 5 GHz