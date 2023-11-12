from typing import Tuple

from sqlalchemy import select
from common.shared.jwt import create_token
from ..database.db import get_db_session
from ..database.models import Candidato, Empresa, Persona, Usuario
from faker import Faker

faker = Faker()

def crear_persona() -> Persona:
    with get_db_session() as session:
        persona = Persona(
            nombres=faker.first_name(),
            apellidos=faker.last_name(),
            email=faker.email(),
        )
        session.add(persona)
        session.commit()
        session.refresh(persona)
        return persona


def crear_candidato() -> Candidato:
    with get_db_session() as session:
        persona = crear_persona()
        candidato = Candidato(
            id_persona=persona.id,
        )
        session.add(candidato)
        session.commit()
        session.refresh(candidato)
        return candidato


def crear_empresa() -> Empresa:
    with get_db_session() as session:
        empresa = Empresa(
            nombre=faker.company(),
            email=faker.email(),
        )
        session.add(empresa)
        session.commit()
        session.refresh(empresa)
        return empresa


def get_empresa(id: int) -> Empresa:
    with get_db_session() as session:
        empresa = session.execute(
            select(Empresa).where(Empresa.id == id)
        ).scalar_one_or_none()
        assert empresa
        return empresa


def create_token_from_usuario(usuario: Usuario) -> str:
    dto = usuario.build_dto()
    token = create_token(dto.model_dump())
    return token


def crear_usuario_empresa() -> Tuple[Usuario, str]:
    with get_db_session() as session:
        empresa = crear_empresa()
        usuario = Usuario(
            email=faker.email(),
            password=faker.password(),
            id_empresa=empresa.id,
        )
        session.add(usuario)
        session.commit()
        session.refresh(usuario)
        token = create_token_from_usuario(usuario)
        return usuario, token


def usuario_empresa_existente(id_empresa: int = 1) -> Tuple[Usuario, str]:
    with get_db_session() as session:
        usuario = session.execute(
            select(Usuario).where(Usuario.id_empresa == id_empresa)
        ).scalar_one_or_none()
        assert usuario
        token = create_token_from_usuario(usuario)
        return usuario, token


def crear_usuario_candidato() -> Tuple[Usuario, str]:
    with get_db_session() as session:
        candidato = crear_candidato()
        usuario = Usuario(
            email=faker.email(),
            password=faker.password(),
            id_candidato=candidato.id,
        )
        session.add(usuario)
        session.commit()
        session.refresh(usuario)
        token = create_token_from_usuario(usuario)
        return usuario, token
