from contextlib import asynccontextmanager
import datetime
from fastapi import FastAPI, Depends
from schemas import schemas 
from core.config import settings
from db.session import engine 
from db.base import Base
from sqlalchemy.orm import Session
from db.session import get_db
from db.models import Question, Answer
from db.crud import get_question, create_new_question, get_answer, create_new_answer, \
                    trunc_questions, trunc_answers
import math
import psutil
import random

@asynccontextmanager
async def lifespan(app: FastAPI):
  Base.metadata.create_all(bind=engine)
  yield
  print("finalizando")

app = FastAPI(title=settings.PROJECT_NAME, version=settings.PROJECT_VERSION, lifespan=lifespan)

def convert_to_percent(load_tuple):
  num_log_cpus = psutil.cpu_count()
  percent_lst = []

  for load in load_tuple:
    percent = (load / num_log_cpus) * 100
    percent_lst.append(percent)
  
  return tuple(percent_lst)

@app.get(
  "/checkanswer/{question_id}/{answer_id}",
  description="Validar correctitud de respuesta",
  response_model=schemas.Response
)
def answer_check(question_id: int, answer_id: int, db: Session = Depends(get_db)): 
  #question = get_question(db, question_id=question_id)
  start_time = datetime.datetime.now()
  answer = get_answer(db, answer_id=answer_id)
  is_right  = answer and answer.question_id == question_id and answer.is_right
  end_time = datetime.datetime.now()
  time_diff = (end_time - start_time)
  execution_time = math.ceil(time_diff.microseconds/1000)
  cpu_load_avg = convert_to_percent(psutil.getloadavg())
  cpu_percent = cpu_load_avg[0]
  #cpu_percent = psutil.cpu_percent()
  mem = psutil.virtual_memory()
  mem_percent = mem.percent

  status = "Wrong"
  message = "Wrong answer"
  
  if is_right :     
    status = "Right"
    message = "Right answer"
    
  return {"ok": True, "status": status, "message": message, 
          "latency_ms": execution_time, "cpu_perc": cpu_percent, "mem_perc": mem_percent}

@app.get(
  "/truncquizes",
  description="Borrar todos los datos de la tablas question y responses"
)
def trunc_quizes(db: Session = Depends(get_db)): 
  truncated_answers = trunc_answers(db)
  truncated_questions = trunc_questions(db)
  truncated = truncated_answers and truncated_questions
  return {"ok": True, "status": "ok", "message": truncated} 

@app.get(
  "/populatequizes/{questions_cant}/{answers_cant}",
  description="Poblar preguntas y respuestas"
)
def populate_quizes(questions_cant: int, answers_cant: int, db: Session = Depends(get_db)): 
  for i in range(questions_cant):
    question = schemas.Question(id=i+1, question=f"Pregunta {i+1}")    
    create_new_question(question=question, db=db)
    for j in range(answers_cant):
      answer_id = (answers_cant * i) + j + 1
      answer = schemas.Answer(id=answer_id, answer=f"Respuesta {answer_id}", is_right=random.choice([True, False]))
      create_new_answer(answer=answer,db=db,question_id=question.id)
  
  return {"ok": True, "status": "Ok", "message": "Quizes populated"}

@app.get( 
  "/ping",
  description="Ping the server to check if it's alive",
)
def ping() -> schemas.Status:
  return {"ok": True, "status": "pong", "message": "Server is alive"}

@app.get("/")
def root():
  return {"message": "pong"}
