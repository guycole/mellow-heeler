import psycopg2
from sqlalchemy import create_engine
#from sqlalchemy.orm import sessionmaker
# Try uninstalling psycopg2-binary, and installing it afresh, with the --no-cache-dir option,

if __name__ == '__main__':
	print("main noted")

	engine = create_engine('postgresql+psycopg2://heeler_py:bogus@localhost:5431/heeler_v1')
