from typing import Union
from fastapi import Depends
from sqlalchemy.orm import Session
from sqlalchemy import select, func
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginDTO,
    UsuarioLoginResponseDTO,
    UsuarioCandidatoDTO,
    UsuarioDTO,
    UsuarioEmpresaDTO,
    UsuarioRegisterDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Usuario

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

    def crear(self, data: Usuario) -> Usuario:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class UsuarioService:
    def __init__(self, repository: UsuarioRepository):
        self.repository = repository

    def login(self, data: UsuarioLoginDTO) -> Union[Usuario, ErrorBuilder]:
        usuario = self.repository.get_by_email(data.email)
        error = ErrorBuilder(data)
        if not usuario:
            error.add("email", "Email no registrado")
            return error

        if usuario.password != data.password:
            error.add("password", "Password incorrecto")
            return error

        # TODO: Generar token
        return usuario

    def crear(self, data: UsuarioRegisterDTO) -> Union[Usuario, ErrorBuilder]:
        error = ErrorBuilder(data)
        if self.repository.get_by_email(data.email):
            error.add("email", "Email ya registrado")

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

        usuario_dto: UsuarioDTO

        if usuario.id_empresa:
            usuario_dto = UsuarioEmpresaDTO(
                id=usuario.id,
                email=usuario.email,
                id_empresa=usuario.id_empresa,
            )
        elif usuario.id_candidato:
            usuario_dto = UsuarioCandidatoDTO(
                id=usuario.id,
                email=usuario.email,
                id_candidato=usuario.id_candidato,
            )
        else:
            raise Exception("Usuario no tiene empresa ni candidato")

        return UsuarioLoginResponseDTO(
            usuario=usuario_dto,
            token="hola",
        )


def get_usuario_repository(
    session: Session = Depends(get_db_session),
) -> UsuarioRepository:
    return UsuarioRepository(session)


def get_usuario_service(
    repository: UsuarioRepository = Depends(get_usuario_repository),
) -> UsuarioService:
    return UsuarioService(repository)
