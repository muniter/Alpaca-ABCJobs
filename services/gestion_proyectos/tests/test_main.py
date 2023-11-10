from typing import Tuple
from fastapi.testclient import TestClient
from common.shared.api_models.gestion_proyectos import (
    ProyectoCreateDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Usuario
from gestion_proyectos.proyectos import ProyectoRepository, ProyectoService
from gestion_proyectos.main import app
from common.shared.tests.helpers import (
    crear_usuario_candidato,
    crear_usuario_empresa,
    get_empresa,
    usuario_empresa_existente,
)
from faker import Faker

faker = Faker()
client = TestClient(app)

session = get_db_session()
proyecto_repository = ProyectoRepository(session=session)
proyecto_service = ProyectoService(repository=proyecto_repository)


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


def create_proyecto_data(id_team: int):
    return ProyectoCreateDTO(
        name=faker.company(),
        description=faker.text(),
        id_team=id_team,
    )


def test_service_get_proyectos():
    usuario, _ = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    result = proyecto_service.get_proyectos(id_empresa=usuario.id_empresa)
    assert not isinstance(result, ErrorBuilder)
    assert len(result) > 0

    proyecto = result[0]
    assert proyecto.id is not None
    assert proyecto.name is not None

    result = proyecto_service.get_proyecto(
        id_proyecto=proyecto.id, id_empresa=usuario.id_empresa
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.id == proyecto.id
    assert result.name == proyecto.name


def test_crear_proyecto():
    usuario, _ = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    result = proyecto_service.create_proyecto(
        id_empresa=usuario.id_empresa, data=create_proyecto_data(id_team=1)
    )
    assert not isinstance(result, ErrorBuilder)
    assert result.id is not None
    assert result.name is not None


def test_crear_proyecto_invalid_team():
    usuario, _ = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    result = proyecto_service.create_proyecto(
        id_empresa=usuario.id_empresa, data=create_proyecto_data(id_team=999999)
    )
    assert isinstance(result, ErrorBuilder)


def test_crear_proyecto_duplicate_name():
    usuario, _ = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    data = create_proyecto_data(id_team=1)
    result = proyecto_service.create_proyecto(id_empresa=usuario.id_empresa, data=data)
    assert not isinstance(result, ErrorBuilder)
    assert result.id is not None
    assert result.name is not None

    result = proyecto_service.create_proyecto(id_empresa=usuario.id_empresa, data=data)
    assert isinstance(result, ErrorBuilder)
    errors = result.serialize()
    assert errors["name"] is not None


def test_endpoint_get_proyectos():
    usuario, token = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    response = client.get(
        "/proyectos/project",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    assert len(response.json()["data"]) > 0

    proyecto = response.json()["data"][0]
    assert proyecto["id"] is not None
    assert proyecto["name"] is not None

    response = client.get(
        f"/proyectos/project/{proyecto['id']}",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    assert response.json()["data"]["id"] == proyecto["id"]
    assert response.json()["data"]["name"] == proyecto["name"]


def test_endpoint_crear_proyecto():
    usuario, token = usuario_empresa_existente()
    assert usuario.id_empresa is not None
    response = client.post(
        "/proyectos/project",
        headers={"Authorization": f"Bearer {token}"},
        json=create_proyecto_data(id_team=1).model_dump(),
    )
    assert response.status_code == 201
    assert response.json()["data"]["id"] is not None
    assert response.json()["data"]["name"] is not None
