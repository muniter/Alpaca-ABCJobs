from fastapi.testclient import TestClient
from common.shared.api_models.gestion_empresas import (
    EmpleadoCreateDTO,
    EquipoCreateDTO,
    VacanteCreateDTO,
    VacantePreseleccionDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.tests.helpers import (
    crear_usuario_empresa,
    create_token_from_usuario,
    usuario_empresa_existente,
)
from gestion_empresas.empresa import (
    EmpleadoRepository,
    EmpresaRepository,
    EmpresaService,
    EquipoRepository,
    UtilsRepository,
    VacanteRepository,
)
from gestion_empresas.main import app
from faker import Faker

faker = Faker()
client = TestClient(app)

session = get_db_session()
empresa_repository = EmpresaRepository(session=session)
utils_repository = UtilsRepository(session=session)
empleado_repository = EmpleadoRepository(session=session)
equipo_repository = EquipoRepository(session=session)
vacante_repository = VacanteRepository(session=session)
empresa_service = EmpresaService(
    repository=empresa_repository,
    utils_repository=utils_repository,
    empleado_repository=empleado_repository,
    equipo_repository=equipo_repository,
    vacante_repository=vacante_repository,
)


def empleado_create_dto():
    return EmpleadoCreateDTO(
        name=faker.name(),
        title=faker.job(),
        skills=list(range(1, faker.random_int(1, 5))),
        personality_id=faker.random_int(1, 10),
    )


def crear_equipo_dto(id_empresa: int, numero_empleados: int):
    empleados = empleado_repository.get_all(id_empresa=id_empresa)
    if len(empleados) < numero_empleados:
        for _ in range(numero_empleados - len(empleados)):
            empresa_service.crear_empleado(
                id_empresa=id_empresa, data=empleado_create_dto()
            )

    empleados = empleado_repository.get_all(id_empresa=id_empresa)
    return EquipoCreateDTO(
        name=faker.name(),
        employees=[empleado.id for empleado in empleados][:numero_empleados],
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


def test_crear_empresa():
    test_data = {
        "nombre": faker.company(),
        "email": faker.email(),
        "password": faker.password(),
    }
    response = client.post("/crear", json=test_data).json()
    assert response["data"]["empresa"]["nombre"] == test_data["nombre"]
    assert response["data"]["empresa"]["email"] == test_data["email"]


def test_crear_empresa_error():
    test_data = {
        "nombre": "a",
        "email": faker.email(),
        "password": faker.password(),
    }
    # Print the response to see the error
    response = client.post("/crear", json=test_data).json()
    assert "at least 2" in response["errors"]["nombre"]


def test_service_crear_empleado():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    result = empresa_service.crear_empleado(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.name == data.name
    assert result.title == data.title
    assert result.personality.id == data.personality_id
    assert len(result.skills) == len(data.skills)


def test_service_get_empleado():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    result = empresa_service.crear_empleado(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = empresa_service.get_empleado_by_id(
        id_empresa=id_empresa, id_empleado=result.id
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.name == data.name
    assert result.title == data.title
    assert result.personality.id == data.personality_id
    assert len(result.skills) == len(data.skills)


def test_endpoint_get_empleado():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    result = empresa_service.crear_empleado(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/employee/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert result["name"] == data.name
    assert result["title"] == data.title
    assert result["personality"]["id"] == data.personality_id
    assert len(result["skills"]) == len(data.skills)


def test_service_get_all_empleados():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    result = empresa_service.crear_empleado(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = empresa_service.get_all_empleados(id_empresa=id_empresa)
    assert not isinstance(result, ErrorBuilder)
    assert len(result) == 1
    assert result[0].name == data.name


def test_endpoint_get_all_empleados():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    result = empresa_service.crear_empleado(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        "/employee",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert len(result) == 1
    assert result[0]["name"] == data.name


def test_endpoint_crear_empleado():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = empleado_create_dto()
    data.name = "hola"
    response = client.post(
        "/employee",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()["data"]
    assert result["name"] == data.name
    assert result["title"] == data.title
    assert result["personality"]["id"] == data.personality_id
    assert len(result["skills"]) == len(data.skills)


def test_service_crear_equipo():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    result = empresa_service.crear_equipo(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.name == data.name
    assert len(result.employees) == len(data.employees)


def test_endpoint_crear_equipo():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    response = client.post(
        "/team",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()["data"]
    assert result["name"] == data.name
    assert len(result["employees"]) == len(data.employees)


def test_service_get_equipo():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    result = empresa_service.crear_equipo(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = empresa_service.get_equipo_by_id(
        id_empresa=id_empresa, id_equipo=result.id
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.name == data.name
    assert len(result.employees) == len(data.employees)


def test_endpoint_get_equipo():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    result = empresa_service.crear_equipo(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        f"/team/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert result["name"] == data.name
    assert len(result["employees"]) == len(data.employees)


def test_service_get_all_equipo():
    usuario, _ = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    result = empresa_service.crear_equipo(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    result = empresa_service.get_all_equipos(id_empresa=id_empresa)
    assert not isinstance(result, ErrorBuilder)
    assert len(result) == 1
    assert result[0].name == data.name


def test_endpoint_get_all_equipo():
    usuario, token = crear_usuario_empresa()
    id_empresa = usuario.id_empresa
    assert id_empresa

    data = crear_equipo_dto(id_empresa=id_empresa, numero_empleados=3)
    result = empresa_service.crear_equipo(id_empresa=id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    response = client.get(
        "/team",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert len(result) == 1
    assert result[0]["name"] == data.name


def test_endpoint_utils_personalities():
    response = client.get("/utils/personalities")
    assert response.status_code == 200
    result = response.json()["data"]
    assert len(result) > 0


def crear_vacante_dto(id_equipo: int):
    return VacanteCreateDTO(
        team_id=id_equipo,
        name=faker.name(),
        description=faker.text(),
    )


def crear_vacante(id_equipo: int | None = None):
    usuario, _ = usuario_empresa_existente()
    id_empresa = usuario.id_empresa
    assert id_empresa
    equipos = equipo_repository.get_all(id_empresa=id_empresa)
    equipo = equipos[0]
    data = crear_vacante_dto(id_equipo=id_equipo or equipo.id)
    result = empresa_service.crear_vacante(id_empresa=id_empresa, data=data)
    return result, data, usuario


def test_service_crear_vacante():
    result, data, _ = crear_vacante()
    assert not isinstance(result, ErrorBuilder)
    assert result.name == data.name
    assert result.description == data.description
    assert result.team.id == data.team_id


def test_service_crear_vacante_error():
    result, _, _ = crear_vacante(id_equipo=999999)
    assert isinstance(result, ErrorBuilder)
    assert result.serialize()["id_team"] is not None


def test_service_get_vacante():
    vacante, _, _ = crear_vacante()
    assert not isinstance(vacante, ErrorBuilder)
    # Get
    result = empresa_service.get_vacante_by_id(
        id_empresa=vacante.company.id, id_vacante=vacante.id
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.id == vacante.id
    assert result.name == vacante.name
    assert result.description == vacante.description

    # Get non existent
    result = empresa_service.get_vacante_by_id(
        id_empresa=vacante.company.id, id_vacante=999999
    )
    assert isinstance(result, ErrorBuilder)

    # Get All
    result = empresa_service.get_all_vacantes(id_empresa=vacante.company.id)
    assert not isinstance(result, ErrorBuilder)
    assert vacante.id in [v.id for v in result]


def test_endpoint_get_vacante():
    vacante, _, usuario = crear_vacante()
    token = create_token_from_usuario(usuario)
    assert not isinstance(vacante, ErrorBuilder)

    response = client.get(
        f"/vacancy/{vacante.id}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert result["id"] == vacante.id
    assert result["name"] == vacante.name
    assert result["description"] == vacante.description

    # Get all
    response = client.get(
        "/vacancy",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    result = response.json()["data"]
    assert vacante.id in [v["id"] for v in result]


def test_endpoint_crear_vacante():
    _, data, usuario = crear_vacante()
    token = create_token_from_usuario(usuario)

    response = client.post(
        "/vacancy",
        json=data.model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 201
    result = response.json()["data"]
    assert result["name"] == data.name
    assert result["description"] == data.description


def test_preselecionar_vacante():
    vacante, _, _ = crear_vacante()
    assert not isinstance(vacante, ErrorBuilder)
    result = empresa_service.preselecionar_vacante(
        id_empresa=vacante.company.id,
        id_vacante=vacante.id,
        data=VacantePreseleccionDTO(
            id_candidate=1,
        ),
    )

    assert not isinstance(result, ErrorBuilder)
    assert result.id == vacante.id
    assert result.name == vacante.name
    assert result.preselection is not None
    assert 1 in [c.id_candidate for c in result.preselection]

    # Try again is an error
    result = empresa_service.preselecionar_vacante(
        id_empresa=vacante.company.id,
        id_vacante=vacante.id,
        data=VacantePreseleccionDTO(
            id_candidate=1,
        ),
    )
    assert isinstance(result, ErrorBuilder)
    assert result.serialize()["id_candidate"] is not None

    # If candidate doesn't exist
    result = empresa_service.preselecionar_vacante(
        id_empresa=vacante.company.id,
        id_vacante=vacante.id,
        data=VacantePreseleccionDTO(
            id_candidate=999999,
        ),
    )
    assert isinstance(result, ErrorBuilder)
    assert result.serialize()["id_candidate"] is not None

    # If the vacancy doesn't exist
    result = empresa_service.preselecionar_vacante(
        id_empresa=vacante.company.id,
        id_vacante=999999,
        data=VacantePreseleccionDTO(
            id_candidate=1,
        ),
    )
    assert isinstance(result, ErrorBuilder)
    assert result.serialize()["global"] is not None


def test_preselecionar_vacante_endpoint():
    vacante, _, usuario = crear_vacante()
    assert not isinstance(vacante, ErrorBuilder)

    token = create_token_from_usuario(usuario)

    response = client.post(
        f"/vacancy/{vacante.id}/preselect",
        json=VacantePreseleccionDTO(
            id_candidate=1,
        ).model_dump(mode="json"),
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 201
    result = response.json()["data"]
    assert result["id"] == vacante.id
    assert result["preselection"] is not None
    assert 1 in [c["id_candidate"] for c in result["preselection"]]
