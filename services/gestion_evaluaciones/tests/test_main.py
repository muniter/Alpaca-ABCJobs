from typing import Tuple
from fastapi.testclient import TestClient
from common.shared.api_models.gestion_evaluaciones import (
    ExamenResultadoDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session
from common.shared.database.models import Usuario
from gestion_evaluaciones.evaluaciones import ExamenRepository, ExamenService
from gestion_evaluaciones.main import app
from common.shared.tests.helpers import crear_usuario_candidato
from faker import Faker

faker = Faker()
client = TestClient(app)

session = get_db_session()
examen_repository = ExamenRepository(session=session)
examen_service = ExamenService(repository=examen_repository)


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


def complete_exam() -> Tuple[Usuario, ExamenResultadoDTO, str]:
    usuario, token = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result and result.next_question is not None
    next_result = examen_service.process_answer(
        id_result=result.id_result,
        id_candidato=usuario.id_candidato,
        resp=result.next_question.answers[1],
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    next_result = examen_service.process_answer(
        id_result=result.id_result,
        id_candidato=usuario.id_candidato,
        resp=next_result.next_question.answers[1],
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.result is not None
    return usuario, next_result.result, token


def test_service_get_examenes():
    result = examen_service.get_all_examenes()
    assert not isinstance(result, ErrorBuilder)
    assert len(result) > 0


def test_endpoint_get_examenes():
    _, token = crear_usuario_candidato()
    response = client.get("/exam", headers={"Authorization": f"Bearer {token}"})
    assert response.status_code == 200
    assert len(response.json()["data"]) > 0


def test_service_get_examen():
    result = examen_service.get_examen(id=1)
    assert not isinstance(result, ErrorBuilder)
    assert result.id == 1
    assert result.skill.name == "Python Developer"


def test_endpoint_get_examen():
    _, token = crear_usuario_candidato()
    response = client.get("/exam/1", headers={"Authorization": f"Bearer {token}"})
    assert response.status_code == 200
    assert response.json()["data"]["id"] == 1
    assert response.json()["data"]["skill"]["name"] == "Python Developer"


def test_service_start_examen():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result.id_exam == 1
    assert result.next_question is not None
    assert len(result.next_question.answers) > 0


def test_endpoint_start_examen():
    _, token = crear_usuario_candidato()
    response = client.post(
        "/exam-result/1/start", headers={"Authorization": f"Bearer {token}"}
    )
    assert response.status_code == 200
    assert response.json()["data"]["id_exam"] == 1
    assert response.json()["data"]["next_question"] is not None
    assert len(response.json()["data"]["next_question"]["answers"]) > 0


def test_service_examen_process_answer():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result.id_exam == 1
    assert result.next_question is not None
    question = result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )
    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.id_exam == 1
    assert next_result.next_question is not None
    assert next_result.next_question.id != question.id


def test_service_examen_complete_exam():
    # An exam is completed with 3 answers
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result.next_question is not None
    question = result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )
    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    question = next_result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )
    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    question = next_result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )
    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is None
    assert next_result.result is not None


