#
# Title: postgres.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
# import sqlalchemy
# from sqlalchemy import and_
# from sqlalchemy import select

import observation


class PostGres:
    """mellow heeler postgresql support"""

    db_engine = None

    def __init__(self, db_connection: str):
        pass

    #    def __init__(self, session: sqlalchemy.orm.session.sessionmaker):
    #        self.Session = session

    def load_observations(self, obs_list: list[observation.Observation]):
        pass


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
