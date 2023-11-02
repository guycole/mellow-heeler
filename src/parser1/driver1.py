import psycopg2
from sqlalchemy import create_engine, select

from sql_table import GeoLoc

from sqlalchemy.orm import Session

# Try uninstalling psycopg2-binary, and installing it afresh, with the --no-cache-dir option,

if __name__ == '__main__':
	print("main noted")

	engine = create_engine('postgresql+psycopg2://heeler_py:bogus@localhost:5432/heeler_v1')

	statement = select(GeoLoc).where(GeoLoc.fix_time_ms > 0)
	with Session(engine) as session:
		for row in session.execute(statement):
			print(row)
