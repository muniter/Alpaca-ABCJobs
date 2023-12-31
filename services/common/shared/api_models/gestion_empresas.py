from datetime import datetime, date
from pydantic import BaseModel, StringConstraints, Field
from typing import Annotated, List, Optional

from common.shared.api_models.gestion_candidatos import (
    CandidatoPersonalInformationDTO,
    CountryDTO,
    RolHabilidadDTO,
)


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
    password: Annotated[str, StringConstraints(max_length=20, min_length=8)]


class EmpresaDTO(BaseModel):
    id: int
    nombre: str
    email: str


class EmpresaCreateResponseDTO(BaseModel):
    empresa: EmpresaDTO
    token: str


class EmpleadoPersonalityDTO(BaseModel):
    id: int
    name: str


class EmpleadoEvaluacionDesempenoCreateDTO(BaseModel):
    date: date
    result: Annotated[int, Field(ge=0, le=100)]


class EmpleadoEvaluacionDesempenoDTO(BaseModel):
    id: int
    date: date
    result: Annotated[int, Field(ge=0, le=100)]


class EmpleadoEquipoDTO(BaseModel):
    id: int
    name: str


class EmpleadoDTO(BaseModel):
    id: int
    id_persona: int
    name: str
    title: str
    company: EmpresaDTO
    personality: EmpleadoPersonalityDTO
    skills: List[RolHabilidadDTO]
    evaluations: List[EmpleadoEvaluacionDesempenoDTO]
    teams: List[EmpleadoEquipoDTO]


class EmpleadoCreateDTO(BaseModel):
    name: Annotated[
        str, StringConstraints(max_length=255, min_length=2, strip_whitespace=True)
    ]
    title: Annotated[
        str, StringConstraints(max_length=255, min_length=2, strip_whitespace=True)
    ]
    skills: List[int]
    personality_id: int


class EquipoDTO(BaseModel):
    id: int
    name: str
    company: EmpresaDTO
    employees: List[EmpleadoDTO]


class EquipoCreateDTO(BaseModel):
    name: Annotated[
        str, StringConstraints(max_length=255, min_length=2, strip_whitespace=True)
    ]
    employees: List[int]


class CandidatoPreseleccionadoVacanteDTO(CandidatoPersonalInformationDTO):
    result: Optional[int] = None


class VacanteDTO(BaseModel):
    id: int
    name: str
    open: bool
    description: Optional[str]
    country: Optional[CountryDTO] = None
    company: EmpresaDTO
    team: EquipoDTO
    preselection: List[CandidatoPreseleccionadoVacanteDTO]
    interview_date: Optional[datetime] = None


class VacanteCreateDTO(BaseModel):
    team_id: int
    name: Annotated[
        str, StringConstraints(max_length=255, min_length=2, strip_whitespace=True)
    ]
    description: Annotated[
        Optional[str],
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]
    country_code: Optional[int] = None


class VacantePreseleccionDTO(BaseModel):
    id_candidate: int


class VacanteResultadoPruebaTecnicaDTO(BaseModel):
    id_candidate: int
    result: Annotated[int, Field(ge=0, le=100)]


class VacanteSetFechaEntrevistaDTO(BaseModel):
    interview_date: datetime


class VacanteSelecconarCandidatoDTO(BaseModel):
    id_candidate: int
