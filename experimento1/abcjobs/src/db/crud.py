from sqlalchemy.orm import Session
from db.models import Question, Answer 

def get_question(db: Session, question_id: int):
    return db.query(Question).filter(Question.id == question_id).first()
  
def get_answer(db: Session, answer_id: int):
    return db.query(Answer).filter(Answer.id == answer_id).first()
  
