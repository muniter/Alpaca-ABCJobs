from typing import List, Union
from sqlalchemy import select, func
from sqlalchemy.orm import Session

from fastapi import Depends
from common.shared.api_models.gestion_candidatos import RolHabilidadDTO
from common.shared.api_models.gestion_empresas import (
    EmpleadoCreateDTO,
    EmpleadoDTO,
    EmpleadoPersonalityDTO,
    EmpresaCreateResponseDTO,
    EmpresaCreateDTO,
)
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.clients.usuario import UsuarioClient
from common.shared.database.models import (
    Empleado,
    Empresa,
    Persona,
    Personalidad,
    RolesHabilidades,
)
from common.shared.database.db import get_db_session_dependency
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse


class EmpresaRepository:
    session: Session

    def __init__(self, session: Session):
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
        session: Session,
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

    def get_rol_habilidad_by_id(self, id: int) -> Union[RolesHabilidades, None]:
        query = select(RolesHabilidades).where(RolesHabilidades.id == id)
        return self.session.execute(query).scalar_one_or_none()


class EmpleadoRepository:
    # Get by id, create, update, delete

    def __init__(
        self,
        session: Session,
    ):
        self.session = session

    def get_by_id(self, id: int) -> Union[Empleado, None]:
        query = select(Empleado).where(Empleado.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_all(self, id_empresa: int) -> List[Empleado]:
        query = select(Empleado).where(Empleado.id_empresa == id_empresa)
        return list(self.session.execute(query).scalars().all())

    def crear(self, data: Empleado) -> Empleado:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def update(self, data: Empleado) -> Empleado:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def delete(self, data: Empleado):
        self.session.delete(data)
        self.session.commit()


class EmpresaService:
    repository: EmpresaRepository
    usuario_client: UsuarioClient
    utils_repository: UtilsRepository
    empleado_repository: EmpleadoRepository

    def __init__(
        self,
        repository: EmpresaRepository,
        utils_repository: UtilsRepository,
        empleado_repository: EmpleadoRepository,
        usuario_client: UsuarioClient = UsuarioClient(),
    ):
        self.repository = repository
        self.usuario_client = usuario_client
        self.utils_repository = utils_repository
        assert isinstance(utils_repository.session, Session)
        self.empleado_repository = empleado_repository

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

    def crear_empleado(
        self, id_empresa: int, data: EmpleadoCreateDTO
    ) -> Union[EmpleadoDTO, ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder(data)
            error.add("global", "Empresa no encontrada")

        empleado = Empleado(id_empresa=id_empresa)
        result = self.load_empleado(empleado, data)
        if isinstance(result, ErrorBuilder):
            return result

        empleado = result
        empleado = self.empleado_repository.crear(empleado)
        return empleado.build_dto()

    def load_empleado(
        self, empleado: Empleado, data: EmpleadoCreateDTO
    ) -> Union[ErrorBuilder, Empleado]:
        persona = empleado.persona if empleado.persona else Persona()
        empleado.persona = persona

        [nombre, apellido] = data.name.split(" ", 1)
        persona.nombres = nombre
        persona.apellidos = apellido if apellido else ""
        empleado.cargo = data.title

        personalidad = self.utils_repository.get_personalidad_by_id(data.personality_id)
        if not personalidad:
            error = ErrorBuilder(data)
            error.add("personality_id", "Personalidad no encontrada")
            return error

        empleado.personalidad = personalidad

        for rol_habilidad_id in data.skills:
            rol_habilidad = self.utils_repository.get_rol_habilidad_by_id(
                rol_habilidad_id
            )
            if not rol_habilidad:
                error = ErrorBuilder(data)
                error.add("skills", "Habilidad no encontrada")
                return error

            empleado.roles_habilidades.append(rol_habilidad)

        return empleado

    def get_all_empleados(
        self, id_empresa: int
    ) -> Union[List[EmpleadoDTO], ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder()
            error.add("global", "Empresa no encontrada")
            return error

        empleados = self.empleado_repository.get_all(id_empresa=id_empresa)
        return [e.build_dto() for e in empleados]

    def get_empleado_by_id(
        self, id_empresa: int, id_empleado: int
    ) -> Union[EmpleadoDTO, ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder()
            error.add("global", "Empresa no encontrada")
            return error

        empleado = self.empleado_repository.get_by_id(id_empleado)
        if not empleado:
            error = ErrorBuilder()
            error.add("global", "Empleado no encontrado")
            return error

        return empleado.build_dto()


def get_empresa_repository(
    session: Session = Depends(get_db_session_dependency),
) -> EmpresaRepository:
    return EmpresaRepository(session=session)


def get_utils_repository(
    session: Session = Depends(get_db_session_dependency),
) -> UtilsRepository:
    return UtilsRepository(session=session)


def get_empleado_repository(
    session: Session = Depends(get_db_session_dependency),
) -> EmpleadoRepository:
    return EmpleadoRepository(session=session)


def get_empresa_service(
    repository: EmpresaRepository = Depends(get_empresa_repository),
    utils_service: UtilsRepository = Depends(get_utils_repository),
    empleado_repository: EmpleadoRepository = Depends(get_empleado_repository),
) -> EmpresaService:
    return EmpresaService(
        repository=repository,
        utils_repository=utils_service,
        empleado_repository=empleado_repository,
    )
