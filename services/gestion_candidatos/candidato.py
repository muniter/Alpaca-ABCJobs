from typing import Union
from sqlalchemy import select, func
from sqlalchemy.orm import Session

from fastapi import Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateResponseDTO,
    CandidatoCreateDTO,
    CandidatoDTO,
)
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.clients.usuario import UsuarioClient
from common.shared.database.models import Candidato
from common.shared.database.db import get_db_session
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse


class CandidatoRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: int) -> Union[Candidato, None]:
        query = select(Candidato).where(Candidato.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_by_email(self, email: str) -> Union[Candidato, None]:
        query = select(Candidato).where(
            func.lower(Candidato.email) == func.lower(email)
        )
        return self.session.execute(query).scalar_one_or_none()

    def crear(self, data: Candidato) -> Candidato:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class CandidatoService:
    repository: CandidatoRepository
    usuario_client: UsuarioClient

    def __init__(
        self,
        repository: CandidatoRepository,
        usuario_client: UsuarioClient = UsuarioClient(),
    ):
        self.repository = repository
        self.usuario_client = usuario_client

    def __crear_usuario(
        self, candidato: Candidato, password: str
    ) -> UsuarioLoginResponseDTO:
        usuario = self.usuario_client.crear_login(
            UsuarioRegisterDTO(
                email=candidato.email,
                password=password,
                id_candidato=candidato.id,
            )
        )
        if isinstance(usuario, ErrorResponse):
            raise Exception("Error al crear usuario")

        return UsuarioLoginResponseDTO.model_validate(usuario.data)

    def crear(
        self, data: CandidatoCreateDTO
    ) -> Union[CandidatoCreateResponseDTO, ErrorBuilder]:
        error = ErrorBuilder(data)
        if self.repository.get_by_email(data.email):
            error.add("email", "Email ya registrado")

        if error.has_error:
            return error

        candidato = Candidato(
            nombres=data.nombres,
            apellidos=data.apellidos,
            email=data.email.lower(),
        )
        candidato = self.repository.crear(candidato)
        usuario = self.__crear_usuario(candidato, data.password)
        return CandidatoCreateResponseDTO(
            candidato=CandidatoDTO(
                id=candidato.id,
                nombres=candidato.nombres,
                apellidos=candidato.apellidos,
                email=candidato.email,
            ),
            token=usuario.token,
        )


def get_candidato_repository(
    session: Session = Depends(get_db_session),
) -> CandidatoRepository:
    return CandidatoRepository(session)


def get_candidato_service(
    repository: CandidatoRepository = Depends(get_candidato_repository),
) -> CandidatoService:
    return CandidatoService(repository)
