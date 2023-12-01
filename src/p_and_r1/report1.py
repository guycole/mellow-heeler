"""utility to refresh box_score table values"""

import sys

import calendar
import datetime
import jinja2

import pytz

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import yaml
from yaml.loader import SafeLoader

from postgres import PostGres
from sql_table import BoxScore

class YearRow:
    caption = None
    href = None

    def __init__(self, base_url:str, year: int):
        self.caption = str(year)
        self.href = f"{base_url}/{self.caption}.html"

class Reporter:
    """utility to create static HTML report"""

    base_url = None
    db_conn = None
    report_dir = None

    def __init__(self, base_url: str, report_dir:str, db_conn: str):
        self.base_url = base_url
        self.report_dir = report_dir
        self.db_conn = db_conn

    def write_index(self, environment: jinja2.environment.Environment):
        """write index.html"""
        date_time_stamp = datetime.datetime.utcnow()
        formatted_date_time = date_time_stamp.strftime('%Y-%b-%d %H:%M:%S')

        rows = []
        years = [2020, 2021, 2022, 2023]

        for ndx in years:
            rows.append(YearRow(self.base_url, ndx))

        template = environment.get_template("index.jinja")

        content = template.render(report_date=formatted_date_time, year_list=rows)

        out_filename = f"{self.report_dir}/index.html"
        with open(out_filename, mode="w", encoding="utf-8") as message:
            message.write(content)

    def execute(self):
        """write report"""

        db_engine = create_engine(self.db_conn, echo=True)
        postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False))

        environment = jinja2.Environment(loader=jinja2.FileSystemLoader("."))

        self.write_index(environment)

print("start report")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as exc:
            print(exc)

    driver = Reporter(configuration["baseUrl"], configuration["reportDir"], configuration["dbConn"])
    driver.execute()

print("stop report")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
