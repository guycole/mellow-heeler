#
# Title: domain.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
class Observation:
    def __init__(
        self,
        accuracy,
        altitude,
        fix_time_ms,
        latitude,
        longitude,
        observation_key,
        observed_at,
        project,
        version,
    ):
        self.accuracy = accuracy
        self.altitude = altitude
        self.fix_time_ms = fix_time_ms
        self.latitude = latitude
        self.longitude = longitude
        self.observation_key = observation_key
        self.observed_at = observed_at
        self.project = project
        self.version = version

    def __str__(self):
        return (
            "observation: accuracy:%d altitude:%d fix_time_ms:%d latitude:%s longitude:%s observation_key:%s observed_at:%s project:%d version:%d"
            % (
                self.accuracy,
                self.altitude,
                self.fix_time_ms,
                self.latitude,
                self.longitude,
                self.observation_key,
                self.observed_at,
                self.project,
                self.version,
            )
        )


class CookedSample:
    def __init__(
        self,
        bssid,
        capability,
        frequency,
        latitude,
        longitude,
        observed_first,
        observed_last,
        population,
        ssid,
    ):
        self.bssid = bssid
        self.capability = capability
        self.frequency = frequency
        self.latitude = latitude
        self.longitude = longitude
        self.observed_first = observed_first
        self.observed_last = observed_last
        self.population = population
        self.ssid = ssid

    def __str__(self):
        return (
            "cooked_sample: bssid:%s capability:%s frequency:%d latitude:%d longitude:%d observed_first:%s observed_last:%s population:%d ssid:%s"
            % (
                self.bssid,
                self.capability,
                self.frequency,
                self.latitude,
                self.longitude,
                self.observed_first,
                self.observed_last,
                self.population,
                self.ssid,
            )
        )


class RawSample:
    def __init__(
        self,
        bssid,
        capability,
        frequency,
        level,
        observation_key,
        ssid,
    ):
        self.bssid = bssid
        self.capability = capability
        self.frequency = frequency
        self.level = level
        self.observation_key = observation_key
        self.ssid = ssid

    def __str__(self):
        return (
            "raw_sample: bssid:%s capability:%s frequency:%d level:%d observation_key:%s ssid:%s"
            % (
                self.bssid,
                self.capability,
                self.frequency,
                self.level,
                self.observation_key,
                self.ssid,
            )
        )


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
