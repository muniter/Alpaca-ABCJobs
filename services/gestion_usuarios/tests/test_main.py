from fastapi.testclient import TestClient
from common.shared.api_models.gestion_usuarios import UsuarioLoginDTO, UsuarioLoginResponseDTO
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Empresa, Usuario
from gestion_usuarios.main import app
from faker import Faker

from gestion_usuarios.usuarios import UsuarioRepository, UsuarioService

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


def crear_usuario_empresa():
    empresa = crear_empresa()
    usuario = Usuario(
        email=faker.email(),
        password=faker.password(),
        id_empresa=empresa.id,
    )
    session = get_db_session()
    session.add(usuario)
    session.commit()
    session.refresh(usuario)
    return usuario


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


def test_crear_empresa_email_ya_registrado():
    usuario_empresa = crear_usuario_empresa()
    test_data = {
        "email": usuario_empresa.email,
        "password": faker.password(),
        "id_empresa": 1,
    }

    response = client.post("/usuarios/crear", json=test_data)
    assert response.status_code == 400
    json = response.json()
    assert json["errors"]["email"] is not None


def test_login():
    usuario_empresa = crear_usuario_empresa()
    response = client.post(
        "/usuarios/login",
        json={
            "email": usuario_empresa.email,
            "password": usuario_empresa.password,
        },
    )
    json = response.json()
    assert response.status_code == 200
    assert json["data"]["token"] is not None
    assert json["data"]["usuario"]["id_empresa"] == usuario_empresa.id_empresa
    assert json["data"]["usuario"]["email"] == usuario_empresa.email


def test_login_email_invalido():
    usuario_empresa = crear_usuario_empresa()
    response = client.post(
        "/usuarios/login",
        json={
            "email": "hola",
            "password": usuario_empresa.password,
        },
    )
    json = response.json()
    assert response.status_code == 400
    assert json["errors"]["email"] is not None


def test_login_password_invalido():
    usuario_empresa = crear_usuario_empresa()
    response = client.post(
        "/usuarios/login",
        json={
            "email": usuario_empresa.email,
            "password": "holamiamigo",
        },
    )
    json = response.json()
    assert response.status_code == 400
    assert json["errors"]["password"] is not None


repository = UsuarioRepository(session=get_db_session())
service = UsuarioService(repository=repository)


def test_repository_get_by_id():
    usuario_empresa = crear_usuario_empresa()
    result = repository.get_by_id(usuario_empresa.id)
    assert result
    assert result.id == usuario_empresa.id


def test_repository_get_by_email():
    usuario_empresa = crear_usuario_empresa()
    result = repository.get_by_email(usuario_empresa.email)
    assert result
    assert result.id == usuario_empresa.id


def test_service_get_usuario_from_token():
    usuario_empresa = crear_usuario_empresa()
    token = service.create_token(usuario_empresa.build_usuario_dto())
    usuario_dto = service.get_usuario_from_token(token)
    assert usuario_dto.email == usuario_empresa.email
    assert usuario_dto.id == usuario_empresa.id


def test_service_login():
    usuario_empresa = crear_usuario_empresa()
    result = service.login(
        UsuarioLoginDTO(
            email=usuario_empresa.email,
            password=usuario_empresa.password,
        )
    )
    assert isinstance(result, UsuarioLoginResponseDTO)
    assert result.usuario.email == usuario_empresa.email
    assert result.usuario.id == usuario_empresa.id


def test_service_login_email_invalido():
    usuario_empresa = crear_usuario_empresa()
    result = service.login(
        UsuarioLoginDTO(
            email="hola",
            password=usuario_empresa.password,
        )
    )
    assert isinstance(result, ErrorBuilder)
    assert result.has_error
    assert result.serialize()["email"] is not None
