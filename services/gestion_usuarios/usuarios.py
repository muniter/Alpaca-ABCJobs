from typing import Union
from fastapi import Depends
from sqlalchemy.orm import Session
from sqlalchemy import select, func
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginDTO,
    UsuarioLoginResponseDTO,
    UsuarioDTO,
    UsuarioRegisterDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Candidato, Empresa, Usuario
from common.shared.jwt import create_token, get_usuario_from_token


class UsuarioRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: int) -> Union[Usuario, None]:
        query = select(Usuario).where(Usuario.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_by_email(self, email: str) -> Union[Usuario, None]:
        query = select(Usuario).where(func.lower(Usuario.email) == func.lower(email))
        return self.session.execute(query).scalar_one_or_none()

    def check_empresa_exists(self, id_empresa: int) -> bool:
        query = select(Empresa).where(Empresa.id == id_empresa)
        return self.session.execute(query).scalar_one_or_none() is not None

    def check_candidate_exists(self, id_candidato: int) -> bool:
        query = select(Candidato).where(Candidato.id == id_candidato)
        return self.session.execute(query).scalar_one_or_none() is not None

    def crear(self, data: Usuario) -> Usuario:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def get_config(self, id: int) -> dict:
        usuario = self.get_by_id(id)
        assert usuario is not None
        return usuario.config

    def set_config(self, id: int, config: dict) -> None:
        usuario = self.get_by_id(id)
        assert usuario is not None
        usuario.config = config
        self.session.commit()


class UsuarioService:
    def __init__(self, repository: UsuarioRepository):
        self.repository = repository

    def create_token(self, data: UsuarioDTO) -> str:
        return create_token(data.model_dump())

    def get_usuario_from_token(self, token: str) -> UsuarioDTO:
        return get_usuario_from_token(token)

    def login(
        self, data: UsuarioLoginDTO
    ) -> Union[UsuarioLoginResponseDTO, ErrorBuilder]:
        usuario = self.repository.get_by_email(data.email)
        error = ErrorBuilder(data)
        if not usuario:
            error.add("email", "Email no registrado")
            return error

        if usuario.password != data.password:
            error.add("password", "Password incorrecto")
            return error

        usuario_dto = usuario.build_usuario_dto()
        return UsuarioLoginResponseDTO(
            usuario=usuario_dto,
            token=self.create_token(usuario_dto),
        )

    def crear(self, data: UsuarioRegisterDTO) -> Union[Usuario, ErrorBuilder]:
        error = ErrorBuilder(data)
        if self.repository.get_by_email(data.email):
            error.add("email", "Email ya registrado")

        if data.id_empresa:
            if not self.repository.check_empresa_exists(data.id_empresa):
                error.add("id_empresa", "Empresa no existe")

        if data.id_candidato:
            if not self.repository.check_candidate_exists(data.id_candidato):
                error.add("id_candidato", "Candidato no existe")

        if error.has_error:
            return error

        usuario = Usuario(
            email=data.email.lower(),
            password=data.password,
        )
        if data.id_empresa:
            usuario.id_empresa = data.id_empresa
        elif data.id_candidato:
            usuario.id_candidato = data.id_candidato
        else:
            raise Exception("Usuario no tiene empresa ni candidato")

        result = self.repository.crear(usuario)
        return result

    def crear_login(
        self, data: UsuarioRegisterDTO
    ) -> Union[UsuarioLoginResponseDTO, ErrorBuilder]:
        result = self.crear(data)
        if isinstance(result, ErrorBuilder):
            return result

        usuario = result

        usuario_dto = usuario.build_usuario_dto()

        return UsuarioLoginResponseDTO(
            usuario=usuario_dto,
            token=self.create_token(usuario_dto),
        )


def get_usuario_repository(
    session: Session = Depends(get_db_session),
) -> UsuarioRepository:
    return UsuarioRepository(session)


def get_usuario_service(
    repository: UsuarioRepository = Depends(get_usuario_repository),
) -> UsuarioService:
    return UsuarioService(repository)
