#
# Title: test_capability.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
from unittest import TestCase

from heeler_capability import HeelerCapabilityV1


class TestCapability(TestCase):
    def test_heeler_cabability01(self):
        # ATTWIMZwjA
        # [WPA2-PSK-CCMP][WPS][ESS]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : CCMP")
        test_text1.append("Pairwise Ciphers (1) : CCMP")
        test_text1.append("Authentication Suites (1) : PSK")

        hc = HeelerCapabilityV1()

        wrapper = {}
        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA2-PSK-CCMP]"

    def test_heeler_cabability02(self):
        # TRENDnet740_QCDJ
        # [WPA2-EAP-CCMP][ESS]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : TKIP")
        test_text1.append("Pairwise Ciphers (1) : CCMP")
        test_text1.append("Authentication Suites (1) : 802.1x")

        hc = HeelerCapabilityV1()

        wrapper = {}
        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA2-EAP-CCMP]"

    def test_heeler_cabability03(self):
        # Alcatel_linkzone 2_4802_2.4GHz
        # [WPA2-PSK-CCMP+TKIP][WPS][ESS]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : TKIP")
        test_text1.append("Pairwise Ciphers (2) : CCMP TKIP")
        test_text1.append("Authentication Suites (1) : PSK")

        hc = HeelerCapabilityV1()

        wrapper = {}
        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA2-PSK-CCMP+TKIP]"

    def test_heeler_capability05(self):
        # empty ssid
        # bssid da:55:a8:27:6d:94
        # [WPA2-UNKNOWN+PSK-UNKNOWN+CCMP]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : CCMP")
        test_text1.append("Pairwise Ciphers (2) : unknown (10) CCMP")
        test_text1.append("Authentication Suites (2) : PSK unknown (6)")

        hc = HeelerCapabilityV1()

        wrapper = {}

        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA2-UNKNOWN+PSK-UNKNOWN+CCMP]"

    def test_heeler_capability06(self):
        # SunPower21450
        # [WPA2-UNKNOWN+CCMP-PSK+UNKNOWN]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : TKIP")
        test_text1.append("Pairwise Ciphers (1) : TKIP")
        test_text1.append("Authentication Suites (1) : PSK")

        hc = HeelerCapabilityV1()

        wrapper = {}

        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA2-PSK-TKIP]"

    def test_heeler_capability07(self):
        # Gate K
        # [WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS]
        # iwlist not report WPS or ESS

        test_text1 = []
        test_text1.append("IE: IEEE 802.11i/WPA2 Version 1")
        test_text1.append("Group Cipher : TKIP")
        test_text1.append("Pairwise Ciphers (2) : CCMP TKIP")
        test_text1.append("Authentication Suites (1) : PSK")

        test_text2 = []
        test_text2.append("IE: WPA Version 1")
        test_text2.append("Group Cipher : TKIP")
        test_text2.append("Pairwise Ciphers (2) : CCMP TKIP")
        test_text2.append("Authentication Suites (1) : PSK")

        hc = HeelerCapabilityV1()

        wrapper = {}

        wrapper["wpa2v1"] = hc.get_wpa(test_text1)
        assert len(wrapper["wpa2v1"]) == 4

        wrapper["wpa1v1"] = hc.get_wpa(test_text2)
        assert len(wrapper["wpa1v1"]) == 4

        capability = hc.build_capability(wrapper)
        assert capability == "[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP]"


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
