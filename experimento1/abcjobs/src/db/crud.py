from sqlalchemy.orm import Session
from sqlalchemy.sql import text
from db.models import Question, Answer 
from schemas import schemas

def get_question(db: Session, question_id: int):
    return db.query(Question).filter(Question.id == question_id).first()
  
def create_new_question(question: schemas.Question, db: Session):
    question = Question(**question.dict())
    db.add(question)
    db.commit()
    db.refresh(question)
    return question
  
def get_answer(db: Session, answer_id: int):
    return db.query(Answer).filter(Answer.id == answer_id).first()
  
def create_new_answer(answer: schemas.Answer, db: Session, question_id: int):
    answer = Answer(**answer.dict(), question_id=question_id)
    db.add(answer)
    db.commit()
    db.refresh(answer)
    return answer
  
def trunc_questions(db: Session):
  db.execute(text('TRUNCATE TABLE question CASCADE'))
  db.commit()
  return True  

def trunc_answers(db: Session):
  db.execute(text('TRUNCATE TABLE answer CASCADE'))
  db.commit()
  return True
  
