from typing import Union
from fastapi import APIRouter, Depends, FastAPI, Response
from common.shared.api_models.gestion_usuarios import (
    UsuarioConfigDTO,
    UsuarioDTO,
    UsuarioLoginDTO,
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse, SuccessResponse
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
    response_model=Union[SuccessResponse[UsuarioLoginResponseDTO], ErrorResponse],
    status_code=201,
)
def crear(
    data: UsuarioRegisterDTO,
    response: Response,
    service: UsuarioService = Depends(get_usuario_service),
):
    result = service.crear_login(data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/login",
    response_model=Union[SuccessResponse[UsuarioLoginResponseDTO], ErrorResponse],
    status_code=200,
)
def login(
    data: UsuarioLoginDTO,
    response: Response,
    service: UsuarioService = Depends(get_usuario_service),
):
    result = service.login(data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/me",
    response_model=Union[SuccessResponse[UsuarioDTO], ErrorResponse],
    status_code=200,
)
def me(
    user: UsuarioDTO = Depends(get_request_user),
):
    return SuccessResponse(data=user)


@router.post(
    "/config",
    response_model=Union[SuccessResponse[UsuarioConfigDTO], ErrorResponse],
    status_code=200,
)
def set_config(
    config: UsuarioConfigDTO,
    current_user: UsuarioDTO = Depends(get_request_user),
    repository: UsuarioRepository = Depends(get_usuario_repository),
):
    repository.set_config(current_user.id, config.config)
    return SuccessResponse(data=config)


@router.get(
    "/config",
    response_model=Union[SuccessResponse[UsuarioConfigDTO], ErrorResponse],
    status_code=200,
)
def get_config(
    current_user: UsuarioDTO = Depends(get_request_user),
    repository: UsuarioRepository = Depends(get_usuario_repository),
):
    user_model = repository.get_by_id(current_user.id)
    assert current_user is not None
    print(user_model.config)
    return SuccessResponse(data=UsuarioConfigDTO(config=user_model.config))


app.include_router(router)  # Regiser alone so everything is at root
# Register under /evaluaciones for prod
app.include_router(router, prefix="/usuarios", tags=["usuarios"])
