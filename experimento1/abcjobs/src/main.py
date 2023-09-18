from contextlib import asynccontextmanager
import datetime
from fastapi import FastAPI, Depends
from schemas import Status, Response
#from database import get_db_connection, close_db_connection
from core.config import settings
from db.session import engine 
from db.base import Base
from sqlalchemy.orm import Session
from db.session import get_db
from db.models import Question, Answer
from db.crud import get_question, get_answer
import math

@asynccontextmanager
async def lifespan(app: FastAPI):
  Base.metadata.create_all(bind=engine)
  yield
  print("finalizando")
  #close_db_connection()

app = FastAPI(title=settings.PROJECT_NAME, version=settings.PROJECT_VERSION, lifespan=lifespan)

@app.get(
  "/checkanswer/{question_id}/{answer_id}",
  description="Validar correctitud de respuesta",
  response_model=Response
)
def answer_check(question_id: int, answer_id: int, db: Session = Depends(get_db)): 
  #question = get_question(db, question_id=question_id)
  start_time = datetime.datetime.now()
  answer = get_answer(db, answer_id=answer_id)
  end_time = datetime.datetime.now()
  time_diff = (end_time - start_time)
  #execution_time = time_diff.total_seconds()
  execution_time = math.ceil(time_diff.microseconds/1000)
  if answer.question_id == question_id and answer.is_right :     
    return {"ok": True, "status": "Right", "message": "Right answer", "latency_ms": execution_time}
  else :
    return {"ok": True, "status": "Wrong", "message": "Wrong answer", "latency_ms": execution_time}

@app.get(
  "/ping",
  description="Ping the server to check if it's alive",
)
def ping() -> Status:
  return {"ok": True, "status": "pong", "message": "Server is alive"}

@app.get("/")
def root():
  return {"message": "pong"}
