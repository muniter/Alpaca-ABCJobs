import psycopg2
from time import sleep
import os

""" def get_db_connection(initial=False, max_attempts=5, wait_time=3):
  print("connect to database") """
  
def close_db_connection():
  print("disconnect to database")
  
def _get_db_connection(initial=False):
  host = os.environ.get("RDS_HOSTNAME", "")
  port = os.environ.get("RDS_PORT", 5432)
  user = os.environ.get("RDS_USERNAME", "postgres")
  password = os.environ.get("RDS_PASSWORD", "postgres")
  database = os.environ.get("RDS_DB_NAME", "postgres")
  print(
    f"Database connection info: host: {host}, port: {port}, user: {user}, password: {password}, database: {database}"
  )
  conn = psycopg2.connect(
    host=host,
    database=database,
    user=user,
    password=password,
    port=port,
  )
  return conn

def get_db_connection(initial=False, max_attempts=5, wait_time=3):
  # Implement waiting for the database to be ready
  attempts = 0
  while True:
    attempts += 1
    try:
      return _get_db_connection(initial)
    except psycopg2.OperationalError as e:
      print("Could not connect to database, trying again")
      if attempts > max_attempts:
        print("Could not connect to database, giving up")
        raise e
      sleep(wait_time)