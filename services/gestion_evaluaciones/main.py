from typing import List
from fastapi import APIRouter, Depends, FastAPI, Response, status
from common.shared.api_models.gestion_evaluaciones import (
    ExamenDTO,
    ExamenRespuestaDTO,
    ExamenResultadoDTO,
    ExamenStepResponseDTO,
)
from common.shared.api_models.gestion_usuarios import UsuarioCandidatoDTO
from common.shared.api_models.shared import (
    APIResponse,
    APIResponseModel,
)
from common.shared.fastapi import shared_app_setup
from common.shared.config import configuration
from common.shared.jwt import get_request_user_candidato
from gestion_evaluaciones.evaluaciones import ExamenService, get_examen_service

app = FastAPI(
    openapi_url="/evaluaciones/openapi.json",
    docs_url="/evaluaciones/docs",
)
router = APIRouter()
shared_app_setup(app, router)


@router.get(
    "/exam",
    response_model=APIResponseModel(List[ExamenDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_exams(
    service: ExamenService = Depends(get_examen_service),
    usuario: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_all_examenes(id_candidato=usuario.id_candidato)
    return APIResponse(result=result)


@router.get(
    "/exam/{id_examen}",
    response_model=APIResponseModel(ExamenDTO),
    status_code=status.HTTP_200_OK,
)
def get_exam(
    id_examen: int,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_examen(id_examen=id_examen, id_candidato=user.id_candidato)
    return APIResponse(result=result)


@router.post(
    "/exam-result/{id_examen}/start",
    response_model=APIResponseModel(ExamenStepResponseDTO),
    status_code=status.HTTP_200_OK,
)
def start_exam(
    id_examen: int,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.start_examen(id=id_examen, id_candidato=user.id_candidato)
    return APIResponse(result=result)


@router.post(
    "/exam-result/{id_result}/answer",
    response_model=APIResponseModel(ExamenStepResponseDTO),
    status_code=status.HTTP_200_OK,
)
def next_step(
    id_result: int,
    resp: ExamenRespuestaDTO,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.process_answer(
        id_result=id_result, id_candidato=user.id_candidato, resp=resp
    )
    return APIResponse(result=result)


@router.get(
    "/exam-result",
    response_model=APIResponseModel(List[ExamenResultadoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_results(
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_all_results(id_candidato=user.id_candidato)
    return APIResponse(result=result)


@router.get(
    "/exam-result/{id_result}",
    response_model=APIResponseModel(ExamenResultadoDTO),
    status_code=status.HTTP_200_OK,
)
def get_result(
    id_result: int,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_resultado(id_result=id_result, id_candidato=user.id_candidato)
    return APIResponse(result=result)


if not configuration.in_aws:
    app.include_router(router)

app.include_router(router, prefix="/evaluaciones", tags=["evaluaciones"])
