from typing import Tuple
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
        dto = usuario.build_dto()
        token = create_token(dto.model_dump())
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
        dto = usuario.build_dto()
        token = create_token(dto.model_dump())
        return usuario, token
