from pydantic import BaseModel, StringConstraints
from typing import Annotated


class EmpresaCreateDTO(BaseModel):
    nombre: Annotated[
        str, StringConstraints(max_length=255, min_length=2, strip_whitespace=True)
    ]
    email: Annotated[
        str,
        StringConstraints(
            max_length=255, min_length=5, strip_whitespace=True, pattern=r".*@.*"
        ),
    ]
    password: Annotated[
        str, StringConstraints(max_length=20, min_length=8)
    ]


class EmpresaDTO(BaseModel):
    id: int
    nombre: str
    email: str


class EmpresaCreateResponseDTO(BaseModel):
    empresa: EmpresaDTO
    token: str
