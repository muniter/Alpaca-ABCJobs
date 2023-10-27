from typing import List, Optional, Union
from sqlalchemy import select, delete, func
from sqlalchemy.orm import Session

from fastapi import Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateResponseDTO,
    CandidatoCreateDTO,
    CandidatoDatosAcademicosCreateDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoDatosLaboralesDTO,
    CandidatoPersonalInformationDTO,
    CandidatoPersonalInformationUpdateDTO,
)
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.clients.usuario import UsuarioClient
from common.shared.database.models import (
    Candidato,
    Country,
    DatosAcademicos,
    DatosAcademicosTipo,
    DatosLaborales,
    Lenguaje,
    Persona,
    RolesHabilidades,
)
from common.shared.database.db import get_db_session_dependency
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse
from datetime import date


class PersonaRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: int) -> Union[Persona, None]:
        query = select(Persona).where(Persona.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_by_email(self, email: str) -> Union[Persona, None]:
        query = select(Persona).where(func.lower(Persona.email) == func.lower(email))
        return self.session.execute(query).scalar_one_or_none()

    def crear(self, data: Persona) -> Persona:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data


class CountryRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_code(self, code: int) -> Union[Country, None]:
        query = select(Country).where(Country.num_code == code)
        return self.session.execute(query).scalar_one_or_none()

    def get_all(self) -> List[Country]:
        query = select(Country)
        result = self.session.execute(query).scalars().all()
        return list(result)


class LenguajeRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: str) -> Union[Lenguaje, None]:
        query = select(Lenguaje).where(Lenguaje.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_all(self) -> List[Lenguaje]:
        query = select(Lenguaje)
        result = self.session.execute(query).scalars().all()
        return list(result)


class DatosLaboralesRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(
        self, id: int, id_candidato: int | None = None
    ) -> Union[DatosLaborales, None]:
        query = select(DatosLaborales).where(DatosLaborales.id == id)
        if id_candidato:
            query = query.join(
                Candidato, Candidato.id_persona == DatosLaborales.id_persona
            ).where(Candidato.id == id_candidato)

        return self.session.execute(query).scalar_one_or_none()

    def get_all_from_id_persona(self, id_persona: int) -> List[DatosLaborales]:
        query = select(DatosLaborales).where(DatosLaborales.id_persona == id_persona)
        result = self.session.execute(query).scalars().all()
        return list(result)

    def crear(self, data: DatosLaborales) -> DatosLaborales:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def update(self, data: DatosLaborales) -> DatosLaborales:
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def eliminar(self, data: DatosLaborales) -> None:
        self.session.delete(data)
        self.session.commit()


class RolesHabilidadesRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get(self, limit: int = 0) -> List[RolesHabilidades]:
        query = select(RolesHabilidades).limit(None if limit == 0 else limit)
        result = self.session.execute(query).scalars().all()
        return list(result)

    def get_by_id(self, id: int) -> Union[RolesHabilidades, None]:
        query = select(RolesHabilidades).where(RolesHabilidades.id == id)
        return self.session.execute(query).scalar_one_or_none()


class DatosAcademicosRepository:
    session: Session

    def __init__(self, session):
        self.session = session

    def get_by_id(self, id: int) -> Union[DatosAcademicos, None]:
        query = select(DatosAcademicos).where(DatosAcademicos.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_all_from_id_persona(self, id_persona: int) -> List[DatosAcademicos]:
        query = select(DatosAcademicos).where(DatosAcademicos.id_persona == id_persona)
        result = self.session.execute(query).scalars().all()
        return list(result)

    def crear(self, data: DatosAcademicos) -> DatosAcademicos:
        self.session.add(data)
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def update(self, data: DatosAcademicos) -> DatosAcademicos:
        self.session.commit()
        # Refresh
        self.session.refresh(data)
        return data

    def eliminar(self, data: DatosAcademicos) -> None:
        self.session.delete(data)
        self.session.commit()

    def eliminar_by_id(self, id: int) -> None:
        query = delete(DatosAcademicos).where(DatosAcademicos.id == id)
        self.session.execute(query)
        self.session.commit()

    def get_tipo_by_id(self, id: int) -> Union[DatosAcademicosTipo, None]:
        query = select(DatosAcademicosTipo).where(DatosAcademicosTipo.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_tipos(self) -> List[DatosAcademicosTipo]:
        query = select(DatosAcademicosTipo)
        result = self.session.execute(query).scalars().all()
        return list(result)


class CandidatoRepository:
    session: Session
    persona_repository: PersonaRepository

    def __init__(
        self,
        session,
        persona_repository: PersonaRepository,
    ):
        self.session = session
        self.persona_repository = persona_repository

    def get_by_id(self, id: int) -> Union[Candidato, None]:
        query = select(Candidato).where(Candidato.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_by_email(self, email: str) -> Union[Candidato, None]:
        query = (
            select(Candidato)
            .where(func.lower(Persona.email) == func.lower(email))
            .join(Persona)
        )
        return self.session.execute(query).scalar_one_or_none()

    def crear(self, data: CandidatoCreateDTO) -> Candidato:
        persona = Persona(
            nombres=data.nombres,
            apellidos=data.apellidos,
            email=data.email,
        )
        persona = self.persona_repository.crear(persona)
        candidato = Candidato(
            id_persona=persona.id,
        )
        self.session.add(candidato)
        self.session.commit()
        # Refresh
        self.session.refresh(candidato)
        return candidato


class CandidatoService:
    repository: CandidatoRepository
    usuario_client: UsuarioClient
    persona_repository: PersonaRepository
    country_repository: CountryRepository
    lenguaje_repository: LenguajeRepository

    def __init__(
        self,
        session: Session = Depends(get_db_session_dependency),
        repository: CandidatoRepository | None = None,
        persona_repository: PersonaRepository | None = None,
        country_repository: CountryRepository | None = None,
        lenguaje_repository: LenguajeRepository | None = None,
        usuario_client: UsuarioClient = UsuarioClient(),
    ):
        self.persona_repository = (
            PersonaRepository(session)
            if persona_repository is None
            else persona_repository
        )
        self.repository = (
            CandidatoRepository(session, self.persona_repository)
            if repository is None
            else repository
        )
        self.country_repository = (
            CountryRepository(session)
            if country_repository is None
            else country_repository
        )
        self.lenguaje_repository = (
            LenguajeRepository(session)
            if lenguaje_repository is None
            else lenguaje_repository
        )
        self.usuario_client = usuario_client

    def __crear_usuario(
        self, candidato: Candidato, password: str
    ) -> UsuarioLoginResponseDTO:
        usuario = self.usuario_client.crear_login(
            UsuarioRegisterDTO(
                email=candidato.persona.email,
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

        candidato = self.repository.crear(data)
        usuario = self.__crear_usuario(candidato, data.password)
        return CandidatoCreateResponseDTO(
            candidato=candidato.build_candidato_dto(),
            token=usuario.token,
        )

    def update_informacion_personal(
        self, id: int, data: CandidatoPersonalInformationUpdateDTO
    ) -> Union[CandidatoPersonalInformationDTO, ErrorBuilder]:
        error = ErrorBuilder(data)
        candidato = self.repository.get_by_id(id)
        assert candidato is not None
        persona = candidato.persona
        assert persona is not None

        persona.fecha_nacimiento = data.birth_date
        # At least 18 years old
        if persona.fecha_nacimiento is not None:
            delta = date.today() - persona.fecha_nacimiento
            if delta.days < 365 * 18:
                error.add("birth_date", "Must be at least 18 years old")
                return error

        persona.biografia = data.biography
        persona.direccion = data.address
        persona.celular = data.phone
        persona.country_code = data.country_code
        persona.ciudad = data.city

        if data.country_code:
            country = self.country_repository.get_by_code(data.country_code)
            if country is None:
                error.add("country_code", "Country code does not exist")
                return error
        else:
            if data.city:
                error.add("country_code", "Country is required if city is provided")
                return error

        for id_lenguaje in data.languages or []:
            lang = self.lenguaje_repository.get_by_id(id_lenguaje)
            if lang is None:
                error.add("languages", f"Invalid language selected: {id_lenguaje}")
                return error

            if lang not in persona.lenguajes:
                persona.lenguajes.append(lang)

        persona = self.persona_repository.crear(persona)
        return persona.build_informacion_personal_dto()

    def get_informacion_personal(self, id: int) -> CandidatoPersonalInformationDTO:
        candidato = self.repository.get_by_id(id)
        assert candidato is not None
        return candidato.persona.build_informacion_personal_dto()


class DatosLaboralesService:
    session: Session
    repository: DatosLaboralesRepository
    roles_repository: RolesHabilidadesRepository
    candidato_repository: CandidatoRepository

    def __init__(
        self,
        session: Session = Depends(get_db_session_dependency),
    ):
        self.session = session
        self.repository = DatosLaboralesRepository(session)
        self.candidato_repository = CandidatoRepository(
            session, PersonaRepository(session)
        )
        self.roles_repository = RolesHabilidadesRepository(session)

    def get_by_id(
        self, id: int, id_candidato: int
    ) -> Union[CandidatoDatosLaboralesDTO, ErrorBuilder]:
        result = self.repository.get_by_id(id, id_candidato)
        if not result:
            error = ErrorBuilder()
            error.add("global", "Invalid id")
            return error

        return result.build_datos_laborales_dto()

    def get_all(self, id_candidato: int) -> List[CandidatoDatosLaboralesDTO]:
        candidato = self.candidato_repository.get_by_id(id_candidato)
        if not candidato:
            return []

        datos_laborales = self.repository.get_all_from_id_persona(candidato.id_persona)
        return [datos.build_datos_laborales_dto() for datos in datos_laborales]

    def crear(
        self, id_candidato: int, data: CandidatoDatosLaboralesCreateDTO
    ) -> Union[CandidatoDatosLaboralesDTO, ErrorBuilder]:
        candidato = self.candidato_repository.get_by_id(id_candidato)
        if not candidato:
            error = ErrorBuilder(data)
            error.add("global", "Invalid candiato id")
            return error

        result = self.load(DatosLaborales(id_persona=candidato.id_persona), data)

        if isinstance(result, ErrorBuilder):
            return result

        result = self.repository.crear(result)
        return result.build_datos_laborales_dto()

    def update(
        self, id: int, id_candidato: int, data: CandidatoDatosLaboralesCreateDTO
    ):
        datos_laborales = self.repository.get_by_id(id)
        if not datos_laborales:
            error = ErrorBuilder(data)
            error.add("global", "Invalid id")
            return error

        candidato = self.candidato_repository.get_by_id(id_candidato)
        if not candidato or candidato.persona.id != datos_laborales.persona.id:
            error = ErrorBuilder(data)
            error.add("global", "You don't have permission to edit this data")
            return error

        result = self.load(datos_laborales, data)
        if isinstance(result, ErrorBuilder):
            return result

        result = self.repository.update(result)
        return result.build_datos_laborales_dto()

    def delete(self, id: int, id_candidato: int):
        datos_laborales = self.repository.get_by_id(id)
        if not datos_laborales:
            error = ErrorBuilder()
            error.add("global", "Invalid id")
            return error

        candidato = self.candidato_repository.get_by_id(id_candidato)
        if not candidato or candidato.persona.id != datos_laborales.persona.id:
            error = ErrorBuilder()
            error.add("global", "You don't have permission to edit this data")
            return error

        self.repository.eliminar(datos_laborales)

    def load(
        self, model: DatosLaborales, data: CandidatoDatosLaboralesCreateDTO
    ) -> DatosLaborales | ErrorBuilder:
        error = ErrorBuilder(data)
        if not model:
            error.add("id", "Invalid id")
            return error

        if data.end_date and data.end_date <= data.start_date:
            error.add("end_date", "End date must be after start date")
            return error

        model.empresa = data.company
        model.cargo = data.role
        model.descripcion = data.description
        model.fecha_inicio = data.start_date
        model.fecha_fin = data.end_date

        if data.skills:
            model.roles_habilidades.clear()
            for id_rol in data.skills:
                rol = self.roles_repository.get_by_id(id_rol)
                if rol is None:
                    error.add("roles", f"Invalid role selected: {id_rol}")
                    return error

                model.roles_habilidades.append(rol)

        return model


class DatosAcademicosService:
    repository: DatosAcademicosRepository
    candidato_repository: CandidatoRepository

    def __init__(
        self,
        session: Session = Depends(get_db_session_dependency),
    ):
        self.repository = DatosAcademicosRepository(session)
        self.candidato_repository = CandidatoRepository(
            session, PersonaRepository(session)
        )

    def crear(self, id_candidato: int, data: CandidatoDatosAcademicosCreateDTO):
        candidato = self.candidato_repository.get_by_id(id_candidato)
        if not candidato:
            error = ErrorBuilder(data)
            error.add("global", "Invalid candiato id")
            return error

        result = self.load(DatosAcademicos(id_persona=candidato.id_persona), data)
        if isinstance(result, ErrorBuilder):
            return result

        result = self.repository.crear(result)
        return result.build_dto()

    def update(
        self, id: int, id_candidato: int, data: CandidatoDatosAcademicosCreateDTO
    ):
        result = self.validate_permissions(id_candidato, id)
        if isinstance(result, ErrorBuilder):
            return result

        datos_academicos = self.repository.get_by_id(id)
        assert datos_academicos is not None

        result = self.load(datos_academicos, data)
        if isinstance(result, ErrorBuilder):
            return result

        result = self.repository.update(result)
        return result.build_dto()

    def delete(self, id: int, id_candidato: int):
        result = self.validate_permissions(id_candidato, id)

        if isinstance(result, ErrorBuilder):
            return result
        self.repository.eliminar_by_id(id)

    def validate_permissions(
        self, id_candidato: int, id_datos_academicos: int | None
    ) -> Optional[ErrorBuilder]:
        candidato = self.candidato_repository.get_by_id(id_candidato)
        error = ErrorBuilder()
        if not candidato:
            error.add("global", "Invalid candiate id")
            return error

        if id_datos_academicos is None:
            return

        datos_academicos = self.repository.get_by_id(id_datos_academicos)
        if not datos_academicos:
            error.add("global", "Invalid academic data id")
            return error

        if candidato.persona.id != datos_academicos.persona.id:
            error.add("global", "You don't have permission to edit this data")
            return error

    def load(self, model: DatosAcademicos, data: CandidatoDatosAcademicosCreateDTO):
        error = ErrorBuilder(data)

        if data.end_year and data.end_year < data.start_year:
            error.add("end_year", "End year must be after or equal to start year")
            return error

        if data.start_year < 1900:
            error.add("start_year", "Start year must be after 1900")
            return error

        if data.end_year and data.end_year < 1900:
            error.add("end_year", "End year must be after 1900")
            return error

        model.institucion = data.institution
        model.titulo = data.title
        model.start_year = data.start_year
        model.end_year = data.end_year
        model.logro = data.achievement

        tipo = self.repository.get_tipo_by_id(data.type)
        if not tipo:
            error.add("type", "Invalid type")
            return error

        model.tipo = tipo

        return model


def get_datos_academicos_repository(
    session: Session = Depends(get_db_session_dependency),
) -> DatosAcademicosRepository:
    return DatosAcademicosRepository(session)


def get_datos_academicos_service(
    session: Session = Depends(get_db_session_dependency),
) -> DatosAcademicosService:
    return DatosAcademicosService(session=session)


def get_persona_repository(
    session: Session = Depends(get_db_session_dependency),
) -> PersonaRepository:
    return PersonaRepository(session)


def get_country_repository(
    session: Session = Depends(get_db_session_dependency),
) -> CountryRepository:
    return CountryRepository(session)


def get_lenguaje_repository(
    session: Session = Depends(get_db_session_dependency),
) -> LenguajeRepository:
    return LenguajeRepository(session)


def get_candidato_repository(
    session: Session = Depends(get_db_session_dependency),
    persona_repository: PersonaRepository = Depends(get_persona_repository),
) -> CandidatoRepository:
    return CandidatoRepository(session, persona_repository)


def get_candidato_service(
    repository: CandidatoRepository = Depends(get_candidato_repository),
    persona_repository: PersonaRepository = Depends(get_persona_repository),
    country_repository: CountryRepository = Depends(get_country_repository),
    lenguaje_repository: LenguajeRepository = Depends(get_lenguaje_repository),
) -> CandidatoService:
    return CandidatoService(
        repository=repository,
        persona_repository=persona_repository,
        country_repository=country_repository,
        lenguaje_repository=lenguaje_repository,
    )


def get_roles_habilidades_repository(
    session: Session = Depends(get_db_session_dependency),
) -> RolesHabilidadesRepository:
    return RolesHabilidadesRepository(session)


def get_datos_laborales_service(
    session: Session = Depends(get_db_session_dependency),
) -> DatosLaboralesService:
    return DatosLaboralesService(session=session)
