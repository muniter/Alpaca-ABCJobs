from fastapi.testclient import TestClient
from common.shared.api_models.gestion_empresas import EmpleadoCreateDTO, EquipoCreateDTO
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.tests.helpers import crear_usuario_empresa
from gestion_empresas.empresa import (
    EmpleadoRepository,
    EmpresaRepository,
    EmpresaService,
    EquipoRepository,
    UtilsRepository,
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
empresa_service = EmpresaService(
    repository=empresa_repository,
    utils_repository=utils_repository,
    empleado_repository=empleado_repository,
    equipo_repository=equipo_repository,
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
