#
# Title: heeler_app.py
# Description: driver for heeler application
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import os

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from scorer import Scorer
from validator import Validator
from postgres import PostGres

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger("heeler")


class HeelerApp:

    def __init__(self, score_limit, stunt_box: str):
        self.score_limit = score_limit
        self.stunt_box = stunt_box

        self.db_conn = "postgresql+psycopg2://wombat_client:batabat@host.docker.internal:5432/wombat"
        db_engine = create_engine(self.db_conn, echo=False)
        self.postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False))

    def execute(self) -> None:
        logger.info(f"heeler execute:{self.stunt_box}")

        if self.stunt_box == "score":
            scorer = Scorer(self.postgres)
            scorer.scorer(self.score_limit)
        elif self.stunt_box == "validate":
            validator = Validator(self.postgres)
            validator.validate()
        else:
            logger.error(f"invalid stunt_box option:{self.stunt_box}")
            return

if __name__ == "__main__":
    # stunt_box options: "score" and "validate"
    score_limit = os.environ.get("limit", -1)
    stunt_box = os.environ.get("stuntbox", "validate")
  
    app = HeelerApp(int(score_limit), stunt_box)
    app.execute()

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
