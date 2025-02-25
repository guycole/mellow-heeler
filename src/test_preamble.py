#
# Title: test_preamble.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from preamble import Preamble

from unittest import TestCase

class TestPreamble(TestCase):

    def test_bogus_geoloc(self):
        geo_loc = {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}

        preamble = Preamble()

        # missing site
        geo_loc = {}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": geo_loc}
        result = preamble.validate_preamble(candidate)
        assert result is None

        # development site
        geo_loc = {"site": "development"}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": geo_loc}
        result = preamble.validate_preamble(candidate)
        assert result is None

        # unknown site
        geo_loc = {"site": "unknown"}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": geo_loc}
        result = preamble.validate_preamble(candidate)
        assert result is None

    def test_missing_geoloc(self):
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764}
 
        preamble = Preamble()
        result = preamble.validate_preamble(candidate)
        assert result is None

    def test_missing_platform(self):
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
     
        preamble = Preamble()
        result = preamble.validate_preamble(candidate)
        assert result is None

    def test_missing_project(self):
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
        candidate = {"wifi": [], "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}

        preamble = Preamble()
        result = preamble.validate_preamble(candidate)
        assert result is None

    def test_missing_version(self):
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
        candidate = {"wifi": [], "project": "heeler", "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
    
        preamble = Preamble()
        result = preamble.validate_preamble(candidate)
        assert result is None

    def test_missing_ztime(self):
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "zTimeMs": 1708591321764, "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}
        candidate = {"wifi": [], "project": "heeler", "version": 1, "platform": "rpi", "geoLoc": {"site": "vallejo", "latitude": 38.1085, "longitude": -122.268}}

        preamble = Preamble()
        result = preamble.validate_preamble(candidate)
        assert result is None

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
