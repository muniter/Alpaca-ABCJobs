from typing import List
from fastapi import Depends
from sqlalchemy import select, func
from sqlalchemy.orm import Session
from common.shared.api_models.gestion_proyectos import ProyectoDTO, ProyectoCreateDTO
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session_dependency
from common.shared.database.models import Equipo, Proyecto


class ProyectoRepository:
    session: Session

    def __init__(self, session: Session):
        self.session = session

    def get_proyecto_by_name(self, name: str, id_empresa: int) -> Proyecto | None:
        query = (
            select(Proyecto)
            .where(Proyecto.nombre == name)
            .where(Proyecto.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_proyecto(self, id_proyecto: int, id_empresa: int) -> Proyecto | None:
        query = (
            select(Proyecto)
            .where(Proyecto.id == id_proyecto)
            .where(Proyecto.id_empresa == id_empresa)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_proyectos(self, id_empresa: int) -> List[Proyecto]:
        query = select(Proyecto).where(Proyecto.id_empresa == id_empresa)
        return list(self.session.execute(query).scalars().all())

    def create_proyecto(self, proyecto: Proyecto) -> Proyecto:
        self.session.add(proyecto)
        self.session.commit()
        return proyecto

    def team_exists(self, id_team: int, id_empresa: int) -> bool:
        query = (
            select(Equipo)
            .where(Equipo.id == id_team)
            .where(Equipo.id_empresa == id_empresa)
        )
        result = self.session.execute(query).scalar_one_or_none()
        return result is not None


class ProyectoService:
    repository: ProyectoRepository

    def __init__(self, repository: ProyectoRepository):
        self.repository = repository

    def get_proyecto(
        self, id_proyecto: int, id_empresa: int
    ) -> ProyectoDTO | ErrorBuilder:
        proyecto = self.repository.get_proyecto(
            id_proyecto=id_proyecto, id_empresa=id_empresa
        )
        if not proyecto:
            error = ErrorBuilder()
            error.add("global", "Project not found")
            return error

        return proyecto.build_dto()

    def get_proyectos(self, id_empresa: int) -> List[ProyectoDTO]:
        return [
            p.build_dto() for p in self.repository.get_proyectos(id_empresa=id_empresa)
        ]

    def create_proyecto(
        self, id_empresa: int, data: ProyectoCreateDTO
    ) -> ProyectoDTO | ErrorBuilder:
        error = ErrorBuilder(data)
        exists = (
            self.repository.get_proyecto_by_name(
                name=data.name, id_empresa=id_empresa
            )
            is not None
        )
        if exists:
            error.add("name", "Project with this name already exists")
            return error

        team_exists = self.repository.team_exists(id_team=data.id_team, id_empresa=id_empresa)
        if not team_exists:
            error = ErrorBuilder()
            error.add("id_team", "Team not found")
            return error

        proyecto = Proyecto(
            nombre=data.name,
            descripcion=data.description,
            id_empresa=id_empresa,
            id_equipo=data.id_team,
        )

        proyecto = self.repository.create_proyecto(proyecto=proyecto)
        return proyecto.build_dto()


def get_proyecto_repository(
    session: Session = Depends(get_db_session_dependency),
) -> ProyectoRepository:
    return ProyectoRepository(session=session)


def get_proyecto_service(
    repository: ProyectoRepository = Depends(get_proyecto_repository),
) -> ProyectoService:
    return ProyectoService(repository=repository)
