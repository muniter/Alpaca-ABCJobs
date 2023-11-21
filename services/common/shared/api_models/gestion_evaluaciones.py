from pydantic import BaseModel
from typing import Optional, List

from common.shared.api_models.gestion_candidatos import CandidatoConocimientoTecnicoTipoDTO


class ExamenDTO(BaseModel):
    id: int
    skill: CandidatoConocimientoTecnicoTipoDTO
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
