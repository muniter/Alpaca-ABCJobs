from fastapi.testclient import TestClient
from common.shared.api_models.gestion_candidatos import (
    CandidatoConocimientoTecnicoBatchSetDTO,
    CandidatoConocimientoTecnicoCreateDTO,
    CandidatoCreateDTO,
    CandidatoDatosAcademicosCreateDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoPersonalInformationUpdateDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Candidato
from common.shared.tests.helpers import crear_usuario_candidato, crear_usuario_empresa
from gestion_candidatos.candidato import (
    CandidatoService,
    ConocimientoTecnicosService,
    DatosAcademicosService,
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
datos_academicos_service = DatosAcademicosService(session)
conocimientos_tecnico_service = ConocimientoTecnicosService(session)


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
        start_year=faker.date_between(start_date="-10y", end_date="-5y").year,
        end_year=faker.date_between(start_date="-4y", end_date="-1y").year,
        skills=[role.id for role in roles],
    )


def data_for_datos_academicos() -> CandidatoDatosAcademicosCreateDTO:
    return CandidatoDatosAcademicosCreateDTO(
        institution=faker.company(),
        title=faker.job(),
        start_year=faker.date_between(start_date="-10y", end_date="-5y").year,
        end_year=faker.date_between(start_date="-4y", end_date="-1y").year,
        type=1,
        achievement=None,
    )


def data_for_conocimientos_tecnicos() -> CandidatoConocimientoTecnicoCreateDTO:
    return CandidatoConocimientoTecnicoCreateDTO(
        type=1,
        description=faker.text(max_nb_chars=200),
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
    _, token = crear_usuario_candidato()
    response = client.get(
        "/personal-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["data"]["names"] is not None


def test_get_informacion_personal_by_id():
    usuario_candidato, _ = crear_usuario_candidato()
    _, token = crear_usuario_empresa()
    assert usuario_candidato.id_candidato is not None
    response = client.get(
        f"/personal-info/{usuario_candidato.id_candidato}",
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


def test_datos_academicos_tipo_titulo():
    response = client.get("/utils/title-types")
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


def test_service_datos_laborales_get():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    get = datos_laborales_service.get_by_id(result.id, candidato.id)
    assert not isinstance(get, ErrorBuilder)
    assert get.role == data.role
    assert get.company == data.company
    get = datos_laborales_service.get_by_id(result.id, candidato.id + 1)
    assert isinstance(get, ErrorBuilder)
    assert "global" in get.serialize()


def test_service_datos_laborales_create_without_skills():
    candidato = crear_candidato()
    data = data_for_datos_laborales()
    data.skills = None
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


def test_endpoint_datos_laborales_get():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/work-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert result["data"]["role"] == data.role
    assert result["data"]["company"] == data.company


def test_endpoint_datos_laborales_get_all():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_laborales()
    result = datos_laborales_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/work-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert len(result["data"]) > 0


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
    data.end_year = data.start_year - 1
    result = datos_laborales_service.crear(candidato.id, data)
    assert isinstance(result, ErrorBuilder)
    assert "end_year" in result.serialize()


def test_endpoint_datos_academicos_create():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_academicos()
    response = client.post(
        "/academic-info",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()
    assert result["data"]["institution"] == data.institution
    assert result["data"]["title"] == data.title


def test_endpoint_datos_academicos_get_all():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/academic-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert len(result["data"]) > 0


def test_endpoint_datos_academicos_get():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/academic-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert result["data"]["institution"] == data.institution
    assert result["data"]["title"] == data.title


def test_endpoint_datos_academicos_update():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    new_title = faker.job()
    data.title = new_title
    response = client.post(
        f"/academic-info/{result.id}",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()
    assert result["data"]["title"] == new_title


def test_endpoint_datos_academicos_delete():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    response = client.delete(
        f"/academic-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()


def test_service_conocimientos_tecnicos_create():
    candidato = crear_candidato()
    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.description == data.description
    assert result.type.name is not None


def test_service_conocimientos_tecnicos_get():
    candidato = crear_candidato()
    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    get = conocimientos_tecnico_service.get_by_id(result.id, candidato.id)
    assert not isinstance(get, ErrorBuilder)
    assert get.description == data.description


def test_service_conocimientos_tecnicos_get_all():
    candidato = crear_candidato()
    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    get = conocimientos_tecnico_service.get_all(candidato.id)
    assert not isinstance(get, ErrorBuilder)
    assert len(get) > 0


def test_service_conocimientos_tecnicos_update():
    candidato = crear_candidato()
    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.description == data.description
    new_description = faker.text(max_nb_chars=200)
    data.description = new_description
    result = conocimientos_tecnico_service.update(
        id=result.id, id_candidato=candidato.id, data=data
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.description == new_description


def test_service_conocimientos_tecnicos_delete():
    candidato = crear_candidato()
    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = conocimientos_tecnico_service.delete(
        id=result.id, id_candidato=candidato.id
    )
    assert not isinstance(result, ErrorBuilder)
    assert result is None


def test_endpoint_conocimientos_tecnicos_create():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_conocimientos_tecnicos()
    response = client.post(
        "/technical-info",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()
    assert result["data"]["description"] == data.description


def test_endpoint_conocimientos_tecnicos_get_all():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/technical-info",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert len(result["data"]) > 0


def test_endpoint_conocimientos_tecnicos_get():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/technical-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()
    assert result["data"]["description"] == data.description


def test_endpoint_conocimientos_tecnicos_update():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    new_description = faker.text(max_nb_chars=200)
    data.description = new_description
    response = client.post(
        f"/technical-info/{result.id}",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()
    assert result["data"]["description"] == new_description


def test_endpoint_conocimientos_tecnicos_batch_update():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    first = data_for_conocimientos_tecnicos()
    batch = CandidatoConocimientoTecnicoBatchSetDTO(
        list=[data_for_conocimientos_tecnicos() for _ in range(5)]
    )
    counter = 2
    for item in batch.list:
        item.type = counter
        counter += 1

    response = client.post(
        f"/technical-info",
        json=first.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()
    assert result["data"]["description"] == first.description
    id = result["data"]["id"]

    # Set batch
    response = client.post(
        f"/technical-info/batch-set",
        json=batch.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()
    print(result)
    assert len(result["data"]) == len(batch.list)
    # Id shouldn't be in the response
    for item in result["data"]:
        assert item["id"] != id


def test_endpoint_conocimientos_tecnicos_delete():
    usuario, token = crear_usuario_candidato()
    candidato = usuario.candidato
    assert candidato

    data = data_for_conocimientos_tecnicos()
    result = conocimientos_tecnico_service.crear(id_candidato=candidato.id, data=data)

    assert not isinstance(result, ErrorBuilder)

    response = client.delete(
        f"/technical-info/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    result = response.json()


def test_endpoint_conocimientos_tecnicos_types_util():
    response = client.get("/utils/technical-info-types")
    assert response.status_code == 200
    result = response.json()
    assert len(result["data"]) > 0
    assert result["data"][0]["id"] is not None
    assert result["data"][0]["name"] is not None


def test_service_datos_academicos_create():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.institution == data.institution
    assert result.title == data.title


def test_service_datos_academicos_get():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    get = datos_academicos_service.get_by_id(result.id, candidato.id)
    assert not isinstance(get, ErrorBuilder)
    assert get.institution == data.institution
    assert get.title == data.title
    get = datos_academicos_service.get_by_id(result.id, candidato.id + 1)
    assert isinstance(get, ErrorBuilder)
    assert "global" in get.serialize()


def test_service_datos_academicos_get_all():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    get = datos_academicos_service.get_all(candidato.id)
    assert not isinstance(get, ErrorBuilder)
    assert len(get) > 0


def test_service_datos_academicos_update():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.institution == data.institution
    assert result.title == data.title
    new_title = faker.job()
    data.title = new_title
    data.end_year = 2023
    result = datos_academicos_service.update(result.id, candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.title == new_title


def test_service_datos_academicos_update_wrong_candidato():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.institution == data.institution
    assert result.title == data.title
    new_title = faker.job()
    data.title = new_title
    new_candidate = crear_candidato()
    result = datos_academicos_service.update(result.id, new_candidate.id + 1, data)
    assert isinstance(result, ErrorBuilder)
    assert "global" in result.serialize()


def test_service_datos_academicos_create_multiple():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.institution == data.institution
    assert result.title == data.title
    new_title = faker.job()
    data.title = new_title
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    assert result.title == new_title


def test_service_datos_academicos_delete():
    candidato = crear_candidato()
    data = data_for_datos_academicos()
    result = datos_academicos_service.crear(candidato.id, data)
    assert not isinstance(result, ErrorBuilder)
    result = datos_academicos_service.delete(result.id, candidato.id)
    assert not isinstance(result, ErrorBuilder)
    assert result is None


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
