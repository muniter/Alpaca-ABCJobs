from typing import List, Union
from fastapi import APIRouter, FastAPI, Response, status, Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateDTO,
    CandidatoCreateResponseDTO,
    CandidatoDatosAcademicosCreateDTO,
    CandidatoDatosAcademicosDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoDatosLaboralesDTO,
    CandidatoDatosLaboralesTipoDTO,
    CandidatoPersonalInformationDTO,
    CandidatoPersonalInformationUpdateDTO,
    CountryDTO,
    LenguajeDTO,
    RolHabilidadDTO,
)
from common.shared.config import configuration
from common.shared.api_models.gestion_usuarios import UsuarioCandidatoDTO
from common.shared.logger import logger
from common.shared.fastapi import shared_app_setup
from common.shared.jwt import get_request_user_candidato
from common.shared.api_models.shared import (
    ErrorBuilder,
    SuccessResponse,
    ErrorResponse,
)
from .candidato import (
    CandidatoService,
    CountryRepository,
    DatosAcademicosRepository,
    DatosAcademicosService,
    DatosLaboralesService,
    LenguajeRepository,
    RolesHabilidadesRepository,
    get_candidato_service,
    get_country_repository,
    get_datos_academicos_repository,
    get_datos_academicos_service,
    get_datos_laborales_service,
    get_lenguaje_repository,
    get_roles_habilidades_repository,
)

app = FastAPI(
    openapi_url="/candidatos/openapi.json",
    docs_url="/candidatos/docs",
)
router = APIRouter()
utils_router = APIRouter()
shared_app_setup(app, router)


@router.post(
    "/crear",
    response_model=Union[SuccessResponse[CandidatoCreateResponseDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def crear(
    data: CandidatoCreateDTO,
    response: Response,
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.crear(data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/personal-info",
    response_model=Union[
        SuccessResponse[CandidatoPersonalInformationDTO], ErrorResponse
    ],
    status_code=status.HTTP_200_OK,
)
def personal_info(
    data: CandidatoPersonalInformationUpdateDTO,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.update_informacion_personal(user.id_candidato, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/personal-info",
    response_model=Union[
        SuccessResponse[CandidatoPersonalInformationDTO], ErrorResponse
    ],
    status_code=status.HTTP_200_OK,
)
def get_personal_info(
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.get_informacion_personal(user.id_candidato)
    return SuccessResponse(data=result)


@router.post(
    "/work-info",
    response_model=Union[SuccessResponse[CandidatoDatosLaboralesDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def work_info(
    data: CandidatoDatosLaboralesCreateDTO,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.crear(user.id_candidato, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/work-info/{id}",
    response_model=Union[SuccessResponse[CandidatoDatosLaboralesDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def update_work_info(
    id: int,
    data: CandidatoDatosLaboralesCreateDTO,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.update(id, user.id_candidato, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.delete(
    "/work-info/{id}",
    response_model=Union[SuccessResponse[dict], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def delete_work_info(
    id: int,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.delete(id, user.id_candidato)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data={})


@router.post(
    "/academic-info",
    response_model=Union[SuccessResponse[CandidatoDatosAcademicosDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def academic_info(
    data: CandidatoDatosAcademicosCreateDTO,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.crear(user.id_candidato, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.post(
    "/academic-info/{id}",
    response_model=Union[SuccessResponse[CandidatoDatosAcademicosDTO], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def update_academic_info(
    data: CandidatoDatosAcademicosCreateDTO,
    id: int,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.update(id, user.id_candidato, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.delete(
    "/academic-info/{id}",
    response_model=Union[SuccessResponse[dict], ErrorResponse],
    status_code=status.HTTP_200_OK,
)
def delete_academic_info(
    id: int,
    response: Response,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.delete(id, user.id_candidato)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data={})


@utils_router.get(
    "/utils/countries",
    response_model=SuccessResponse[List[CountryDTO]],
    status_code=status.HTTP_200_OK,
)
def get_countries(
    repository: CountryRepository = Depends(get_country_repository),
):
    result = repository.get_all()
    return SuccessResponse(data=result)


@utils_router.get(
    "/utils/languages",
    response_model=SuccessResponse[List[LenguajeDTO]],
    status_code=status.HTTP_200_OK,
)
def get_languages(repository: LenguajeRepository = Depends(get_lenguaje_repository)):
    languages = repository.get_all()
    result = []
    for language in languages:
        result.append(LenguajeDTO(id=language.id, name=language.nombre))

    return SuccessResponse(data=result)


@utils_router.get(
    "/utils/skills",
    response_model=SuccessResponse[List[RolHabilidadDTO]],
    status_code=status.HTTP_200_OK,
)
def get_skills(
    repository: RolesHabilidadesRepository = Depends(get_roles_habilidades_repository),
):
    skills = repository.get()
    result = [skill.build_roles_habilidades_dto() for skill in skills]
    return SuccessResponse(data=result)


@utils_router.get(
    "/utils/title-types",
    response_model=SuccessResponse[List[CandidatoDatosLaboralesTipoDTO]],
    status_code=status.HTTP_200_OK,
)
def get_title_types(
    repository: DatosAcademicosRepository = Depends(get_datos_academicos_repository),
):
    tipos = repository.get_tipos()
    result = [tipo.build_dto() for tipo in tipos]
    return SuccessResponse(data=result)


if not configuration.in_aws:
    app.include_router(router)
    app.include_router(utils_router)

app.include_router(router, prefix="/candidatos", tags=["candidatos"])
app.include_router(utils_router, prefix="/candidatos", tags=["utils"])
