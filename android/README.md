## Android is a sad story

This application only runs on old phones which can run WifiManager.startScan()

There used to be a Android collector for heeler, and it was nice because it had a GPS and could run in the background.  But Google decided this was too useful, and now background operations and WiFi scans are greatly restricted.  I have replaced Android with a dedicated rPi collector.

I used this application to verify that my rPi heeler gave the same results as old Android phones.

Run from Android Studio, output is to logcat.

./adb logcat | grep net.braingang.heeler > log.out

