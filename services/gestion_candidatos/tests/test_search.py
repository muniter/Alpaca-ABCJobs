from fastapi.testclient import TestClient
from gestion_candidatos.main import app
from sqlalchemy import select, func
from common.shared.api_models.gestion_candidatos import CandidatoSearchDTO
from common.shared.database.db import get_db_session
from common.shared.tests.helpers import crear_usuario_empresa
from common.shared.database.models import (
    Candidato,
    ConocimientoTecnicos,
    DatosAcademicos,
    DatosLaborales,
    Lenguaje,
    Persona,
    person_language,
    datos_laborales_roles,
)
from gestion_candidatos.candidato import CandidatoSearchService


client = TestClient(app)
session = get_db_session()
search_service = CandidatoSearchService(session)


def debug_query(query):  # pragma: no cover
    print(str(query.compile(compile_kwargs={"literal_binds": True})))
    assert False


def test_search_lenguaje():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(person_language, person_language.c.id_persona == Persona.id)
        .join(Lenguaje, Lenguaje.id == person_language.c.id_lenguaje)
        .having(func.count(Candidato.id) > 1)
        .group_by(Candidato.id)
    )
    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    one = result[0]
    languages = [lenguaje.id for lenguaje in one.persona.lenguajes]

    result = search_service.search(
        CandidatoSearchDTO(
            languages=languages,
        )
    )
    assert len(result) > 0
    print("Found", len(result), "candidatos")
    candidatos = select(Candidato).where(Candidato.id.in_([c.id for c in result]))
    for candidato in session.execute(candidatos).scalars().all():
        for lenguaje in candidato.persona.lenguajes:
            assert lenguaje.id in [leng.id for leng in candidato.persona.lenguajes]


def test_search_tecnical_info_types():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(Persona.conocimientos_tecnicos)
        .where(ConocimientoTecnicos.id.isnot(None))
        .having(func.count(ConocimientoTecnicos.id) > 1)
        .group_by(Candidato.id)
    )

    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    first = result[0]
    assert len(first.persona.conocimientos_tecnicos) > 0

    result = search_service.search(
        CandidatoSearchDTO(
            technical_info_types=[first.persona.conocimientos_tecnicos[0].id_tipo],
        )
    )
    assert len(result) > 0
    assert first.id in [c.id for c in result]


def test_search_country_code():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .where(Persona.country_code.isnot(None))
    )
    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    first = result[0]
    assert first.persona.country_code is not None
    result = search_service.search(
        CandidatoSearchDTO(
            country_code=first.persona.country_code,
        )
    )
    assert len(result) > 0
    assert first.id in [c.id for c in result]


def test_search_least_academic_level():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(DatosAcademicos, DatosAcademicos.id_persona == Persona.id)
        .where(DatosAcademicos.id_tipo > 5)
        .group_by(Candidato.id)
    )
    result = session.execute(existing).scalars().all()
    assert len(result) > 0

    result = search_service.search(
        CandidatoSearchDTO(
            least_academic_level=5,
        )
    )

    assert len(result) > 0
    candidatos = select(Candidato).where(Candidato.id.in_([c.id for c in result]))
    for candidato in session.execute(candidatos).scalars().all():
        da = candidato.persona.datos_academicos
        max_level = max([d.id_tipo for d in da])
        assert max_level >= 5


def test_search_study_areas():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(DatosAcademicos, DatosAcademicos.id_persona == Persona.id)
        .group_by(Candidato.id)
        .limit(1)
    )

    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    first = result[0]
    titulo = first.persona.datos_academicos[0].titulo
    partial = titulo[: int(len(titulo) / 2)]
    print(partial)

    result = search_service.search(
        CandidatoSearchDTO(
            study_areas=[partial],
        )
    )

    assert len(result) > 0

    assert first.id in [c.id for c in result]

    candidatos = select(Candidato).where(Candidato.id.in_([c.id for c in result]))
    for candidato in session.execute(candidatos).scalars().all():
        da = candidato.persona.datos_academicos
        filtered = [d for d in da if partial in d.titulo]
        assert len(filtered) > 0


def test_roles_habilidades():
    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(DatosLaborales, DatosLaborales.id_persona == Persona.id)
        .join(
            datos_laborales_roles,
            datos_laborales_roles.c.id_datos_laborales == DatosLaborales.id,
        )
        .group_by(Candidato.id)
        .limit(1)
    )

    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    first = result[0]
    assert len(first.persona.datos_laborales) > 0
    datos_laborales = [da for da in first.persona.datos_laborales]
    skills = [d.id for d in datos_laborales[0].roles_habilidades]

    result = search_service.search(
        CandidatoSearchDTO(
            skills=skills,
        )
    )

    assert len(result) > 0
    assert first.id in [c.id for c in result]


def test_by_multiple():
    # Random candidato
    existing = select(Candidato).order_by(func.random()).limit(5)

    candidatos = session.execute(existing).scalars().all()

    for candidato in candidatos:
        search_criteria = CandidatoSearchDTO()
        if candidato.persona.country_code:
            search_criteria.country_code = candidato.persona.country_code

        if candidato.persona.datos_academicos:
            search_criteria.least_academic_level = max(
                [d.id_tipo for d in candidato.persona.datos_academicos]
            )

        if candidato.persona.lenguajes:
            search_criteria.languages = [la.id for la in candidato.persona.lenguajes]

        if candidato.persona.conocimientos_tecnicos:
            search_criteria.technical_info_types = [
                c.id_tipo for c in candidato.persona.conocimientos_tecnicos
            ]

        if candidato.persona.datos_laborales:
            search_criteria.skills = [
                d.id for d in candidato.persona.datos_laborales[0].roles_habilidades
            ]

        if candidato.persona.datos_academicos:
            search_criteria.study_areas = [
                d.titulo[: int(len(d.titulo) / 2)]
                for d in candidato.persona.datos_academicos
            ]

        result = search_service.search(search_criteria)
        assert len(result) > 0
        assert candidato.id in [c.id for c in result]


def test_endpoint():
    _, token = crear_usuario_empresa()

    existing = (
        select(Candidato)
        .join(Candidato.persona)
        .join(person_language, person_language.c.id_persona == Persona.id)
        .join(Lenguaje, Lenguaje.id == person_language.c.id_lenguaje)
        .having(func.count(Candidato.id) > 1)
        .group_by(Candidato.id)
    )
    result = session.execute(existing).scalars().all()
    assert len(result) > 0
    one = result[0]
    languages = [lenguaje.id for lenguaje in one.persona.lenguajes]

    response = client.post(
        "/search",
        json={
            "languages": languages,
        },
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    assert len(response.json()["data"]) > 0
