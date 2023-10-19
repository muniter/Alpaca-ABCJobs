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
