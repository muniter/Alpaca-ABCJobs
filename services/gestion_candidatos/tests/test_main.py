from fastapi.testclient import TestClient
from gestion_candidatos.main import app
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


def test_crear_candidato():
    test_data = {
        "nombres": faker.first_name(),
        "apellidos": faker.last_name(),
        "email": faker.email(),
        "password": faker.password(),
    }
    response = client.post("/crear", json=test_data).json()
    assert response["data"]["candidato"]["nombres"] == test_data["nombres"]
    assert response["data"]["candidato"]["email"] == test_data["email"]


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
