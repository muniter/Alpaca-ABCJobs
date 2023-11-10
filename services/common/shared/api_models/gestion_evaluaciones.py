from pydantic import BaseModel, StringConstraints
from typing import Annotated, Optional, List
from datetime import date

from common.shared.api_models.gestion_candidatos import RolHabilidadDTO


class ExamenDTO(BaseModel):
    id: int
    skill: RolHabilidadDTO
    completed: Optional[bool]
    number_of_questions: int


class ExamenRespuestaDTO(BaseModel):
    id: int
    id_question: int
    answer: str


class ExamenPreguntaDTO(BaseModel):
    id: int
    id_exam: int
    question: str
    difficulty: int
    answers: List[ExamenRespuestaDTO]


class ExamenResultadoDTO(BaseModel):
    id: int
    exam: ExamenDTO
    id_candidato: int
    result: int
    completed: bool


class ExamenStepResponseDTO(BaseModel):
    id_result: int
    id_exam: int
    next_question: Optional[ExamenPreguntaDTO]
    result: Optional[ExamenResultadoDTO]
