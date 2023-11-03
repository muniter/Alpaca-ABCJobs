from typing import List, Union
from sqlalchemy import select, func
from sqlalchemy.orm import Session

from fastapi import Depends
from common.shared.api_models.gestion_candidatos import RolHabilidadDTO
from common.shared.api_models.gestion_empresas import (
    EmpleadoPersonalityDTO,
    EmpresaCreateResponseDTO,
    EmpresaCreateDTO,
)
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.clients.usuario import UsuarioClient
from common.shared.database.models import Empresa, Personalidad, RolesHabilidades
from common.shared.database.db import get_db_session_dependency
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse


class EmpresaRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: int) -> Union[Empresa, None]:
        query = select(Empresa).where(Empresa.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_by_email(self, email: str) -> Union[Empresa, None]:
        query = select(Empresa).where(func.lower(Empresa.email) == func.lower(email))
        return self.session.execute(query).scalar_one_or_none()

    def get_by_nombre(self, nombre: str) -> Union[Empresa, None]:
        query = select(Empresa).where(func.lower(Empresa.nombre) == func.lower(nombre))
        return self.session.execute(query).scalar_one_or_none()

    def crear(self, data: Empresa) -> Empresa:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class UtilsRepository:
    session: Session

    def __init__(
        self,
        session: Session = Depends(get_db_session_dependency),
    ):
        self.session = session

    def get_personalidades(self) -> list[Personalidad]:
        query = select(Personalidad)
        return list(self.session.execute(query).scalars().all())

    def get_personalidades_dto(self) -> List[EmpleadoPersonalityDTO]:
        return [p.build_dto() for p in self.get_personalidades()]

    def get_personalidad_by_id(self, id: int) -> Union[Personalidad, None]:
        query = select(Personalidad).where(Personalidad.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_roles_habilidades(self) -> list[RolesHabilidades]:
        query = select(RolesHabilidades)
        return list(self.session.execute(query).scalars().all())

    def get_roles_habilidades_dto(self) -> List[RolHabilidadDTO]:
        return [p.build_dto() for p in self.get_roles_habilidades()]


class EmpresaService:
    repository: EmpresaRepository
    usuario_client: UsuarioClient

    def __init__(
        self,
        repository: EmpresaRepository,
        usuario_client: UsuarioClient = UsuarioClient(),
    ):
        self.repository = repository
        self.usuario_client = usuario_client

    def __crear_usuario(
        self, empresa: Empresa, password: str
    ) -> UsuarioLoginResponseDTO:
        usuario = self.usuario_client.crear_login(
            UsuarioRegisterDTO(
                email=empresa.email,
                password=password,
                id_empresa=empresa.id,
            )
        )
        if isinstance(usuario, ErrorResponse):
            raise Exception("Error al crear usuario")

        return UsuarioLoginResponseDTO.model_validate(usuario.data)

    def crear(
        self, data: EmpresaCreateDTO
    ) -> Union[EmpresaCreateResponseDTO, ErrorBuilder]:
        error = ErrorBuilder(data)
        if self.repository.get_by_email(data.email):
            error.add("email", "Email ya registrado")

        if self.repository.get_by_nombre(data.nombre):
            error.add("nombre", "Nombre ya registrado")

        if error.has_error:
            return error

        empresa = Empresa(
            nombre=data.nombre,
            email=data.email.lower(),
        )
        empresa = self.repository.crear(empresa)
        usuario = self.__crear_usuario(empresa, data.password)
        return EmpresaCreateResponseDTO(
            empresa=empresa.build_dto(),
            token=usuario.token,
        )


def get_empresa_repository(
    session: Session = Depends(get_db_session_dependency),
) -> EmpresaRepository:
    return EmpresaRepository(session)


def get_empresa_service(
    repository: EmpresaRepository = Depends(get_empresa_repository),
) -> EmpresaService:
    return EmpresaService(repository)
