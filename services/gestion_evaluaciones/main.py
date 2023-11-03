from typing import List, Union
from fastapi import APIRouter, Depends, FastAPI, Response, status
from common.shared.api_models.gestion_evaluaciones import (
    ExamenDTO,
    ExamenRespuestaDTO,
    ExamenResultadoDTO,
    ExamenStepResponseDTO,
)
from common.shared.api_models.gestion_usuarios import UsuarioCandidatoDTO
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse, SuccessResponse
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
    response_model=Union[SuccessResponse[List[ExamenDTO]], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_all_exams(
    response: Response,
    service: ExamenService = Depends(get_examen_service),
    _: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_all_examenes()
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/exam/{id}",
    response_model=Union[SuccessResponse[ExamenDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_exam(
    response: Response,
    id: int,
    service: ExamenService = Depends(get_examen_service),
    _: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_examen(id=id)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/exam/{id}/start",
    response_model=Union[SuccessResponse[ExamenStepResponseDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def start_exam(
    response: Response,
    id: int,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.start_examen(id=id, id_candidato=user.id_candidato)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/exam-result/{id_result}/answer",
    response_model=Union[SuccessResponse[ExamenStepResponseDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def next_step(
    response: Response,
    id_result: int,
    resp: ExamenRespuestaDTO,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.process_answer(
        id_result=id_result, id_candidato=user.id_candidato, resp=resp
    )
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/exam-result",
    response_model=Union[SuccessResponse[List[ExamenResultadoDTO]], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_all_results(
    response: Response,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_all_results(id_candidato=user.id_candidato)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/exam-result/{id_result}",
    response_model=Union[SuccessResponse[ExamenResultadoDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_result(
    response: Response,
    id_result: int,
    service: ExamenService = Depends(get_examen_service),
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
):
    result = service.get_resultado(id_result=id_result, id_candidato=user.id_candidato)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


if not configuration.in_aws:
    app.include_router(router)

app.include_router(router, prefix="/evaluaciones", tags=["evaluaciones"])
