from typing import Union
from fastapi import APIRouter, Depends, FastAPI, Response
from common.shared.api_models.gestion_usuarios import (
    UsuarioConfigDTO,
    UsuarioDTO,
    UsuarioLoginDTO,
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.config import configuration
from common.shared.api_models.shared import APIResponse, APIResponseModel
from common.shared.fastapi import shared_app_setup
from common.shared.jwt import get_request_user
from gestion_usuarios.usuarios import (
    UsuarioRepository,
    UsuarioService,
    get_usuario_repository,
    get_usuario_service,
)

app = FastAPI(
    openapi_url="/usuarios/openapi.json",
    docs_url="/usuarios/docs",
)
router = APIRouter()
shared_app_setup(app, router)


@router.post(
    "/crear",
    response_model=APIResponseModel(UsuarioLoginResponseDTO),
    status_code=201,
)
def crear(
    data: UsuarioRegisterDTO,
    service: UsuarioService = Depends(get_usuario_service),
):
    result = service.crear_login(data)
    return APIResponse(result)


@router.post(
    "/login",
    response_model=APIResponseModel(UsuarioLoginResponseDTO),
    status_code=200,
)
def login(
    data: UsuarioLoginDTO,
    service: UsuarioService = Depends(get_usuario_service),
):
    result = service.login(data)
    return APIResponse(result)


@router.get(
    "/me",
    response_model=APIResponseModel(UsuarioDTO),
    status_code=200,
)
def me(
    user: UsuarioDTO = Depends(get_request_user),
):
    return APIResponse(user)


@router.post(
    "/config",
    response_model=APIResponseModel(UsuarioConfigDTO),
    status_code=200,
)
def set_config(
    config: UsuarioConfigDTO,
    current_user: UsuarioDTO = Depends(get_request_user),
    repository: UsuarioRepository = Depends(get_usuario_repository),
):
    repository.set_config(current_user.id, config.config)
    return APIResponse(config)


@router.get(
    "/config",
    response_model=APIResponseModel(UsuarioConfigDTO),
    status_code=200,
)
def get_config(
    current_user: UsuarioDTO = Depends(get_request_user),
    repository: UsuarioRepository = Depends(get_usuario_repository),
):
    user_model = repository.get_by_id(current_user.id)
    assert user_model is not None
    assert current_user is not None
    return APIResponse(UsuarioConfigDTO(config=user_model.config))


if not configuration.in_aws:
    app.include_router(router)
# Register under /evaluaciones for prod
app.include_router(router, prefix="/usuarios", tags=["usuarios"])
