from pydantic import BaseModel, StringConstraints, model_validator
from typing import Annotated, Optional, Union


class UsuarioCandidatoDTO(BaseModel):
    id: int
    email: str
    id_candidato: int


class UsuarioEmpresaDTO(BaseModel):
    id: int
    email: str
    id_empresa: int


UsuarioDTO = Union[UsuarioCandidatoDTO, UsuarioEmpresaDTO]


def build_usuario_dto(usuario_data: dict) -> UsuarioDTO:
    if usuario_data.get("id_candidato", None):
        return UsuarioCandidatoDTO.model_validate(usuario_data)
    elif usuario_data.get("id_empresa", None):
        return UsuarioEmpresaDTO.model_validate(usuario_data)
    else:
        raise ValueError("Usuario data must have id_candidato or id_empresa")


class UsuarioLoginDTO(BaseModel):
    email: Annotated[
        str,
        StringConstraints(
            max_length=255, min_length=5, strip_whitespace=True, pattern=r".*@.*"
        ),
    ]
    password: Annotated[str, StringConstraints(max_length=20, min_length=8)]


class UsuarioRegisterDTO(BaseModel):
    email: Annotated[
        str,
        StringConstraints(
            max_length=255, min_length=5, strip_whitespace=True, pattern=r".*@.*"
        ),
    ]
    password: Annotated[str, StringConstraints(max_length=20, min_length=8)]
    id_empresa: Optional[int] = None
    id_candidato: Optional[int] = None

    # One of the two
    @model_validator(mode="after")
    def one_of_two(cls, v):
        if not (v.id_empresa or v.id_candidato):
            raise ValueError("One of id_empresa or id_candidato must be set")
        return v


class UsuarioLoginResponseDTO(BaseModel):
    usuario: UsuarioDTO
    token: str


class UsuarioSetConfigDTO(BaseModel):
    config: dict


class UsuarioConfigDTO(BaseModel):
    config: dict
