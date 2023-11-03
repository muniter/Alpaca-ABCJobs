import csv
from pathlib import Path

from sqlalchemy import text
from ..logger import logger
from .db import get_db_session, recreate_all


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