def test_endpoint_complete_exam():
    _, token = crear_usuario_candidato()
    response = client.post(
        "/exam-result/1/start", headers={"Authorization": f"Bearer {token}"}
    )
    assert response.status_code == 200
    initial_data = response.json()["data"]
    assert initial_data["id_exam"] == 1
    assert initial_data["id_result"] is not None
    assert initial_data["next_question"] is not None
    id_result = response.json()["data"]["id_result"]
    answer = response.json()["data"]["next_question"]["answers"][0]
    response = client.post(
        f"/exam-result/{id_result}/answer",
        json=answer,
        headers={"Authorization": f"Bearer {token}"},
    )
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["id_exam"] == 1
    assert data["id_result"] == id_result
    assert data["next_question"] is not None
    assert data["result"] is None
    # Correcta sube dificultad
    assert data["next_question"]["difficulty"] == 2
    next_question = data["next_question"]
    answer = next_question["answers"][0]
    response = client.post(
        f"/exam-result/{id_result}/answer",
        json=answer,
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    data = response.json()["data"]
    assert data["id_exam"] == 1
    assert data["id_result"] == id_result
    assert data["next_question"] is not None
    assert data["result"] is None
    # Correcta sube dificultad
    assert data["next_question"]["difficulty"] == 3
    next_question = data["next_question"]
    answer = next_question["answers"][0]
    response = client.post(
        f"/exam-result/{id_result}/answer",
        json=answer,
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    data = response.json()["data"]
    assert data["id_exam"] == 1
    assert data["id_result"] == id_result
    assert data["next_question"] is None
    assert data["result"] is not None
    assert data["result"]["result"] == 3
    assert data["result"]["completed"] is True


def test_service_examen_dificultad_adaptativa_increase():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)

    # First question is difficulty 1
    assert not isinstance(result, ErrorBuilder)
    assert result.next_question is not None

    # First answer is correct, so next question should be difficulty 2
    question = result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    assert next_result.next_question.difficulty == 2

    # First answer is correct, so next question should be difficulty 3
    question = next_result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    assert next_result.next_question.difficulty == 3


def test_service_examen_dificultad_adaptativa_mantain():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)

    # First question is difficulty 1
    assert not isinstance(result, ErrorBuilder)
    assert result.next_question is not None
    assert result.next_question.difficulty == 1

    # First answer is correct, so next question should be difficulty 2
    question = result.next_question
    answer = question.answers[0]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    assert next_result.next_question.difficulty == 2

    # Incorrect answer
    question = next_result.next_question
    answer = question.answers[1]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    assert next_result.next_question.difficulty == 2

    # Incorrect answer
    question = next_result.next_question
    answer = question.answers[1]
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is None
    assert next_result.result is not None
    # 1 Respuesta correcta, 2 incorrectas
    assert next_result.result.result == 1
    assert next_result.result.completed is True

    # Examen ya completado da error
    next_result = examen_service.process_answer(
        id_result=result.id_result, id_candidato=usuario.id_candidato, resp=answer
    )
    assert isinstance(next_result, ErrorBuilder)
    assert next_result.serialize()["global"] == "Exam already completed"


def test_service_examen_first_two_wrong_ends_short():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result.next_question is not None
    next_result = examen_service.process_answer(
        id_result=result.id_result,
        id_candidato=usuario.id_candidato,
        resp=result.next_question.answers[1],
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is not None
    next_result = examen_service.process_answer(
        id_result=result.id_result,
        id_candidato=usuario.id_candidato,
        resp=next_result.next_question.answers[1],
    )

    assert not isinstance(next_result, ErrorBuilder)
    assert next_result.next_question is None
    assert next_result.result is not None
    assert next_result.result.result == 0
    assert next_result.result.completed is True


def test_service_examen_answer_doesnt_exist():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    result = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(result, ErrorBuilder)
    assert result.next_question is not None
    resp = result.next_question.answers[1]
    resp.id = 9999
    next_result = examen_service.process_answer(
        id_result=result.id_result,
        id_candidato=usuario.id_candidato,
        resp=resp,
    )
    assert isinstance(next_result, ErrorBuilder)
    assert next_result.serialize()["global"] == "Answer not found"


def test_get_all_results():
    usuario, result, _ = complete_exam()
    assert usuario.id_candidato
    assert result.id_candidato == usuario.id_candidato
    results = examen_service.get_all_results(id_candidato=usuario.id_candidato)
    assert len(results) == 1
    assert results[0].id == result.id
    assert results[0].exam.skill.name is not None


def test_endpoint_get_result():
    usuario, result, token = complete_exam()
    assert usuario.id_candidato

    response = client.get(
        f"/exam-result/{result.id}",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    assert response.json()["data"]["id"] == result.id


def test_endpoint_get_all_results():
    usuario, result, token = complete_exam()
    assert usuario.id_candidato

    response = client.get(
        f"/exam-result",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200
    assert len(response.json()["data"]) == 1
    assert response.json()["data"][0]["id"] == result.id


def test_service_examen_twice():
    usuario, _ = crear_usuario_candidato()
    assert usuario.id_candidato is not None
    res = examen_service.start_examen(id=1, id_candidato=usuario.id_candidato)
    assert not isinstance(res, ErrorBuilder)
    res = examen_service.start_examen(id=2, id_candidato=usuario.id_candidato)
    assert not isinstance(res, ErrorBuilder)
