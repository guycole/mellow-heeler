#
# Title: test_converter.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from converter import Converter

from unittest import TestCase


class TestConverter(TestCase):

    def test1(self):
        file_name = "../ffe08060-3068-4424-bd9d-d23507d53c87"
        file_name = "sample1.scan"

        converter = Converter()
        observations = converter.converter(file_name)
        assert len(observations) == 20

        for obs in observations:
            if obs["ssid"] == "Gate K":
                assert len(obs) == 5
                assert obs["bssid"] == "3e:8c:f8:f9:2b:bb"
                assert obs["frequency_mhz"] == 2412
                assert obs["signal_dbm"] == -57
                assert obs["ssid"] == "Gate K"

    def test2(self):
        file_name = "sample2.scan"

        converter = Converter()
        observations = converter.converter(file_name)
        assert len(observations) == 10

        for obs in observations:
            if obs["ssid"] == "SunPower21450":
                print(obs)
                assert len(obs) == 5
                assert obs["bssid"] == "6e:21:a2:a4:cd:94"
                assert obs["frequency_mhz"] == 2412
                assert obs["signal_dbm"] == -91
                assert obs["ssid"] == "SunPower21450"


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
