#
# Title: gps_wrapper.py
# Description: gps logic
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import pygpsd
import sys
import time
import yaml

from yaml.loader import SafeLoader


class GpsSample:
    elements = {}
    file_name = "/tmp/gps.json"

    def __init__(self, datum: pygpsd.type.data.Data):
        errors = {}
        errors["epc"] = datum.geo.errors.epc
        errors["epd"] = datum.geo.errors.epd
        errors["eph"] = datum.geo.errors.eph
        errors["epv"] = datum.geo.errors.epv
        errors["epx"] = datum.geo.errors.epx
        errors["epy"] = datum.geo.errors.epy

        self.elements["mode"] = int(datum.mode)
        self.elements["altitude"] = datum.geo.position.altitude
        self.elements["errors"] = errors
        self.elements["latitude"] = datum.geo.position.latitude
        self.elements["longitude"] = datum.geo.position.longitude
        self.elements["speed"] = datum.geo.trajectory.speed
        self.elements["fixTime"] = int(datum.time.timestamp())
        self.elements["track"] = datum.geo.trajectory.track

    def __str__(self):
        return f"{self.elements['fixTime']} {self.elements['speed']} {self.elements['latitude']} {self.elements['longitude']}"

    def get_speed(self) -> float:
        if self.elements is not None:
            if "speed" in self.elements:
                return self.elements["speed"]

        return 0.0

    def reader(self) -> None:
        try:
            with open(self.file_name, "r") as infile:
                self.elements = json.load(infile)
        except Exception as error:
            print(error)

    def writer(self) -> None:
        try:
            with open(self.file_name, "w") as outfile:
                json.dump(self.elements, outfile, indent=4)
        except Exception as error:
            print(error)


class GpsWrapper:
    file_name = "/tmp/gps.counter"
    idle_limit = 10

    def __init__(self):
        self.gpsd = pygpsd.GPSD()

    def idle_reader(self) -> int:
        try:
            with open(self.file_name, "r") as infile:
                buffer = infile.readline()
                return int(buffer)
        except Exception as error:
            print(error)

        return 0

    def idle_writer(self, arg: int) -> None:
        try:
            with open(self.file_name, "w") as outfile:
                outfile.write(str(arg))
        except Exception as error:
            print(error)

    def gps_sample(self) -> GpsSample:
        attempts = 3
        while attempts > 0:
            attempts = attempts - 1
            time.sleep(1)

            datum = self.gpsd.poll()
            if int(datum.mode) == 3:  # 3d fix
                return GpsSample(datum)

        return None

    def run_test(self) -> GpsSample:
        sample = self.gps_sample()
        if sample is None:
            print("unable to obtain GPS datum")
            return None

        if sample.get_speed() > 10.0:
            print("speed threshold met, must take sample")
            self.idle_writer(self.idle_limit)
            return sample

        # must be stationary, sample every ten attempts
        ndx = self.idle_reader()
        if ndx < 1:
            print("return idle sample")
            self.idle_writer(self.idle_limit)
            return sample
        else:
            print("skipping this attempt")
            self.idle_writer(ndx - 1)
            return None

    def execute(self):
        sample = self.run_test()
        if sample is None:
            print("skipping collection")
        else:
            print("work to do")


#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        file_name = sys.argv[1]
    else:
        file_name = "config.yaml"

    with open(file_name, "r") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)

    gps_wrapper = GpsWrapper()
    gps_wrapper.execute()

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
