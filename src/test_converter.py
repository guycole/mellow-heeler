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
        """scan file without preamble is a failure"""

        file_name = "sample1.scan"

        converter = Converter()
        assert converter.converter(file_name) is False

    def test2(self):
        """short file without iwlist(8) scan is a failure"""

        file_name = "../samples/ff948343-0167-45f6-89de-d6691cea9109"

        converter = Converter()
        assert converter.converter(file_name) is False

    def test3(self):
        """success with one observation"""

        file_name = "../samples/8755869d-471f-460a-a2eb-ff8eb667629f"

        converter = Converter()
        assert converter.converter(file_name) is True
        assert len(converter.obs_list) == 1

        for obs in converter.obs_list:
            if obs["ssid"] == "braingang2_2GEXT":
                assert len(obs) == 5
                assert obs["bssid"] == "6c:cd:d6:2a:62:06"
                assert obs["frequency_mhz"] == 2437
                assert obs["signal_dbm"] == -65
                assert obs["ssid"] == "braingang2_2GEXT"


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
