from typing import Union
from fastapi import APIRouter, Depends, FastAPI, Response
from common.shared.api_models.gestion_usuarios import (
    UsuarioLoginDTO,
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from common.shared.api_models.shared import ErrorBuilder, ErrorResponse, SuccessResponse
from common.shared.fastapi import shared_app_setup
from gestion_usuarios.usuarios import UsuarioService, get_usuario_service

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


app.include_router(router)  # Regiser alone so everything is at root
# Register under /evaluaciones for prod
app.include_router(router, prefix="/usuarios", tags=["usuarios"])
