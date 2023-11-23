#
# Title: test_box_score_date.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
from unittest import TestCase

from box_score import BoxScoreDateRange

class UtcDateBucket(TestCase):
    def test_utc_date_bucket(self):
        bsdr = BoxScoreDateRange()
        bsdr.date_range(1698813722129)
        

# content of test_sample.py
#def inc(x):
#    return x + 1


#def test_answer():
#    assert inc(3) == 5


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
