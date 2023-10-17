from pydantic import BaseModel, StringConstraints
from typing import Annotated


class CandidatoCreateDTO(BaseModel):
    nombres: Annotated[
        str, StringConstraints(max_length=100, min_length=2, strip_whitespace=True)
    ]
    apellidos: Annotated[
        str, StringConstraints(max_length=100, min_length=2, strip_whitespace=True)
    ]
    email: Annotated[
        str,
        StringConstraints(
            max_length=255, min_length=5, strip_whitespace=True, pattern=r".*@.*"
        ),
    ]
    password: Annotated[str, StringConstraints(max_length=20, min_length=8)]


class CandidatoDTO(BaseModel):
    id: int
    nombres: str
    apellidos: str
    email: str


class CandidatoCreateResponseDTO(BaseModel):
    candidato: CandidatoDTO
    token: str
