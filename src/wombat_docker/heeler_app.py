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

    def __init__(self, stuntbox: str):
        self.stuntbox = stuntbox

        self.db_conn = "postgresql+psycopg2://wombat_client:batabat@host.docker.internal:5432/wombat"
        db_engine = create_engine(self.db_conn, echo=False)
        self.postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False))

    def execute(self) -> None:
        logger.info(f"heeler execute:{self.stuntbox}")

        if self.stuntbox == "score":
            scorer = Scorer(self.postgres)
            scorer.scorer()
        elif self.stuntbox == "validate":
            validator = Validator(self.postgres)
            validator.validate()
        else:
            logger.error(f"invalid stuntbox option:{self.stuntbox}")
            return


if __name__ == "__main__":
    # stuntbox options: "score" and "validate"
    stuntbox = os.environ.get("stuntbox", "validate")
    app = HeelerApp(stuntbox)
    app.execute()

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
