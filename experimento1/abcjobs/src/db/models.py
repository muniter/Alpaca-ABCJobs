from db.base_class import Base
from sqlalchemy import Boolean
from sqlalchemy import Column
from sqlalchemy import ForeignKey
from sqlalchemy import Integer
from sqlalchemy import String
from sqlalchemy.orm import relationship

class Question(Base):
  id = Column(Integer, primary_key=True)
  question = Column(String, nullable=False)
  answers = relationship("Answer", back_populates="question")
    
class Answer(Base):
  id = Column(Integer, primary_key=True)
  answer = Column(String, nullable=False)
  question_id = Column(Integer, ForeignKey("question.id"))
  question = relationship("Question", back_populates="answers")
  is_right = Column(Boolean(), default=False)