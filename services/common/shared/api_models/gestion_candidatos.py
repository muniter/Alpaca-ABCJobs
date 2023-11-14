from pydantic import BaseModel, StringConstraints
from typing import Annotated, Optional, List
from datetime import date


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


class LenguajeDTO(BaseModel):
    id: str
    name: str


class CountryDTO(BaseModel):
    num_code: int
    alpha_2_code: str
    alpha_3_code: str
    en_short_name: str
    nationality: str


class CandidatoPersonalInformationDTO(BaseModel):
    id_candidate: int
    id_persona: int
    names: str
    last_names: str
    full_name: str
    email: str
    birth_date: Optional[date]
    country_code: Optional[int]
    country: Optional[str]
    city: Optional[str]
    address: Optional[str]
    phone: Optional[str]
    biography: Optional[str]
    languages: Optional[List[LenguajeDTO]]


class CandidatoPersonalInformationUpdateDTO(BaseModel):
    birth_date: Optional[date]
    country_code: Optional[int]
    city: Annotated[
        Optional[str],
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]
    address: Annotated[
        Optional[str],
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]

    phone: Annotated[
        Optional[str],
        # Only numbers from 2 to 15 digits
        StringConstraints(
            max_length=15, min_length=2, strip_whitespace=True, pattern=r"^\d{2,15}$"
        ),
    ]

    biography: Annotated[
        Optional[str],
        StringConstraints(max_length=255, min_length=10, strip_whitespace=True),
    ]

    languages: Optional[List[str]]


class RolHabilidadDTO(BaseModel):
    id: int
    name: str


class CandidatoDatosLaboralesDTOBase(BaseModel):
    role: Annotated[
        str,
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]

    company: Annotated[
        str,
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]

    description: Annotated[
        str,
        StringConstraints(max_length=500, min_length=2, strip_whitespace=True),
    ]

    skills: Optional[List[RolHabilidadDTO]]

    start_year: int
    end_year: Optional[int]


class CandidatoDatosLaboralesDTO(CandidatoDatosLaboralesDTOBase):
    id: int
    id_persona: int


class CandidatoDatosLaboralesCreateDTO(CandidatoDatosLaboralesDTOBase):
    skills: Optional[List[int]]


class CandidatoDatosLaboralesTipoDTO(BaseModel):
    id: int
    name: str


class CandidatoDatosLaboralesUpdateDTO(CandidatoDatosLaboralesDTOBase):
    roles: Optional[List[int]]


class CandidatoDatosAcademicosDTOBase(BaseModel):
    institution: Annotated[
        str,
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]

    title: Annotated[
        str,
        StringConstraints(max_length=255, min_length=2, strip_whitespace=True),
    ]
    start_year: int
    end_year: Optional[int]
    achievement: Optional[str]


class CandidatoDatosAcademicosDTO(CandidatoDatosAcademicosDTOBase):
    id: int
    id_persona: int
    type: CandidatoDatosLaboralesTipoDTO


class CandidatoDatosAcademicosCreateDTO(CandidatoDatosAcademicosDTOBase):
    type: int


class CandidatoDatosAcademicosUpdateDTO(CandidatoDatosAcademicosDTOBase):
    type: Optional[int]


class CandidatoConocimientoTecnicoTipoDTO(BaseModel):
    id: int
    name: str


class CandidatoConocimientoTecnicoDTOBase(BaseModel):
    description: Annotated[
        Optional[str],
        StringConstraints(max_length=500, min_length=2, strip_whitespace=True),
    ]


class CandidatoConocimientoTecnicoDTO(CandidatoConocimientoTecnicoDTOBase):
    id: int
    id_persona: int
    type: CandidatoConocimientoTecnicoTipoDTO


class CandidatoConocimientoTecnicoCreateDTO(CandidatoConocimientoTecnicoDTOBase):
    type: int


class CandidatoConocimientoTecnicoBatchSetDTO(BaseModel):
    list: List[CandidatoConocimientoTecnicoCreateDTO]


class CandidatoSearchDTO(BaseModel):
    # País
    country_code: Optional[int] = None

    # Tecnologías
    technical_info_types: Optional[List[int]] = None

    # Idiomas
    languages: Optional[List[str]] = None

    # Nivel de estudios mínimo
    least_academic_level: Optional[int] = None

    # Areas de Studio
    study_areas: Optional[List[str]] = None

    # Roles
    skills: Optional[List[int]] = None


class CandidatoSearchResultDTO(CandidatoDTO):
    country: Optional["CountryDTO"]
    city: Optional[str]
    # Skills and roles
    skills: List["RolHabilidadDTO"]
    skills_related: List["RolHabilidadDTO"]
    # Tecnologías
    technical_info_types: List["CandidatoConocimientoTecnicoTipoDTO"]
    technical_info_types_related: List["CandidatoConocimientoTecnicoTipoDTO"]
    languages: List["LenguajeDTO"]
    languages_related: List["LenguajeDTO"]
