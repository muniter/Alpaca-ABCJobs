from fastapi.testclient import TestClient
from common.shared.database.db import get_db_session
from common.shared.database.models import Empresa
from gestion_usuarios.main import app
from faker import Faker

faker = Faker()
client = TestClient(app)


def crear_empresa():
    session = get_db_session()
    empresa = Empresa(
        nombre=faker.company(),
        email=faker.email(),
    )
    session.add(empresa)
    session.commit()
    session.refresh(empresa)
    return empresa


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
    empresa = crear_empresa()
    test_data = {
        "email": faker.email(),
        "password": faker.password(),
        "id_empresa": empresa.id,
    }
    response = client.post("/usuarios/crear", json=test_data)
    json = response.json()
    assert response.status_code == 201
    assert json["data"]["token"] is not None


def test_crear_empresa_datos_invalidos():
    test_data = {
        "email": "hola",
        "password": faker.password(),
        "id_empresa": 1,
    }

    response = client.post("/usuarios/crear", json=test_data)
    assert response.status_code == 400
    json = response.json()
    assert json["errors"]["email"] is not None
