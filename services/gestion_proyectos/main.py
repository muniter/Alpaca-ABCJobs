from typing import List
from fastapi import APIRouter, Depends, FastAPI, Response, status
from common.shared.api_models.gestion_proyectos import (
    ProyectoDTO,
    ProyectoCreateDTO,
)
from common.shared.api_models.gestion_usuarios import UsuarioCandidatoDTO, UsuarioEmpresaDTO
from common.shared.api_models.shared import (
    APIResponse,
    APIResponseModel,
)
from common.shared.fastapi import shared_app_setup
from common.shared.config import configuration
from common.shared.jwt import get_request_user_candidato, get_request_user_empresa
from gestion_proyectos.proyectos import ProyectoService, get_proyecto_service

app = FastAPI(
    openapi_url="/proyectos/openapi.json",
    docs_url="/proyectos/docs",
)
router = APIRouter()
shared_app_setup(app, router)


@router.get(
    "/project",
    response_model=APIResponseModel(List[ProyectoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_proyects(
    service: ProyectoService = Depends(get_proyecto_service),
    usuario: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_proyectos(id_empresa=usuario.id_empresa)
    return APIResponse(result=result)


@router.get(
    "/project/{id_proyecto}",
    response_model=APIResponseModel(ProyectoDTO),
    status_code=status.HTTP_200_OK,
)
def get_proyect(
    id_proyecto: int,
    service: ProyectoService = Depends(get_proyecto_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_proyecto(id_proyecto=id_proyecto, id_empresa=user.id_empresa)
    return APIResponse(result=result)


@router.post(
    "/project",
    response_model=APIResponseModel(ProyectoDTO),
    status_code=status.HTTP_201_CREATED,
)
def create_proyect(
    data: ProyectoCreateDTO,
    service: ProyectoService = Depends(get_proyecto_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.create_proyecto(id_empresa=user.id_empresa, data=data)
    return APIResponse(result=result)


if not configuration.in_aws:
    app.include_router(router)

app.include_router(router, prefix="/proyectos", tags=["proyectos"])
