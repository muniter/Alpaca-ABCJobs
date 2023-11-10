import csv
from pathlib import Path

from sqlalchemy import select, text

from common.shared.database.models import (
    Empleado,
    Empresa,
    Equipo,
    Persona,
    Proyecto,
    RolesHabilidades,
    Usuario,
)
from ..logger import logger
from .db import get_db_session, recreate_all
from faker import Faker
from faker.providers import company

faker = Faker()
faker.seed_instance(90909)
faker.add_provider(company)


def seed():
    logger.info("Starting database seed")
    logger.info("Recreating database")
    with get_db_session() as session:
        logger.info("Droping public schema")
        recreate_all()
        logger.info("Recreating database done")
        logger.info("Seeding countries")
        data_dir = Path(__file__).parent.parent.parent / "data"
        with open(data_dir / "countries.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding countries done")

        with open(data_dir / "roles_habilidades.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding roles_habilidades done")

        logger.info("Seeding languages")
        with open(data_dir / "languages.csv") as f:
            reader = csv.DictReader(f)
            for row in reader:
                session.execute(
                    text("INSERT INTO lenguaje (id, nombre) VALUES (:id, :nombre)"),
                    row,
                )
            session.commit()
            logger.info("Seeding languages done")

        logger.info("Seeding datos_academicos")
        with open(data_dir / "datos_academicos.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding datos_academicos done")

        logger.info("Seeding conocimientos_tecnicos")
        with open(data_dir / "conocimientos_tecnicos.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding conocimientos_tecnicos done")

        logger.info("Seeding personalities")
        with open(data_dir / "personalities.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding personalities done")

        logger.info("Seeding examenes")
        with open(data_dir / "examenes.sql") as f:
            session.execute(text(f.read()))
            session.commit()
            logger.info("Seeding examenes done")

        logger.info("Seeding empresas")
        seed_empresas()


def seed_empresas():
    with get_db_session() as session:
        seed_empresa(session, email="empresa1@email.com")
        seed_empresa(session, email="empresa2@email.com")


def seed_empresa(session, email: str):
    empresa = Empresa(nombre=faker.company(), email=email)
    session.add(empresa)
    session.commit()
    usuario = Usuario(email=empresa.email, password="123456789", id_empresa=empresa.id)
    session.add(usuario)
    session.commit()

    personas = []
    empleados = []
    equipos = []
    roles_habilidades = session.execute(select(RolesHabilidades)).scalars().all()
    for _ in range(15):
        persona = Persona(
            nombres=faker.first_name(),
            apellidos=faker.last_name(),
            email=faker.email(),
        )
        personas.append(persona)
        session.add(persona)
        session.commit()

        empleado = Empleado(
            id_empresa=empresa.id,
            id_persona=persona.id,
            cargo=faker.job(),
            personalidad_id=faker.random_int(min=1, max=10),
            roles_habilidades=faker.random_choices(
                elements=roles_habilidades, length=3
            ),
        )
        empleados.append(empleado)
        session.add(empleado)
        session.commit()

    # Equipos
    for _ in range(1, 5):
        emp = faker.random_choices(
            elements=empleados, length=faker.random_int(min=2, max=5)
        )
        # Unique empleados
        emp = list(set(emp))
        equipo = Equipo(id_empresa=empresa.id, nombre=faker.color_name(), empleados=emp)
        session.add(equipo)
        session.commit()
        equipos.append(equipo)

    for _ in range(1, 3):
        proyecto = Proyecto(
            id_empresa=empresa.id,
            nombre="Proyect: " + faker.color_name(),
            descripcion=faker.sentence(),
            id_equipo=faker.random_element(elements=equipos).id,
        )
        session.add(proyecto)
        session.commit()

    session.commit()
