import logging
import os

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from file_helper import FileHelper
from postgres import PostGres

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger("heeler")

class HeelerApp:
    def __init__(self, stuntbox: str):
        self.stuntbox = stuntbox

        self.failure_dir = "/mnt/wombat/heeler/failure/"
        self.fresh_dir = "/mnt/wombat/fresh/heeler"
        self.success_dir = "/mnt/wombat/heeler/success/"

        self.failure = 0
        self.success = 0

        self.db_conn = "postgresql+psycopg2://wombat_client:batabat@host.docker.internal:5432/wombat"
        db_engine = create_engine(self.db_conn, echo=False)
        self.postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False))

    def file_failure(self, file_name: str):
        logger.info(f"file failure:{file_name}")

        self.failure += 1
        os.rename(file_name, self.failure_dir + file_name)

    def file_success(self, file_name: str):
        # logger.info(f"file success:{file_name}")

        self.success += 1
        # os.rename(file_name, self.success_dir + "/" + file_name)

    def file_processor(self, file_name: str) -> None:
        if os.path.isfile(file_name) is False:
            logger.warning(f"skipping non-file:{file_name}")
            return

        file_helper = FileHelper()
        if file_helper.converter(file_name):
            db_args = file_helper.load_log()
            if len(db_args) > 0:
                logger.info(f"db_args:{db_args}")
                try:
                    self.postgres.load_log_insert(db_args)
                    self.file_success(file_name)
                except Exception as error:
                    logger.error(f"Postgres insert failed for {file_name}: {error}")
                    self.file_failure(file_name)
            else:
                logger.error(f"invalid db_args for file:{file_name}")
                self.file_failure(file_name)
        else:
            self.file_failure(file_name)

    def daily_score(self):
        pass

    def validate(self):
        logger.info(f"fresh dir:{self.fresh_dir}")
        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        logger.info(f"{len(targets)} files noted")

        for target in targets:
            self.file_processor(target)

        logger.info(f"success:{self.success} failure:{self.failure}")

    def execute(self) -> None:
        logger.info(f"heeler execute:{self.stuntbox}")

        if self.stuntbox == "score":
            self.daily_score()
        elif self.stuntbox == "validate":
            self.validate()
        else:
            logger.error(f"invalid stuntbox option:{self.stuntbox}")
            return

if __name__ == "__main__":
    # stuntbox options: "score" and "validate"
    stuntbox = os.environ.get("stuntbox", "validate")
    app = HeelerApp(stuntbox)
    app.execute()
