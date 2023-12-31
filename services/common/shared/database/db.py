from typing import Generator
from sqlalchemy.orm import Session
from ..config import configuration
from sqlalchemy import create_engine, text
from ..logger import logger

engine = create_engine(configuration.db_uri, echo=configuration.debug)


def create_all():
    from . import models

    models.Base.metadata.create_all(engine)


def recreate_all():
    from . import models

    models.Base.metadata.drop_all(engine)
    models.Base.metadata.create_all(engine)


def get_db_session() -> Session:
    session = Session(engine, autoflush=False)
    return session


def get_db_session_dependency() -> Generator[Session, None, None]:
    session = get_db_session()
    try:
        yield session
    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()


def connection_check():
    with get_db_session() as session:
        try:
            session.execute(text("SELECT 1"))
            logger.info("Connection to database successful")
            return True
        except Exception as _:
            logger.error("Connection to database failed")
            return False
