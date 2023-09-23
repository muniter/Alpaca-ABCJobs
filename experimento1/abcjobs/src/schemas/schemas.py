from pydantic import BaseModel

class Status(BaseModel):
  ok: bool
  status: str
  message: str

  class Config:
    json_schema_extra = {
      "example": {
        "ok": True,
        "status": "pong",
        "message": "Server is alive",
      }
    }

class Response(Status):
  latency_ms: int
  cpu_perc: float
  cpu_avg: float
  mem_perc: float
  
  class Config:
    json_schema_extra = {
      "example": {
        "ok": True,
        "status": "right",
        "message": "Right answer",
        "latency_ms": "300",
        "cpu_perc": "20.5",
        "cpu_avg": "cpu_avg",
        "mem_perc": "mem_perc"
      }
    }
    
class Question(BaseModel):
  id: int
  question: str
  
  class Config:
    orm_mode = True
  
class Answer(BaseModel):
  id: int
  answer: str
  is_right: bool
  
  class Config:
    orm_mode = True