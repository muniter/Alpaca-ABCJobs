from typing import List, Union
from sqlalchemy import select, func
from sqlalchemy.orm import Session
import datetime

from fastapi import Depends
from common.shared.api_models.gestion_candidatos import RolHabilidadDTO
from common.shared.api_models.gestion_empresas import (
    EmpleadoCreateDTO,
    EmpleadoDTO,
    EmpleadoEvaluacionDesempenoCreateDTO,
    EmpleadoPersonalityDTO,
    EmpresaCreateResponseDTO,
    EmpresaCreateDTO,
    EquipoCreateDTO,
    EquipoDTO,
    VacanteCreateDTO,
    VacanteDTO,
    VacantePreseleccionDTO,
    VacanteResultadoPruebaTecnicaDTO,
    VacanteSelecconarCandidatoDTO,
    VacanteSetFechaEntrevistaDTO,
)
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.clients.usuario import UsuarioClient
from common.shared.database.models import (
    Empleado,
    Empresa,
    Equipo,
    EvaluacionDesempeno,
    Persona,
    Candidato,
    Personalidad,
    RolesHabilidades,
    Vacante,
    VacanteCandidato,
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

    def get_rol_habilidad_by_id(self, id: int) -> Union[RolesHabilidades, None]:
        query = select(RolesHabilidades).where(RolesHabilidades.id == id)
        return self.session.execute(query).scalar_one_or_none()


class EmpleadoRepository:
    def __init__(
        self,
        session: Session,
    ):
        self.session = session

    def get_by_id(self, id: int, id_empresa: int) -> Union[Empleado, None]:
        query = (
            select(Empleado)
            .where(Empleado.id == id)
            .where(Empleado.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_all(
        self, id_empresa: int, contratado_abc: bool | None = None
    ) -> List[Empleado]:
        query = select(Empleado).where(Empleado.id_empresa == id_empresa)
        if contratado_abc is not None:
            query = query.where(Empleado.contratado_abc == contratado_abc)
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


class EquipoRepository:
    session: Session

    def __init__(
        self,
        session: Session,
    ):
        self.session = session

    def get_by_id(self, id: int, id_empresa: int) -> Union[Equipo, None]:
        query = (
            select(Equipo).where(Equipo.id == id).where(Equipo.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_by_nombre(self, nombre: str, id_empresa: int) -> Union[Equipo, None]:
        query = (
            select(Equipo)
            .where(Equipo.nombre == nombre)
            .where(Equipo.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_all(self, id_empresa: int) -> List[Equipo]:
        query = select(Equipo).where(Equipo.id_empresa == id_empresa)
        return list(self.session.execute(query).scalars().all())

    def crear(self, data: Equipo) -> Equipo:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def update(self, data: Equipo) -> Equipo:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class VacanteRepository:
    session: Session

    def __init__(
        self,
        session: Session,
    ):
        self.session = session

    def get_by_id(self, id: int, id_empresa: int) -> Union[Vacante, None]:
        query = (
            select(Vacante)
            .where(Vacante.id == id)
            .where(Vacante.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_all(self, id_empresa: int) -> List[Vacante]:
        query = select(Vacante).where(Vacante.id_empresa == id_empresa)
        return list(self.session.execute(query).scalars().all())

    def crear(self, data: Vacante) -> Vacante:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def get_candidato_by_id(self, id: int) -> Union[Candidato, None]:
        query = select(Candidato).where(Candidato.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def update(self, data: Vacante) -> Vacante:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class EmpresaService:
    repository: EmpresaRepository
    usuario_client: UsuarioClient
    utils_repository: UtilsRepository
    empleado_repository: EmpleadoRepository
    equipo_repository: EquipoRepository

    def __init__(
        self,
        repository: EmpresaRepository,
        utils_repository: UtilsRepository,
        empleado_repository: EmpleadoRepository,
        equipo_repository: EquipoRepository,
        vacante_repository: VacanteRepository,
        usuario_client: UsuarioClient = UsuarioClient(),
    ):
        self.repository = repository
        self.usuario_client = usuario_client
        self.utils_repository = utils_repository
        self.empleado_repository = empleado_repository
        self.equipo_repository = equipo_repository
        self.vacante_repository = vacante_repository

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

        result = data.name.split(" ", 1)
        persona.nombres = result[0] if len(result) > 0 else " "
        persona.apellidos = result[1] if len(result) > 1 else " "
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
        self, id_empresa: int, contratado_abc: bool | None = None
    ) -> Union[List[EmpleadoDTO], ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder()
            error.add("global", "Empresa no encontrada")
            return error

        empleados = self.empleado_repository.get_all(
            id_empresa=id_empresa, contratado_abc=contratado_abc
        )
        return [e.build_dto() for e in empleados]

    def get_empleado_by_id(
        self, id_empresa: int, id_empleado: int
    ) -> Union[EmpleadoDTO, ErrorBuilder]:
        empleado = self.empleado_repository.get_by_id(
            id=id_empleado, id_empresa=id_empresa
        )
        if not empleado:
            error = ErrorBuilder()
            error.add("global", "Empleado no encontrado")
            return error

        return empleado.build_dto()

    def get_equipo_by_id(
        self, id_empresa: int, id_equipo: int
    ) -> Union[EquipoDTO, ErrorBuilder]:
        equipo = self.equipo_repository.get_by_id(id=id_equipo, id_empresa=id_empresa)
        if not equipo:
            error = ErrorBuilder()
            error.add("global", "Equipo no encontrado")
            return error

        return equipo.build_dto()

    def get_all_equipos(self, id_empresa: int) -> Union[List[EquipoDTO], ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder()
            error.add("global", "Empresa no encontrada")
            return error

        equipos = self.equipo_repository.get_all(id_empresa=id_empresa)
        return [e.build_dto() for e in equipos]

    def crear_equipo(
        self, id_empresa: int, data: EquipoCreateDTO
    ) -> Union[EquipoDTO, ErrorBuilder]:
        empresa = self.repository.get_by_id(id_empresa)
        if not empresa:
            error = ErrorBuilder()
            error.add("global", "Company not found")
            return error

        if self.equipo_repository.get_by_nombre(
            nombre=data.name, id_empresa=id_empresa
        ):
            error = ErrorBuilder()
            error.add("name", "Name of team already exists")
            return error

        equipo = Equipo(id_empresa=id_empresa)
        equipo = self.load_equipo(equipo, data)
        if isinstance(equipo, ErrorBuilder):
            return equipo

        equipo = self.equipo_repository.crear(equipo)
        return equipo.build_dto()

    def load_equipo(
        self, equipo: Equipo, data: EquipoCreateDTO
    ) -> Union[Equipo, ErrorBuilder]:
        error = ErrorBuilder(data)
        equipo.nombre = data.name
        seen = set()
        for empleado_id in data.employees:
            if empleado_id in seen:
                error.add("employees", "Empleado duplicado")
                return error
            seen.add(empleado_id)

            empleado = self.empleado_repository.get_by_id(
                id=empleado_id, id_empresa=equipo.id_empresa
            )
            if not empleado:
                error.add("employees", "Empleado no encontrado")
                return error

            equipo.empleados.append(empleado)

        return equipo

    def vacante_get_by_id(
        self, id_empresa: int, id_vacante: int
    ) -> Union[VacanteDTO, ErrorBuilder]:
        vacante = self.vacante_repository.get_by_id(
            id=id_vacante, id_empresa=id_empresa
        )
        if not vacante:
            error = ErrorBuilder()
            error.add("global", "Vacancy not found")
            return error

        return vacante.build_dto()

    def vacante_get_all(self, id_empresa: int) -> List[VacanteDTO]:
        vacantes = self.vacante_repository.get_all(id_empresa=id_empresa)
        return [v.build_dto() for v in vacantes]

    def vacante_crear(
        self, id_empresa: int, data: VacanteCreateDTO
    ) -> Union[VacanteDTO, ErrorBuilder]:
        vacante = Vacante()
        vacante.name = data.name
        vacante.descripcion = data.description
        vacante.id_empresa = id_empresa
        equipo = self.equipo_repository.get_by_id(
            id=data.team_id, id_empresa=id_empresa
        )
        if not equipo:
            error = ErrorBuilder()
            error.add("id_team", "Team does not exist")
            return error

        vacante.id_equipo = data.team_id

        vacante = self.vacante_repository.crear(vacante)

        return vacante.build_dto()

    def vacante_preseleccion(
        self, id_empresa: int, id_vacante: int, data: VacantePreseleccionDTO
    ) -> Union[VacanteDTO, ErrorBuilder]:
        vacante = self.vacante_get_use(
            id_empresa=id_empresa, id_vacante=id_vacante, use="edit"
        )
        if isinstance(vacante, ErrorBuilder):
            return vacante

        error = ErrorBuilder()

        candidato = self.vacante_repository.get_candidato_by_id(data.id_candidate)
        if not candidato:
            error.add("id_candidate", "Candidate not found")
            return error

        if candidato.id in [c.id_candidato for c in vacante.preseleccion]:
            error.add("id_candidate", "Candidate already preselected")
            return error

        vacante.preseleccion.append(
            VacanteCandidato(id_vacante=id_vacante, id_candidato=candidato.id)
        )
        vacante = self.vacante_repository.update(vacante)

        return vacante.build_dto()

    def vacante_resultado_prueba_tecnica(
        self,
        id_empresa: int,
        id_vacante: int,
        data: List[VacanteResultadoPruebaTecnicaDTO],
    ):
        vacante = self.vacante_get_use(
            id_empresa=id_empresa, id_vacante=id_vacante, use="edit"
        )
        if isinstance(vacante, ErrorBuilder):
            return vacante

        preseleccion = vacante.preseleccion
        indexed = {p.id_candidato: p for p in preseleccion}
        ids = indexed.keys()

        error = ErrorBuilder()
        for result in data:
            if result.id_candidate not in ids:
                error.add(
                    "global",
                    f"Candidate with id: {result.id_candidate} not preselected",
                )
                return error

        for result in data:
            vc = indexed[result.id_candidate]
            vc.puntaje = result.result

        self.vacante_repository.update(vacante)

        return vacante.build_dto()

    def vacante_set_fecha_entrevista(
        self,
        id_empresa: int,
        id_vacante: int,
        data: VacanteSetFechaEntrevistaDTO,
    ) -> Union[VacanteDTO, ErrorBuilder]:
        vacante = self.vacante_get_use(
            id_empresa=id_empresa, id_vacante=id_vacante, use="edit"
        )
        if isinstance(vacante, ErrorBuilder):
            return vacante

        vacante.fecha_entrevista = data.interview_date
        self.vacante_repository.update(vacante)
        return vacante.build_dto()

    def vacante_get_use(
        self, id_empresa: int, id_vacante: int, use: str
    ) -> Union[Vacante, ErrorBuilder]:
        error = ErrorBuilder()
        vacante = self.vacante_repository.get_by_id(
            id=id_vacante, id_empresa=id_empresa
        )
        if not vacante:
            error.add("global", "Vacancy not found")
            return error

        if use == "edit" and not vacante.abierta:
            error.add("global", "Vacancy is closed, can't be edited")
            return error

        return vacante

    def vacante_seleccionar(
        self,
        id_empresa: int,
        id_vacante: int,
        data: VacanteSelecconarCandidatoDTO,
    ) -> Union[VacanteDTO, ErrorBuilder]:
        error = ErrorBuilder()
        vacante = self.vacante_get_use(
            id_empresa=id_empresa, id_vacante=id_vacante, use="edit"
        )
        if isinstance(vacante, ErrorBuilder):
            return vacante

        equipo = self.equipo_repository.get_by_id(
            id=vacante.id_equipo, id_empresa=id_empresa
        )
        assert equipo is not None

        vc = next(
            (c for c in vacante.preseleccion if c.id_candidato == data.id_candidate),
            None,
        )
        if not vc:
            error.add("id_candidate", "Candidate not preselected")
            return error

        candidato = vc.candidato

        if candidato.persona.empleado:
            error.add(
                "id_candidate", "Candidate is already an employee, can't be hired"
            )
            return error

        # Close vacante, create an employee record associated with the candidate
        vacante.abierta = False

        empleado = Empleado()
        empleado.id_empresa = id_empresa
        empleado.cargo = vacante.name
        empleado.id_persona = candidato.id_persona
        empleado.personalidad_id = 1
        empleado.contratado_abc = True
        equipo.empleados.append(empleado)

        session = self.repository.session
        session.begin_nested()
        try:
            self.empleado_repository.crear(empleado)
            self.vacante_repository.update(vacante)
            self.equipo_repository.update(equipo)
            self.repository.session.commit()
        except Exception as e:
            self.repository.session.rollback()
            raise e

        return vacante.build_dto()

    def empleado_evaluacion_desempeno(
        self,
        id_empresa: int,
        id_empleado: int,
        data: EmpleadoEvaluacionDesempenoCreateDTO,
    ) -> Union[EmpleadoDTO, ErrorBuilder]:
        empleado = self.empleado_repository.get_by_id(
            id=id_empleado, id_empresa=id_empresa
        )
        error = ErrorBuilder()
        if not empleado:
            error.add("global", "Employee not found")
            return error

        if not empleado.contratado_abc:
            error.add("global", "Employee was not hired using ABC Jobs")
            return error

        for evaluacion in empleado.evaluaciones_desempeno:
            if (
                evaluacion.fecha.year == data.date.year
                and evaluacion.fecha.month == data.date.month
            ):
                error.add("date", "Evaluation already exists for this year and month")
                return error

        date = datetime.date(year=data.date.year, month=data.date.month, day=1)
        empleado.evaluaciones_desempeno.append(
            EvaluacionDesempeno(
                id_empleado=id_empleado,
                fecha=date,
                puntaje=data.result,
            )
        )

        empleado = self.empleado_repository.update(empleado)
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


def get_equipo_repository(
    session: Session = Depends(get_db_session_dependency),
) -> EquipoRepository:
    return EquipoRepository(session=session)


def get_vacaante_repository(
    session: Session = Depends(get_db_session_dependency),
) -> VacanteRepository:
    return VacanteRepository(session=session)


def get_empresa_service(
    repository: EmpresaRepository = Depends(get_empresa_repository),
    utils_service: UtilsRepository = Depends(get_utils_repository),
    empleado_repository: EmpleadoRepository = Depends(get_empleado_repository),
    equipo_repository: EquipoRepository = Depends(get_equipo_repository),
    vacante_repository: VacanteRepository = Depends(get_vacaante_repository),
) -> EmpresaService:
    return EmpresaService(
        repository=repository,
        utils_repository=utils_service,
        empleado_repository=empleado_repository,
        equipo_repository=equipo_repository,
        vacante_repository=vacante_repository,
    )
