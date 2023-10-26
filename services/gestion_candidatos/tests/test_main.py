import json
from fastapi.testclient import TestClient
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoPersonalInformationUpdateDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Candidato
from common.shared.tests.helpers import crear_usuario_candidato, crear_usuario_empresa
from gestion_candidatos.candidato import (
    CandidatoService,
    DatosLaboralesService,
    RolesHabilidadesRepository,
)
from gestion_candidatos.main import app
from faker import Faker

faker = Faker()
client = TestClient(app)

session = get_db_session()
candidate_service = CandidatoService(session)
roles_habilidades_repository = RolesHabilidadesRepository(session)
datos_laborales_service = DatosLaboralesService(session)


def crear_candidato() -> Candidato:
    create_result = candidate_service.crear(
        CandidatoCreateDTO(
            nombres=faker.first_name(),
            apellidos=faker.last_name(),
            email=faker.email(),
            password=faker.password(),
        )
    )
    assert not isinstance(create_result, ErrorBuilder)
    candidato = candidate_service.repository.get_by_id(create_result.candidato.id)
    assert candidato is not None
    return candidato


def data_for_pi_update() -> CandidatoPersonalInformationUpdateDTO:
    return CandidatoPersonalInformationUpdateDTO(
        birth_date=faker.date_of_birth(minimum_age=18, maximum_age=100),
        country_code=4,
        city=faker.city(),
        address=faker.address(),
        phone=faker.numerify(text="#########"),
        biography=faker.text(max_nb_chars=200),
        languages=["EN", "ES"],
    )


def data_for_datos_laborales() -> CandidatoDatosLaboralesCreateDTO:
    roles = roles_habilidades_repository.get(5)
    return CandidatoDatosLaboralesCreateDTO(
        role=faker.job(),
        company=faker.company(),
        description=faker.text(max_nb_chars=200),
        start_date=faker.date_between(start_date="-10y", end_date="-5y"),
        end_date=faker.date_between(start_date="-4y", end_date="-1y"),
        skills=[role.id for role in roles],
    )


def test_ping():
    response = client.get("/ping")
    assert response.status_code == 200
    assert response.json()["ping"] == "pong"


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"
    assert "source" in response.json()
    assert "aws" in response.json()
    assert "task_data" in response.json()


def test_crear_candidato():
    test_data = {
        "nombres": faker.first_name(),
        "apellidos": faker.last_name(),
        "email": faker.email(),
        "password": faker.password(),
    }
    response = client.post("/crear", json=test_data)
    assert response.status_code == 201
    data = response.json()
    assert data["data"]["candidato"]["nombres"] == test_data["nombres"]
    assert data["data"]["candidato"]["email"] == test_data["email"]


def test_update_informacion_personal():
    _, token = crear_usuario_candidato()
    test_data = data_for_pi_update()
    response = client.post(
        "/personal-info",
        json=test_data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["data"]["country_code"] == test_data.country_code


def test_get_informacion_personal():
    usuario, token = crear_usuario_candidato()
    response = client.get(
        "/personal-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["data"]["names"] is not None


def test_get_informacion_personal_si_soy_empresa():
    _, token = crear_usuario_empresa()
    response = client.get(
        "/personal-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 401


def test_get_countries():
    response = client.get("/utils/countries")
    assert response.status_code == 200
    data = response.json()
    assert len(data["data"]) > 0


def test_get_languages():
    response = client.get("/utils/languages")
    assert response.status_code == 200
    data = response.json()
    assert len(data["data"]) > 0
    assert data["data"][0]["id"] is not None
    assert data["data"][0]["name"] is not None


def test_endpoint_skills():
    response = client.get("/utils/skills")
    assert response.status_code == 200
    data = response.json()
    assert len(data["data"]) > 0
    assert data["data"][0]["id"] is not None
    assert data["data"][0]["name"] is not None


def test_crear_candidato_error():
    test_data = {
        "nombres": "a",
        "apellidos": faker.last_name(),
        "email": faker.email(),
        "password": faker.password(),
    }
    # Print the response to see the error
    response = client.post("/crear", json=test_data).json()
    assert "at least 2" in response["errors"]["nombres"]


def test_service_update_informacion_personal():
    candidato = crear_candidato()
    data = data_for_pi_update()
    result = candidate_service.update_informacion_personal(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.biography == data.biography
    assert result.languages is not None
    assert "EN" in [lang.id for lang in result.languages]


def test_service_update_informacion_personal_no_pais():
    candidato = crear_candidato()
    data = data_for_pi_update()
    data.country_code = None
    result = candidate_service.update_informacion_personal(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "country_code" in result.serialize()


def test_service_update_under_18():
    candidato = crear_candidato()
    data = data_for_pi_update()
    data.birth_date = faker.date_of_birth(minimum_age=5, maximum_age=17)
    result = candidate_service.update_informacion_personal(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "birth_date" in result.serialize()


def test_service_datos_laborales_create():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.role == data.role
    assert result.company == data.company


def test_endpoint_datos_laborales_create():
    _, token = crear_usuario_candidato()
    data = data_for_datos_laborales()
    response = client.post(
        "/work-info",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()
    assert result["data"]["role"] == data.role
    assert result["data"]["company"] == data.company


def test_endpoint_datos_laborales_update():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    new_job = faker.job()
    data.role = new_job
    response = client.post(
        f"/work-info/{result.id}",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()
    assert result["data"]["role"] == new_job


def test_endpoint_datos_laborales_delete():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    response = client.delete(
        f"/work-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()


def test_service_datos_laborales_update():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.role == data.role
    new_job = faker.job()
    data.role = new_job
    result = datos_laborales_service.update(
        id=result.id, id_candidato=candidato.id, data=data
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.role == new_job


def test_service_datos_laborales_delete():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = datos_laborales_service.delete(id=result.id, id_candidato=candidato.id)
    assert not isinstance(result, ErrorBuilder)
    assert result is None


def test_service_datos_laborales_delete_invalid_id_candidato():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = datos_laborales_service.delete(id=result.id, id_candidato=candidato.id + 1)
    assert isinstance(result, ErrorBuilder)
    assert "global" in result.serialize()


def test_service_datos_laborales_invalid_end_date():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    data.end_date = data.start_date
    result = datos_laborales_service.crear(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "end_date" in result.serialize()


def test_invalid_language():
    candidato = crear_candidato()
    data = data_for_pi_update()
    data.languages = ["EN", "ES", "INVALID"]
    result = candidate_service.update_informacion_personal(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "languages" in result.serialize()


def test_invalid_country():
    candidato = crear_candidato()
    data = data_for_pi_update()
    data.country_code = 1
    result = candidate_service.update_informacion_personal(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "country_code" in result.serialize()
