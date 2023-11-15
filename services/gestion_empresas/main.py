from typing import List
from fastapi import APIRouter, FastAPI, status, Depends
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
    VacanteCreateDTO,
    VacanteDTO,
    VacantePreseleccionDTO,
    VacanteResultadoPruebaTecnicaDTO,
)
from common.shared.api_models.shared import (
    APIResponse,
    APIResponseModel,
)
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


@router.post(
    "/crear",
    response_model=APIResponseModel(EmpresaCreateResponseDTO),
    status_code=status.HTTP_201_CREATED,
)
def crear(
    data: EmpresaCreateDTO,
    service: EmpresaService = Depends(get_empresa_service),
):
    result = service.crear(data)
    return APIResponse(result)


@router.post(
    "/employee",
    response_model=APIResponseModel(EmpleadoDTO),
    status_code=status.HTTP_201_CREATED,
)
def crear_employee(
    data: EmpleadoCreateDTO,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.crear_empleado(id_empresa=user.id_empresa, data=data)
    return APIResponse(result)


@router.get(
    "/employee",
    response_model=APIResponseModel(List[EmpleadoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_empleados(
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_all_empleados(id_empresa=user.id_empresa)
    return APIResponse(result)


@router.get(
    "/employee/{id}",
    response_model=APIResponseModel(EmpleadoDTO),
    status_code=status.HTTP_200_OK,
)
def get_empleado(
    id: int,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_empleado_by_id(id_empresa=user.id_empresa, id_empleado=id)
    return APIResponse(result)


@router.get(
    "/team",
    response_model=APIResponseModel(List[EquipoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_team(
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_all_equipos(id_empresa=user.id_empresa)
    return APIResponse(result)


@router.get(
    "/team/{id}",
    response_model=APIResponseModel(EquipoDTO),
    status_code=status.HTTP_200_OK,
)
def get_team(
    id: int,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_equipo_by_id(id_empresa=user.id_empresa, id_equipo=id)
    return APIResponse(result)


@router.post(
    "/team",
    response_model=APIResponseModel(EquipoDTO),
    status_code=status.HTTP_201_CREATED,
)
def crear_team(
    data: EquipoCreateDTO,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.crear_equipo(id_empresa=user.id_empresa, data=data)
    return APIResponse(result)


@router.get(
    "/vacancy",
    response_model=APIResponseModel(List[VacanteDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_vacancies(
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.vacante_get_all(id_empresa=user.id_empresa)
    return APIResponse(result)


@router.get(
    "/vacancy/{id}",
    response_model=APIResponseModel(VacanteDTO),
    status_code=status.HTTP_200_OK,
)
def get_vacancy(
    id: int,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.vacante_get_by_id(id_empresa=user.id_empresa, id_vacante=id)
    return APIResponse(result)


@router.post(
    "/vacancy",
    response_model=APIResponseModel(VacanteDTO),
    status_code=status.HTTP_201_CREATED,
)
def crear_vacancy(
    data: VacanteCreateDTO,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.vacante_crear(id_empresa=user.id_empresa, data=data)
    return APIResponse(result)


@router.post(
    "/vacancy/{id}/preselect",
    response_model=APIResponseModel(VacanteDTO),
    status_code=status.HTTP_201_CREATED,
)
def preselect_vacancy(
    id: int,
    data: VacantePreseleccionDTO,
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.vacante_preseleccion(
        id_empresa=user.id_empresa, id_vacante=id, data=data
    )
    return APIResponse(result)


@router.post(
    "/vacancy/{id}/test-result",
    response_model=APIResponseModel(VacanteDTO),
    status_code=status.HTTP_200_OK,
)
def test_result_vacancy(
    id: int,
    data: List[VacanteResultadoPruebaTecnicaDTO],
    service: EmpresaService = Depends(get_empresa_service),
    user: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.vacante_resultado_prueba_tecnica(
        id_empresa=user.id_empresa, id_vacante=id, data=data
    )
    return APIResponse(result)


@router.get(
    "/utils/personalities",
    response_model=APIResponseModel(List[EmpleadoPersonalityDTO]),
    status_code=status.HTTP_200_OK,
)
def get_personalidades(
    respository: UtilsRepository = Depends(get_utils_repository),
):
    result = respository.get_personalidades_dto()
    return APIResponse(result)


if not configuration.in_aws:
    app.include_router(router)
app.include_router(router, prefix="/empresas", tags=["empresas"])
