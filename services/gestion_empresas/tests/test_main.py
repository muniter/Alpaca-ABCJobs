from fastapi.testclient import TestClient
from gestion_empresas.main import app
from faker import Faker

faker = Faker()
client = TestClient(app)


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
