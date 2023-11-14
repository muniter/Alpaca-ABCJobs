from pydantic import BaseModel, StringConstraints
from typing import Annotated

from common.shared.api_models.gestion_empresas import EquipoDTO


class ProyectoCreateDTO(BaseModel):
    name: Annotated[
        str, StringConstraints(max_length=255, min_length=1, strip_whitespace=True)
    ]

    description: Annotated[
        str, StringConstraints(max_length=255, min_length=0, strip_whitespace=True)
    ]
    id_team: int


class ProyectoDTO(BaseModel):
    id: int
    name: str
    description: str
    team: EquipoDTO
