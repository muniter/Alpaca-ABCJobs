from typing import List, Union
from fastapi import APIRouter, FastAPI, Response, status, Depends
from common.shared.api_models.gestion_usuarios import UsuarioEmpresaDTO
from common.shared.jwt import get_request_user_empresa
from common.shared.logger import logger
from common.shared.fastapi import shared_app_setup
from common.shared.config import configuration
from common.shared.api_models.gestion_empresas import (
    EmpleadoCreateDTO,
    EmpleadoDTO,
    EmpleadoPersonalityDTO,
    EmpresaCreateResponseDTO,
    EmpresaCreateDTO,
    EquipoCreateDTO,
    EquipoDTO,
)
from common.shared.api_models.shared import (
    ErrorBuilder,
    SuccessResponse,
    ErrorResponse,
)
from common.shared.database.db import recreate_all
from .empresa import (
    EmpresaService,
    UtilsRepository,
    get_empresa_service,
    get_utils_repository,
)

app = FastAPI(
    openapi_url="/empresas/openapi.json",
    docs_url="/empresas/docs",
)
router = APIRouter()
shared_app_setup(app, router)


@router.get("/db_recreate")
def recreate():
    recreate_all()
    return {"status": "ok"}


@router.post(
    "/crear",
    response_model=Union[SuccessResponse[EmpresaCreateResponseDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def crear(
    data: EmpresaCreateDTO,
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
):
    result = service.crear(data)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_422_UNPROCESSABLE_ENTITY
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/employee",
    response_model=Union[SuccessResponse[EmpleadoDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def crear_employee(
    data: EmpleadoCreateDTO,
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.crear_empleado(id_empresa=user.id_empresa, data=data)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/employee",
    response_model=Union[SuccessResponse[List[EmpleadoDTO]], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_all_empleados(
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_all_empleados(id_empresa=user.id_empresa)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/employee/{id}",
    response_model=Union[SuccessResponse[EmpleadoDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_empleado(
    id: int,
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_empleado_by_id(id_empresa=user.id_empresa, id_empleado=id)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/team",
    response_model=Union[SuccessResponse[List[EquipoDTO]], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_all_team(
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_all_equipos(id_empresa=user.id_empresa)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/team/{id}",
    response_model=Union[SuccessResponse[EquipoDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_team(
    response: Response,
    id: int,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_equipo_by_id(id_empresa=user.id_empresa, id_equipo=id)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/team",
    response_model=Union[SuccessResponse[EquipoDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def crear_team(
    data: EquipoCreateDTO,
    response: Response,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.crear_equipo(id_empresa=user.id_empresa, data=data)
    if isinstance(result, ErrorBuilder):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/utils/personalities",
    response_model=Union[SuccessResponse[List[EmpleadoPersonalityDTO]], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def get_personalidades(
    respository: UtilsRepository = Depends(get_utils_repository),
):
    result = respository.get_personalidades_dto()
    return SuccessResponse(data=result)


if not configuration.in_aws:
    app.include_router(router)
app.include_router(router, prefix="/empresas", tags=["empresas"])
